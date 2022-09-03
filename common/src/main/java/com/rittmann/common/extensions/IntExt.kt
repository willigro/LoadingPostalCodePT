package com.rittmann.common.extensions

fun Int?.orZero(): Int = this ?: 0

fun Int?.orIt(or:Int): Int = this ?: or