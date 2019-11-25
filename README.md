# Juerobot - 基于 [PicqBoxX](https://github.com/HyDevelop/PicqBotX) 进行二次封装的开发框架

## Juerobot 是什么

> 一个经过二次封装的 [coolq-http-api](https://github.com/richardchien/coolq-http-api) CQ plugin 开发框架，能够进行基于 [coolq-http-api](https://github.com/richardchien/coolq-http-api) 的 CQ 插件开发。
>
> 因为一些执拗的原因，所以这个项目中会充斥大量lambda的使用😀。
>
> 使用该框架可进行模块化开发，按模块提供功能，用户可按需引用。

## Juerobot 怎么用

1. 创建一个 Juerobot 并启动:
```kotlin
fun main(vararg args: String) {
    val config = PicqConfig(56101)

    config.isDebug = true
    val bot = PicqBotX(config)
    bot.enableCommandManager("$")
    bot.addAccount("jg-bot", "localhost", 56100)

    Juerobot.init(bot)
    Juerobot.start()
}
```

2. 注册一个 CqModule 以使用其功能:
```kotlin
Juerobot.init(bot) {
    // 在这个地方注册模块
    install(RemoteConfigManagerModule)
    // 还可以对模块进行个性化
    install(ErrorHandleModule) {
        errorHandlerFunction = {
            val respondEvent = when {
                this is EventMessage -> this
                this.parentEvent is EventMessage -> this.parentEvent
                else -> null
            } as EventMessage?
            respondEvent?.respond("执行过程中发生了点儿问题，请查看日志以获取更多信息！ <- 这是覆盖了默认异常处理动作的信息哦！")
        }
    }
}
```

3. 编写一个 Juerobot 模块（CqModule）:
```kotlin
object TestModule: CqModule("test-module", "示例模块，这是一个合格的复读姬模块哦！", true, true) {
    // 使用远程配置
    var replayMode by rc(false)
    init {
        // 创建群消息指令
        createGroupCommand {
            // 设置指令名称，用户使用 prefix<name|alias> [args...] 的方式进行调用
            name = "开始复读"
            summary = "用于开启复读模式。"
            // 返回该命令的执行体，返回文本作为响应
            {
                replayMode = true
                "已经打开复读模式了哦！接下来，我会复读你们所有人的话！"
            }
        }
    
        createGroupCommand {
            name = "停止复读"
            summary = "用户关闭复读模式"
    
            {
                replayMode = false
                "复读模式已关闭，kira~★"
            }
        }
    }
    // 添加一个事件处理函数，该注册方式与 PicqBotX 一致。
    @EventHandler
    fun replay(event: EventGroupMessage) {
        if (this.replayMode && event.message != "开始复读" && event.message != "停止复读") {
            event.respond("[CQ:at,qq=${event.senderId}]，你发送了：${event.message}")
        }
    }
}
```
相关示例代码可在 [juerobot-testor](https://github.com/juergenie/juerobot-testor) 查看。
