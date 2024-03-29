package com.rittmann.common.tracker

import android.util.Log


const val TAG = "WTracker"
private const val UNKNOWN = "UNKNOWN"
private const val PROJECT_PATH = "com.rittmann"
private const val TRACKER_CLASS_PATH = "com.rittmann.common.tracker.TrackerKt"
const val MESSAGE_DIVISOR = ":::"

inline fun track(description: String? = "", block: () -> Unit) {
//    Thread.currentThread().stackTrace.forEachIndexed { index, stackTraceElement ->
//        Log.i("TRACKER", "$index::: ${stackTraceElement.className}")
//    }

    Log.d(
        TAG,
        "${getFromAndMethod()} ${compileDescription(description)}"
    )
    block()
}

inline fun <T> track(description: String? = "", block: () -> T): T {
    Log.d(TAG, "${getFromAndMethod()}${compileDescription(description)}")
    return block()
}

fun track(description: String? = "") {
    Log.d(TAG, "${getFromAndMethod()}${compileDescription(description)}")
}

fun track(error: Throwable) {
    Log.d(TAG, "${getFromAndMethod()}${compileDescription("Failure=${error.message}")}")
}

fun compileDescription(description: String?): String =
    if (description.isNullOrEmpty()) "" else "$MESSAGE_DIVISOR $description"

fun getFromAndMethod(): String {
    var from = UNKNOWN
    for (i in 0 until Thread.currentThread().stackTrace.size) {
        val name = Thread.currentThread().stackTrace[i].className
        if (name.contains(PROJECT_PATH) && name != TRACKER_CLASS_PATH) {
            from = name.split(".").let { it[it.lastIndex] } +
                    MESSAGE_DIVISOR +
                    Thread.currentThread().stackTrace[i].methodName
            break
        }
    }

    return from
}