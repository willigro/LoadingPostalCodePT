package com.rittmann.common.extensions

import android.content.Context
import androidx.core.content.ContextCompat

fun Context.getColorByRes(resIdColor: Int) = ContextCompat.getColor(this, resIdColor)