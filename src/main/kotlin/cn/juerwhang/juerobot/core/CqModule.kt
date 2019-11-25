package cn.juerwhang.juerobot.core

import cc.moecraft.icq.PicqBotX
import cc.moecraft.icq.event.IcqListener
import cn.juerwhang.juerobot.store.BaseTable
import java.lang.reflect.Constructor


typealias CqModuleConfigCallback<T> = T.() -> Unit
open class CqModule(
    val name: String,
    val summary: String,
    val enabled: Boolean,
    val asListener: Boolean = false
): IcqListener() {
    private val commandList = LinkedHashSet<FunctionalCommand>()
    open val tableDependencies: List<BaseTable<*>> = emptyList()

    open fun register(bot: PicqBotX) {
        this.tableDependencies.forEach { it.createTable() }

        if (this.enabled) {
            for (command in commandList) {
                bot.commandManager.registerCommand(command)
            }
            if (this.asListener) {
                bot.eventManager.registerListener(this)
            }
        }
    }

    private fun <TArg: FunctionalCommand, TBody> createCommand(
        commandConstructor: Constructor<TArg>,
        block: FunctionalCommandCreatorArgument.() -> TBody
    ) {
        val argument = FunctionalCommandCreatorArgument()
        val body = argument.block()

        if (argument.name == null) {
            throw IllegalArgumentException("Command's name cannot be null!")
        }

        this.commandList.add(commandConstructor.newInstance(
            this,
            argument.name!!,
            argument.summary?:"",
            (argument.alias?: emptyList()).toTypedArray(),
            body
        ))
    }

    fun createPrivateCommand(block: FunctionalCommandCreatorArgument.() -> FunctionalPrivateCommandBody)
            = createCommand(getCommandConstructor("PrivateCommand")!!, block)

    fun createGroupCommand(block: FunctionalCommandCreatorArgument.() -> FunctionalGroupCommandBody)
            = createCommand(getCommandConstructor("GroupCommand")!!, block)

    fun createDiscussCommand(block: FunctionalCommandCreatorArgument.() -> FunctionalDiscussCommandBody)
            = createCommand(getCommandConstructor("DiscussCommand")!!, block)

    fun createEverywhereCommand(block: FunctionalCommandCreatorArgument.() -> FunctionalEverywhereCommandBody)
            = createCommand(getCommandConstructor("EverywhereCommand")!!, block)

    open fun <SubType: CqModule> config(block: CqModuleConfigCallback<SubType>) {
        @Suppress("UNCHECKED_CAST")
        block(this as SubType)
    }
}
