package com.rittmann.common.extensions

import android.widget.Toast
import androidx.fragment.app.FragmentActivity

fun FragmentActivity.toast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    runOnUiThread {
        Toast.makeText(this, message, duration).show()
    }
}

fun FragmentActivity.getDeviceHeightInPercentage(percentage: Float): Int {
    val displayMetrics = resources?.displayMetrics
    return if (displayMetrics != null) {
        val toMultiply = percentage / 100f
        val pixels = displayMetrics.heightPixels * toMultiply
        pixels.toInt()
    } else 0
}