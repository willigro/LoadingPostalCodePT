package com.rittmann.common.datasource.sharedpreferences

import android.content.Context
import com.rittmann.common.constants.EMPTY_STRING

class SharedPreferencesModel(private val context: Context) {
    private fun getEditor() =
        context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)

    fun setDownloadPostalCodeNotificationId(value: String) =
        getEditor().edit().putString(DOWNLOAD_POSTAL_CODE_NOTIFICATION_ID, value).apply()

    fun getDownloadPostalCodeNotificationId(): String =
        getEditor().getString(DOWNLOAD_POSTAL_CODE_NOTIFICATION_ID, EMPTY_STRING) ?: EMPTY_STRING

    fun setRegisterPostalCodeNotificationId(value: String) =
        getEditor().edit().putString(REGISTER_POSTAL_CODE_NOTIFICATION_ID, value).apply()

    fun getRegisterPostalCodeNotificationId(): String =
        getEditor().getString(REGISTER_POSTAL_CODE_NOTIFICATION_ID, EMPTY_STRING) ?: EMPTY_STRING

    fun downloadWasConcluded() {
        getEditor().edit().putBoolean(DOWNLOAD_POSTAL_CODE_CONCLUDED, true).apply()
    }

    fun isDownloadConcluded(): Boolean = getEditor().getBoolean(DOWNLOAD_POSTAL_CODE_CONCLUDED, false)

    fun registerPostalCodeWasConcluded() {
        getEditor().edit().putBoolean(REGISTER_POSTAL_CODE_CONCLUDED, true).apply()
    }

    fun isRegisterPostalCodeConcluded(): Boolean = getEditor().getBoolean(REGISTER_POSTAL_CODE_CONCLUDED, false)

    fun setRegisterPostalCodePeriodicId(value: String) =
        getEditor().edit().putString(REGISTER_POSTAL_CODE_PERIODIC_ID, value).apply()

    fun getRegisterPostalCodePeriodicId(): String =
        getEditor().getString(REGISTER_POSTAL_CODE_PERIODIC_ID, EMPTY_STRING) ?: EMPTY_STRING

    fun setDownloadPostalCodePeriodicId(value: String) =
        getEditor().edit().putString(DOWNLOAD_POSTAL_CODE_PERIODIC_ID, value).apply()

    fun getDownloadPostalCodePeriodicId(): String =
        getEditor().getString(DOWNLOAD_POSTAL_CODE_PERIODIC_ID, EMPTY_STRING) ?: EMPTY_STRING

    companion object {
        private const val EMPTY_JSON = "{}"
        private const val PREFERENCES = "my_preferences_wtest"
        private const val DOWNLOAD_POSTAL_CODE_NOTIFICATION_ID = "DOWNLOAD_POSTAL_CODE_NOTIFICATION_ID"
        private const val REGISTER_POSTAL_CODE_NOTIFICATION_ID = "REGISTER_POSTAL_CODE_NOTIFICATION_ID"
        private const val DOWNLOAD_POSTAL_CODE_CONCLUDED = "DOWNLOAD_CONCLUDED"
        private const val REGISTER_POSTAL_CODE_CONCLUDED = "REGISTER_CONCLUDED"
        private const val REGISTER_POSTAL_CODE_PERIODIC_ID = "REGISTER_POSTAL_CODE_PERIODIC_ID"
        private const val DOWNLOAD_POSTAL_CODE_PERIODIC_ID = "DOWNLOAD_POSTAL_CODE_PERIODIC_ID"
    }
}