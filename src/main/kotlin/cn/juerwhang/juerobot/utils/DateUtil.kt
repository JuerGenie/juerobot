package cn.juerwhang.juerobot.utils

import java.time.LocalDateTime

const val DATE_FORMAT_TEMPLATE = "%s年%s月%s日"
const val TIME_FORMAT_TEMPLATE = "%s时%s分%s秒"

fun LocalDateTime.formatDate(): String {
    return DATE_FORMAT_TEMPLATE.format(this.year, this.monthValue, this.dayOfMonth)
}

fun LocalDateTime.formatTime(): String {
    return TIME_FORMAT_TEMPLATE.format(this.hour, this.minute, this.second)
}

fun LocalDateTime.formatDateTime(): String {
    return "%s %s".format(this.formatDate(), this.formatTime())
}

fun LocalDateTime.isToday(): Boolean {
    val now = LocalDateTime.now()
    return this.year == now.year && this.dayOfYear == now.dayOfYear
}
