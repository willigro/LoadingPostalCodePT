package com.rittmann.components.buttons

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.AttrRes
import com.google.android.material.button.MaterialButton
import com.rittmann.components.R


class MediumButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = R.attr.mediumButtonStyle
) : MaterialButton(context, attrs, defStyleAttr)