package com.rittmann.common.datasource.sharedpreferences

import android.content.Context
import com.rittmann.androidtools.log.log
import com.rittmann.common.constants.EMPTY_STRING

class SharedPreferencesModel(private val context: Context) {
    private fun getEditor() =
        context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)

    fun setNotificationId(value: String) =
        getEditor().edit().putString(NOTIFICATION_ID, value).apply()

    fun getNotificationId(): String =
        getEditor().getString(NOTIFICATION_ID, EMPTY_STRING) ?: EMPTY_STRING

    fun downloadWasConcluded() {
        getEditor().edit().clear().putBoolean(DOWNLOAD_CONCLUDED, true).apply()
        "downloadWasConcluded ${getIsDownloadConcluded()}".log()
    }

    fun getIsDownloadConcluded(): Boolean = getEditor().getBoolean(DOWNLOAD_CONCLUDED, false)

    companion object {
        private const val EMPTY_JSON = "{}"
        private const val PREFERENCES = "my_preferences_wtest"
        private const val NOTIFICATION_ID = "NOTIFICATION_ID"
        private const val DOWNLOAD_CONCLUDED = "DOWNLOAD_CONCLUDED"
    }
}