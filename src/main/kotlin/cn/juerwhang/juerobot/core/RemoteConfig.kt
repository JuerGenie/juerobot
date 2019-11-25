@file:Suppress("UNUSED")
package cn.juerwhang.juerobot.core

import cn.juerwhang.juerobot.Juerobot
import cn.juerwhang.juerobot.store.BaseEntity
import cn.juerwhang.juerobot.store.BaseTable
import cn.juerwhang.juerobot.utils.toJson
import cn.juerwhang.juerobot.utils.toObject
import cn.juerwhang.juerobot.utils.usingDatabase
import me.liuwj.ktorm.dsl.*
import me.liuwj.ktorm.entity.findAll
import me.liuwj.ktorm.entity.findById
import me.liuwj.ktorm.entity.findList
import me.liuwj.ktorm.entity.findOne
import me.liuwj.ktorm.schema.boolean
import me.liuwj.ktorm.schema.long
import me.liuwj.ktorm.schema.varchar

open class NoImplementMap<Key, Value>: MutableMap<Key, Value> {
    override val entries: MutableSet<MutableMap.MutableEntry<Key, Value>> get() = mutableSetOf()
    override val keys: MutableSet<Key> get() = mutableSetOf()
    override val values: MutableCollection<Value> get() = mutableSetOf()
    override val size: Int get() = 0
    override fun containsKey(key: Key): Boolean = false
    override fun containsValue(value: Value): Boolean = false
    override fun get(key: Key): Value? = null
    override fun isEmpty(): Boolean = true
    override fun clear() = Unit
    override fun put(key: Key, value: Value): Value? = null
    override fun putAll(from: Map<out Key, Value>) = Unit
    override fun remove(key: Key): Value? = null
}

/**
 * rc = RemoteConfig，为 CqModule 添加一个远程配置委托。
 * @param defaultValue 默认值，如果未启用数据库，则永远返回该值。若启用数据库，且数据库中尚未有该配置，则使用该值作为该配置初始值。
 * @return 一个继承了 MutableMap 接口的委托对象。
 * @see NoImplementMap
 * @see RemoteConfigDelegate
 *
 * 使用方法：
 *
 * val ownerId: Long by rc(2695996944L)
 */
inline fun <reified Value: Any> CqModule.rc(defaultValue: Value): RemoteConfigDelegate<Value>
        = RemoteConfigDelegate(this, defaultValue, Value::class.java)

class RemoteConfigDelegate<Value: Any>(
    private val module: CqModule,
    private val defaultValue: Value,
    private val valueClass: Class<Value>
): NoImplementMap<String, Value>() {

    override fun get(key: String): Value? {
        return if (Juerobot.usingDatabase) {
            val result = RemoteConfigManagerModule.getConfig(module.id, key)?.value
            if (result == null) {
                RemoteConfigManagerModule.updateOrCreateConfig(module.id, key, defaultValue.toJson())
                defaultValue
            } else {
                result.toObject(valueClass)
            }
        } else {
            defaultValue
        }
    }

    override fun put(key: String, value: Value): Value? {
        return if (Juerobot.usingDatabase && RemoteConfigManagerModule.updateOrCreateConfig(module.id, key, value.toJson())) {
            value
        } else {
            null
        }
    }
}

object RemoteConfigs: BaseTable<RemoteConfig>("table_remote_configs") {
    val module by long("module").references(Modules) { it.module }
    val name by varchar("name").bindTo { it.name }
    val value by varchar("value").bindTo { it.value }
    val summary by varchar("summary").bindTo { it.summary }
}

interface RemoteConfig: BaseEntity<RemoteConfig> {
    var module: Module
    var name: String
    var value: String
    var summary: String?
}

object Modules: BaseTable<Module>("table_modules") {
    val name by varchar("name").bindTo { it.name }
    val enable by boolean("enable").bindTo { it.enable }
}

interface Module: BaseEntity<Module> {
    var name: String
    var enable: Boolean
}

/**
 * 模块配置细项表
 */
typealias ModuleConfigDetailMap = HashMap<String, String>
/**
 * 模块配置表
 */
typealias ModuleConfigMap = HashMap<Long, ModuleConfigDetailMap>
typealias ModuleIdCacheMap = HashMap<String, Long>
object RemoteConfigManagerModule: CqModule("remote-config-manager", "提供远程配置能力，不提供额外的任何功能。", true) {
    override val tableDependencies: List<BaseTable<*>>
        get() = listOf(
            Modules,
            RemoteConfigs
        )

    /**
     * 模块配置缓存
     */
    private val remoteConfigCache = ModuleConfigMap()
    private val moduleIdCache = ModuleIdCacheMap()

    fun getModules(): List<Module> {
        return Modules.findAll()
    }

    fun getModulesBySubName(subName: String): List<Module> {
        return Modules.findList { it.name like subName }
    }

    fun getModuleByName(name: String): Module? {
        return Modules.findOne { it.name eq name }
    }

    fun getOrCreateModuleByName(name: String): Module {
        return if (moduleIdCache.containsKey(name)) {
            Modules.findById(moduleIdCache[name]!!)!!
        } else {
            var result = getModuleByName(name)
            if (result == null) {
                val newId = Modules.insertAndGenerateKey {
                    Modules.name to name
                    Modules.enable to false
                }
                result = Modules.findById(newId)!!
            }
            moduleIdCache[name] = result.id
            result
        }
    }

    fun getModuleIdByName(name: String): Long {
        return if (moduleIdCache.containsKey(name)) {
            moduleIdCache[name]!!
        } else {
            getOrCreateModuleByName(name).id
        }
    }

    fun getConfigsByModule(module: Long): List<RemoteConfig> {
        return RemoteConfigs.findList { it.module eq module }
    }

    fun getConfig(module: Long, name: String): RemoteConfig? {
        return RemoteConfigs.findOne { it.module eq module and (it.name eq name) }
    }

    fun updateOrCreateConfig(module: Long, name: String, value: String = "null"): Boolean {
        val target = RemoteConfigs.findOne { it.module eq module and (it.name eq name) }
        return if (target == null) {
            RemoteConfigs.insertAndGenerateKey {
                RemoteConfigs.module to module
                RemoteConfigs.name to name
                RemoteConfigs.value to value
            }
            true
        } else {
            target.value = value
            target.flushChanges() > 0
        }
    }
}

val CqModule.id: Long get() = RemoteConfigManagerModule.getModuleIdByName(this.name)
