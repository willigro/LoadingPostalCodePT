package com.rittmann.common.extensions

import java.math.BigDecimal

fun BigDecimal?.toDoubleValid(): Double {
    if (this == null) {
        return 0.0
    }

    return try {
        toDouble()
    } catch (e: Exception) {
        0.0
    }
}