package cn.juerwhang.juerobot

import cc.moecraft.icq.PicqBotX
import cn.juerwhang.juerobot.core.CqModule
import cn.juerwhang.juerobot.core.CqModuleConfigCallback
import cn.juerwhang.juerobot.utils.logger
import java.lang.IllegalArgumentException

/**
 * 对 PicqBotX 进行封装，使用示例请参照 github 仓库：juerobot-testor
 */
@Suppress("UNUSED")
object Juerobot {
    private var bot: PicqBotX? = null
    val picqBotX: PicqBotX get() = bot?:throw IllegalArgumentException("lose PicqBotX object! You should call Juerobot.init(PicqBotX) first!")

    /**
     * 初始化 Juerobot，该步骤应当于 step.1 执行。
     * @param picqBotX 创建并配置好的的 PicqBotX 实例。
     * @param block Juerobot 的配置回调，请在此进行适当的个性化配置。
     *
     * 使用示例：
     * Juerobot.init(bot) {
     *   install(ErrorHandleModule)
     * }
     */
    fun init(picqBotX: PicqBotX, block: (JuerobotContext.() -> Unit)? = null) {
        this.bot = picqBotX
        logger.log("初始化 Juerobot...")
        block?.invoke(JuerobotContext)
    }

    /**
     * 请以此方法代替 PicqBotX 的 startBot，而不是直接调用 PicqBotX 的 startBot 方法。
     * 调用该函数将会自动进行模块与指令的注册，并启动机器人。
     */
    fun start() {
        for (module in JuerobotContext.installedModules) {
            module.register(picqBotX)
        }
        this.picqBotX.startBot()
    }

    object JuerobotContext {
        internal val installedModules = LinkedHashSet<CqModule>()

        /**
         * 该函数用于注册一个模块。
         * @param module 继承自 CqModule 的 Juerobot 模块。
         * @param config 配置该 CqModule 的回调函数。
         *
         * 使用示例：
         * Juerobot.init(bot) {
         *   // 注册错误处理模块。
         *   install(ErrorHandleModule) {
         *     // 个性化错误处理模块中的错误处理函数。
         *     errorHandlerFunction = {
         *       exception.printStackTrace()
         *     }
         *   }
         * }
         */
        fun <T: CqModule> install(module: T, config: CqModuleConfigCallback<T>? = null) {
            installedModules.add(module)
            config?.let { module.config(it) }
        }
    }
}
