package com.rittmann.common.components

import android.view.View
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import java.util.*
import kotlin.concurrent.schedule


open class EditTextSearch(
    private val editText: EditText,
    private val clearImage: View? = null,
    private val delay: Long = DELAY,
    private val callback: (String) -> Unit
) {
    private var timer: Timer? = null

    var onStart: (() -> Unit)? = null

    fun start() {
        val watcher = editText.doAfterTextChanged { value ->
            timer = newTimer()
            timer?.schedule(delay) {
                callback(value.toString())
            }
        }

        clearImage?.setOnClickListener {
            timer?.cancel()
            editText.removeTextChangedListener(watcher)
            editText.setText("")
            callback("")
            editText.addTextChangedListener(watcher)
        }
    }

    private fun newTimer(): Timer {
        timer?.cancel()
        return Timer(NAME, false).also {
            onStart?.invoke()
        }
    }

    companion object {
        const val NAME = "EditTextSearch timer"
        const val DELAY = 450L
    }
}