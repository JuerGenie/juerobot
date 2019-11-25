//package cn.juerwhang.juerobot.utils
//
//
//import cn.juerwhang.juerobot.utils.deleteLast
//import com.google.gson.Gson
//import org.apache.http.HttpException
//import org.apache.http.client.HttpClient
//import org.apache.http.client.methods.HttpGet
//import org.apache.http.client.methods.HttpUriRequest
//import org.apache.http.impl.client.HttpClientBuilder
//import org.apache.http.util.EntityUtils.toString
//
//var clientInstance: HttpClient? = null
//
//fun getHttpClient(): HttpClient {
//    if (clientInstance == null)
//        clientInstance = HttpClientBuilder.create().build()
//    return clientInstance!!
//}
//
//inline fun <reified T> doGet(url: String, vararg params: Pair<String, *>): T {
//    val client = getHttpClient()
//    val urlBuilder = StringBuilder(url)
//    if (!urlBuilder.contains("?")) {
//        urlBuilder.append("?")
//    }
//    if (params.isNotEmpty()) {
//        for (param in params) {
//            urlBuilder.append(param.first).append("=").append(param.second).append("&")
//        }
//        urlBuilder.deleteLast(1)
//    }
//
//    val request = HttpGet(urlBuilder.toString())
//    val response = client.execute(request as HttpUriRequest?)
//    if (response.statusLine.statusCode != 200) {
//        throw HttpException("请求[GET - %s]未成功: %d".format(url, response.statusLine.statusCode))
//    } else {
//        val json = Gson()
//        return json.fromJson<T>(toString(response.entity), T::class.java)
//    }
//}