package com.rittmann.common.extensions

fun Double?.orZero(): Double {
    return this ?: 0.0
}