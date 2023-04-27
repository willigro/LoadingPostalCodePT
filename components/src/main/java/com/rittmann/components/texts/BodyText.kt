package com.rittmann.components.texts

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.AttrRes
import com.google.android.material.textview.MaterialTextView
import com.rittmann.components.R

class BodyText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = R.attr.bodyTextStyle
) : MaterialTextView(context, attrs, defStyleAttr)