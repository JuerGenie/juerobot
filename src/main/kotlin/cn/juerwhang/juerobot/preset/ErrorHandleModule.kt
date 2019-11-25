package cn.juerwhang.juerobot.preset

import cc.moecraft.icq.event.EventHandler
import cc.moecraft.icq.event.events.local.EventLocalException
import cc.moecraft.icq.event.events.message.EventMessage
import cn.juerwhang.juerobot.core.CqModule
import cn.juerwhang.juerobot.utils.args
import cn.juerwhang.juerobot.utils.logger

object ErrorHandleModule: CqModule("错误处理模块", "用于处理错误，默认将错误输出至控制台。", true, true) {
    private val defaultErrorHandlerFunction: EventLocalException.() -> Unit = {
        exception.printStackTrace()
        if (args.debugMode) {
            val respondEvent = when {
                this is EventMessage -> this
                this.parentEvent is EventMessage -> this.parentEvent
                else -> null
            } as EventMessage?
            respondEvent?.respond("执行过程中发生了点儿问题，请查看日志以获取更多信息！")
        }
    }

    var errorHandlerFunction: EventLocalException.() -> Unit = {
        defaultErrorHandlerFunction()
    }

    @EventHandler
    fun errorHandler(event: EventLocalException) {
        logger.error("发生了异常：${event.exception.message}")
        event.errorHandlerFunction()
    }
}