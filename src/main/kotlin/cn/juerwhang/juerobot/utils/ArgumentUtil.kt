package cn.juerwhang.juerobot.utils


data class Arguments(
    var location: String = "localhost",
    var targetPort: Int = 56100,
    var socketPort: Int = 56101,
    var prefix: Array<String> = arrayOf(">", "》"),
    var debugMode: Boolean = false,
    var showSQL: Boolean = false
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Arguments

        if (location != other.location) return false
        if (targetPort != other.targetPort) return false
        if (socketPort != other.socketPort) return false
        if (!prefix.contentEquals(other.prefix)) return false
        if (debugMode != other.debugMode) return false
        if (showSQL != other.showSQL) return false

        return true
    }

    override fun hashCode(): Int {
        var result = location.hashCode()
        result = 31 * result + targetPort
        result = 31 * result + socketPort
        result = 31 * result + prefix.contentHashCode()
        result = 31 * result + debugMode.hashCode()
        result = 31 * result + showSQL.hashCode()
        return result
    }
}

fun getArguments(): Arguments {
    val result = Arguments()
    System.getProperties().entries.forEach {
        when(it.key.toString().toLowerCase()) {
            "location" -> result.location = it.value.toString().trim()
            "target.port" -> result.targetPort = it.value.toString().trim().toInt()
            "source.port" -> result.socketPort = it.value.toString().trim().toInt()
            "prefix" -> result.prefix = it.value.toString().replace("，", ",").split(",").toTypedArray()
            "debug" -> result.debugMode = true
            "showsql" -> result.showSQL = true
        }
    }
    return result
}

val args = getArguments()