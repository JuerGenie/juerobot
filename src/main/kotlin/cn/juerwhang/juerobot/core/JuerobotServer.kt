//package cn.juerwhang.juerobot.core
//
//import io.ktor.application.Application
//import io.ktor.http.content.defaultResource
//import io.ktor.http.content.resources
//import io.ktor.http.content.static
//import io.ktor.http.content.staticBasePackage
//import io.ktor.routing.routing
//import io.ktor.server.engine.ApplicationEngine
//import io.ktor.server.engine.embeddedServer
//import io.ktor.server.netty.Netty
//import io.ktor.websocket.WebSocketServerSession
//import io.ktor.websocket.webSocket
//import java.util.*
//
//fun createServer(port: Int): JuerobotServer {
//    return JuerobotServer(port)
//}
//
//class JuerobotServer internal constructor(val port: Int) {
//    lateinit var webserver: ApplicationEngine
//
//    fun init(otherInit: Application.() -> Unit) {
//        webserver = embeddedServer(port = port, factory = Netty) {
//            routing {
//                static {
//                    defaultResource("index.html", "static")
//                    resources("static")
//                }
//
//                webSocket("/api") {
//
//                }
//
//                webSocket("/event") {
//
//                }
//            }
//
//            otherInit()
//        }
//    }
//
//    fun start() {
//        webserver.start()
//    }
//}
//
//class JuerobotClient(private val session: WebSocketServerSession) {
//    private val requestHeader = session.call.request.headers
//    val id = requestHeader["X-Self-ID"]
//    val token = requestHeader["Authorization"]
//
////    val eventList = LinkedList<>
//
//
//    fun receive() {
//
//    }
//}
