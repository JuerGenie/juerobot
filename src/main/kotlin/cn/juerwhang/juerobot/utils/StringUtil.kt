package cn.juerwhang.juerobot.utils

fun StringBuilder.deleteLast(length: Int): StringBuilder {
    val last = this.length - 1
    this.delete(last - length, last)
    return this
}

fun splitToPair(arg: String): Pair<String, String?>? {
    var result: Pair<String, String?>? = null
    if (arg.isNotBlank()) {
        val array = arg.split("=")
        val first = array[0].trim()
        var second: String? = null
        if (array.size > 1) {
            second = array[1].trim()
        }
        result = Pair(first, second)
    }
    return result
}

fun String.asTemplate(args: Map<String, String>): String {
    var result = this
    val regex = Regex("(?!\\\\)&.+?&")
    for (matchStr in regex.findAll(result)) {
        val temp = matchStr.value
        result = result.replace(temp, args.getOrDefault(temp.substring(1, temp.length - 1), ""))
    }
    return result
}

fun String.asTemplate(vararg args: Pair<String, String>): String {
    return this.asTemplate(mapOf(*args))
}
