package com.rittmann.common.extensions

import android.view.View

fun View?.visible() {
    this?.visibility = View.VISIBLE
}

fun View?.invisible() {
    this?.visibility = View.INVISIBLE
}

fun View?.gone() {
    this?.visibility = View.GONE
}

fun View?.isGone(): Boolean {
    return this?.visibility == View.GONE
}

enum class StateField {
    DEFAULT, INVALID, VALID
}

fun Int.isResId() = this != -1

// Use it inside the post method
fun View?.heightPercentage(percentage: Int): Int {
    return (this?.height ?: 0) * percentage / 100
}
