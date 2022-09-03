package com.rittmann.common.extensions

import android.app.Activity
import androidx.activity.result.ActivityResult

fun ActivityResult.isOk() = this.resultCode == Activity.RESULT_OK