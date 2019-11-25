package cn.juerwhang.juerobot.core

import cc.moecraft.icq.PicqBotX
import cc.moecraft.icq.event.IcqListener
import cc.moecraft.logger.format.AnsiColor
import cn.juerwhang.juerobot.store.BaseTable
import cn.juerwhang.juerobot.utils.logger
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
        logger.log("${AnsiColor.CYAN}>> ========${AnsiColor.RESET} 初始化模块${AnsiColor.CYAN} ======== <<")
        this.tableDependencies.forEach { it.createTable() }
        logger.log("${AnsiColor.CYAN}>> ========${AnsiColor.RESET} 初始化完毕${AnsiColor.CYAN} ======== <<")

        if (this.enabled) {
            logger.log("${AnsiColor.CYAN}>> ========${AnsiColor.RESET} 正在注册模块${AnsiColor.CYAN} ======== <<")
            logger.log("${AnsiColor.CYAN}>>${AnsiColor.YELLOW} 模块信息${AnsiColor.RESET}：$summary")
            if (this.asListener) {
                logger.log("${AnsiColor.CYAN}>>${AnsiColor.GREEN} 将模块注册为事件监听器！")
                bot.eventManager.registerListener(this)
            }
            for (command in commandList) {
                logger.log("${AnsiColor.CYAN}>>${AnsiColor.YELLOW} 注册命令${AnsiColor.RESET} [ ${AnsiColor.CYAN} ${command.type}${AnsiColor.YELLOW} ->${AnsiColor.RESET} ${command.properties().name} (${AnsiColor.WHITE} ${command.properties().alias.joinToString()}${AnsiColor.RESET} ) ]")
                bot.commandManager.registerCommand(command)
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
