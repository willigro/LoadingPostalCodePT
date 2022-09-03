package com.rittmann.common.extensions

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.linearLayoutManager() {
    layoutManager = LinearLayoutManager(
        context,
        RecyclerView.VERTICAL,
        false
    )
}