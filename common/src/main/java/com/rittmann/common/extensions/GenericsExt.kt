package com.rittmann.common.extensions

fun <A, B> B.isNot(vararg that: A): Boolean {
    for (t in that)
        if (t == this)
            return false
    return true
}

fun <A, B> B.itIs(vararg that: A): Boolean {
    for (t in that)
        if (t != this)
            return false
    return true
}

fun <A, B> B.itCanBe(vararg that: A): Boolean {
    for (t in that)
        if (t == this)
            return true
    return false
}