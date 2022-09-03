package com.rittmann.common.datasource.sharedpreferences

import android.content.Context
import com.rittmann.common.constants.EMPTY_STRING

class SharedPreferencesModel(private val context: Context) {
    private fun getEditor() =
        context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)

    fun setNotificationId(value: String) =
        getEditor().edit().putString(NOTIFICATION_ID, value).apply()

    fun getNotificationId(): String =
        getEditor().getString(NOTIFICATION_ID, EMPTY_STRING) ?: EMPTY_STRING

    companion object {
        private const val EMPTY_JSON = "{}"
        private const val PREFERENCES = "my_preferences_wtest"
        private const val NOTIFICATION_ID = "NOTIFICATION_ID"
    }
}