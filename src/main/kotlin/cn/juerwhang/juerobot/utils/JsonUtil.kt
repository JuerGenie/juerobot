package cn.juerwhang.juerobot.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder

val prettyGson: Gson = GsonBuilder()
    .setPrettyPrinting()
    .serializeNulls()
    .setDateFormat("yyyy-MM-dd HH:mm:ss")
    .create()

val defaultGson: Gson = GsonBuilder()
    .serializeNulls()
    .setDateFormat("yyyy-MM-dd HH:mm:ss")
    .create()

inline fun <reified T> String.toObject(): T {
    return defaultGson.fromJson(this, T::class.java)
}

fun <T> String.toObject(clazz: Class<T>): T {
    return defaultGson.fromJson(this, clazz)
}

fun Any.toJson(prettyOutput: Boolean = false): String {
    return (if (prettyOutput) prettyGson else defaultGson).toJson(this)
}
