package cn.juerwhang.juerobot.utils

import cc.moecraft.logger.HyLogger
import cc.moecraft.logger.LoggerInstanceManager
import cn.juerwhang.juerobot.Juerobot
import cn.juerwhang.juerobot.core.CqModule


val Juerobot.loggerFactory: LoggerInstanceManager by lazy {
    Juerobot.picqBotX.loggerInstanceManager
}
val Juerobot.logger: HyLogger by lazy {
    Juerobot.loggerFactory.getLoggerInstance("Juerobot", true)
}

val CqModule.logger: HyLogger
    get() = Juerobot.loggerFactory.getLoggerInstance(this.name, true)

fun getLogger(prefix: String, debugMode: Boolean = args.debugMode): HyLogger {
    return Juerobot.loggerFactory.getLoggerInstance(prefix, debugMode)
}
