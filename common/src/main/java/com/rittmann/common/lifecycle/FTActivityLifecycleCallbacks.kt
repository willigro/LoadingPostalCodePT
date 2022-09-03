package com.rittmann.common.lifecycle

import android.app.Activity
import android.os.Bundle

interface LifecycleApp {

    val mFTActivityLifecycleCallbacks: FTActivityLifecycleCallbacks
        get() = FTActivityLifecycleCallbacks()

    companion object {
        var lifecycleAppInstance: LifecycleApp? = null

        fun currentActivity(): Activity? {
            return lifecycleAppInstance?.mFTActivityLifecycleCallbacks?.currentActivity
        }
    }
}

class FTActivityLifecycleCallbacks : android.app.Application.ActivityLifecycleCallbacks {

    var currentActivity: Activity? = null

    override fun onActivityCreated(activity: Activity, p1: Bundle?) {
        currentActivity = activity
    }

    override fun onActivityStarted(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityResumed(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityPaused(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityStopped(p0: Activity) {}

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {}

    override fun onActivityDestroyed(p0: Activity) {}
}
