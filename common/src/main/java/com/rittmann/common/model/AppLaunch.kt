package com.rittmann.common.model

/**
 * App launch information about a specific [packageName]
 */
data class AppLaunch(
    val packageName: String,
    val launchPoints: List<LaunchPoint>,
) {

    /**
     * Class that holds an app launch point information.
     */
    data class LaunchPoint(
        val entryPoint: EntryPoint,
        val iid: String,
    )

    /**
     * App specific entry points.
     */
    enum class EntryPoint {
        UNKNOWN,
        POKE_LIST,
    }
}