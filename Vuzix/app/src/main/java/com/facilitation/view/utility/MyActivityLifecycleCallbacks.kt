package com.facilitation.view.utility

import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import android.util.Log
import java.io.Serializable

class MyActivityLifecycleCallbacks(currentActivity: Activity) : ActivityLifecycleCallbacks, Serializable {
    @Transient var currentActivity: Activity? = currentActivity

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {
        Log.i(p0::class.simpleName, "onCreate()")
        currentActivity = p0
    }

    override fun onActivityStarted(p0: Activity) {
        if (this.currentActivity == p0) {
            Log.i(p0::class.simpleName, "onStart()")
            currentActivity = p0
        }
    }

    override fun onActivityResumed(p0: Activity) {
        Log.i(p0::class.simpleName, "onResume()")
        currentActivity = p0
    }

    override fun onActivityPaused(p0: Activity) {
        Log.i(p0::class.simpleName, "onPause()")
    }

    override fun onActivityStopped(p0: Activity) {
        Log.i(p0::class.simpleName, "onStop()")
    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
        Log.i(p0::class.simpleName, "onSaveInstanceState()")
    }

    override fun onActivityDestroyed(p0: Activity) {
        Log.i(p0::class.simpleName, "onDestroy()")
        p0.application.unregisterActivityLifecycleCallbacks(this)
    }
}