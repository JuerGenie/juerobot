package cn.juerwhang.juerobot.core

import cc.moecraft.icq.command.CommandProperties
import cc.moecraft.icq.command.interfaces.*
import cc.moecraft.icq.event.events.message.EventDiscussMessage
import cc.moecraft.icq.event.events.message.EventGroupMessage
import cc.moecraft.icq.event.events.message.EventMessage
import cc.moecraft.icq.event.events.message.EventPrivateMessage
import cc.moecraft.icq.user.Group
import cc.moecraft.icq.user.GroupUser
import cc.moecraft.icq.user.User
import java.lang.reflect.Constructor


open class FunctionalCommand(
    val parentModule: CqModule,
    val type: String,
    val name: String,
    val summary: String,
    vararg val alias: String
): IcqCommand {
    override fun properties(): CommandProperties {
        return CommandProperties(name, *alias)
    }
}

data class FunctionalCommandCreatorArgument(
    var name: String? = null,
    var alias: List<String>? = null,
    var summary: String? = null
)

// ==== private command start ====
data class PrivateCommandArgument(
    val event: EventPrivateMessage,
    val sender: User,
    val command: String,
    val args: List<String>
)
typealias FunctionalPrivateCommandBody = (PrivateCommandArgument) -> String
class FunctionalPrivateCommand(
    parentModule: CqModule,
    name: String,
    summary: String,
    alias: Array<out String>,
    val commandBody: FunctionalPrivateCommandBody
): FunctionalCommand(parentModule, "PrivateCommand", name, summary, *alias), PrivateCommand {

    override fun privateMessage(
        event: EventPrivateMessage?,
        sender: User?,
        command: String?,
        args: ArrayList<String>?
    ): String = commandBody(PrivateCommandArgument(event!!, sender!!, command!!, args?: arrayListOf()))
}
// ==== private command end ====

// ==== group command start ====
data class GroupCommandArgument(
    val event: EventGroupMessage,
    val group: Group,
    val sender: GroupUser,
    val command: String,
    val args: List<String>
)
typealias FunctionalGroupCommandBody = (GroupCommandArgument) -> String
class FunctionalGroupCommand(
    parentModule: CqModule,
    name: String,
    summary: String,
    alias: Array<out String>,
    val commandBody: FunctionalGroupCommandBody
): FunctionalCommand(parentModule, "GroupCommand", name, summary, *alias), GroupCommand {
    override fun groupMessage(
        event: EventGroupMessage?,
        sender: GroupUser?,
        group: Group?,
        command: String?,
        args: java.util.ArrayList<String>?
    ): String = commandBody(GroupCommandArgument(event!!, group!!, sender!!, command!!, args?: arrayListOf()))
}
// ==== group command end ====

// ==== discuss command start ====
data class DiscussCommandArgument(
    val event: EventDiscussMessage,
    val group: Group,
    val sender: GroupUser,
    val command: String,
    val args: List<String>
)
typealias FunctionalDiscussCommandBody = (DiscussCommandArgument) -> String
class FunctionalDiscussCommand(
    parentModule: CqModule,
    name: String,
    summary: String,
    alias: Array<out String>,
    val commandBody: FunctionalDiscussCommandBody
): FunctionalCommand(parentModule, "DiscussCommand", name, summary, *alias), DiscussCommand {
    override fun discussMessage(
        event: EventDiscussMessage?,
        sender: GroupUser?,
        discuss: Group?,
        command: String?,
        args: java.util.ArrayList<String>?
    ): String = commandBody(DiscussCommandArgument(event!!, discuss!!, sender!!, command!!, args?: arrayListOf()))
}
// ==== discuss command end ====

// ==== everywhere command start ====
data class EverywhereCommandArgument(
    val event: EventMessage,
    val sender: User,
    val command: String,
    val args: List<String>
)
typealias FunctionalEverywhereCommandBody = (EverywhereCommandArgument) -> String
class FunctionalEverywhereCommand(
    parentModule: CqModule,
    name: String,
    summary: String,
    alias: Array<out String>,
    val commandBody: FunctionalEverywhereCommandBody
): FunctionalCommand(parentModule, "EverywhereCommand", name, summary, *alias), EverywhereCommand {
    override fun run(
        event: EventMessage?,
        sender: User?,
        command: String?,
        args: java.util.ArrayList<String>?
    ): String = commandBody(EverywhereCommandArgument(event!!, sender!!, command!!, args?: arrayListOf()))
}
// ==== everywhere command end ====

private val constructorMapper = mapOf<String, Constructor<out FunctionalCommand>>(
    "PrivateCommand" to FunctionalPrivateCommand::class.java.getConstructor(
        CqModule::class.java,
        String::class.java,
        String::class.java,
        Array<out String>::class.java,
        Function1::class.java),
    "GroupCommand" to FunctionalGroupCommand::class.java.getConstructor(
        CqModule::class.java,
        String::class.java,
        String::class.java,
        Array<String>::class.java,
        Function1::class.java),
    "DiscussCommand" to FunctionalDiscussCommand::class.java.getConstructor(
        CqModule::class.java,
        String::class.java,
        String::class.java,
        Array<String>::class.java,
        Function1::class.java),
    "EverywhereCommand" to FunctionalEverywhereCommand::class.java.getConstructor(
        CqModule::class.java,
        String::class.java,
        String::class.java,
        Array<String>::class.java,
        Function1::class.java)
)

fun getCommandConstructor(type: String) = constructorMapper[type]
