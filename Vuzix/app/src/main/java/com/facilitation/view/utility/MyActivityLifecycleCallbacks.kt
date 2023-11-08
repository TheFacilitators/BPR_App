package com.facilitation.view.utility

import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import android.util.Log
import java.io.Serializable

class MyActivityLifecycleCallbacks(currentActivity: Activity) : ActivityLifecycleCallbacks, Serializable {
    @Transient var currentActivity: Activity? = currentActivity

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {
        currentActivity = p0
    }

    override fun onActivityStarted(p0: Activity) {
        currentActivity = p0
    }

    override fun onActivityResumed(p0: Activity) {
        currentActivity = p0
    }

    override fun onActivityPaused(p0: Activity) {
    }

    override fun onActivityStopped(p0: Activity) {
    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
    }

    override fun onActivityDestroyed(p0: Activity) {
        p0.application.unregisterActivityLifecycleCallbacks(this)
    }
}