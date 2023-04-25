package com.rittmann.datasource.workmanager

const val DOWNLOAD_STATUS_KEY = "DOWNLOAD_STATUS_KEY"

enum class WorkProgressState(val value: Int) {
    DONE(1), ON_GOING(0)
}