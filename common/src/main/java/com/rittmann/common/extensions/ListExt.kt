package com.rittmann.common.extensions

interface ListableString {
    fun label(): String
}

fun List<ListableString>.convertToLabelingList(): List<String> {
    val arr = arrayListOf<String>()
    forEach {
        arr.add(it.label())
    }
    return arr
}

inline fun <T> List<T>.containsElementThat(predicate: (T) -> Boolean): Boolean {
    for (e in this) {
        if (predicate(e)) return true
    }
    return false
}