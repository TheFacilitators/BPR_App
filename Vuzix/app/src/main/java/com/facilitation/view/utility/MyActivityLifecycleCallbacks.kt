package com.facilitation.view.utility

import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import java.io.Serializable

/** A serializable customized class for handling the logic of activity lifecycle callbacks.
 * @property currentActivity a nullable Activity  of the one currently being used.*/
class MyActivityLifecycleCallbacks(currentActivity: Activity) : ActivityLifecycleCallbacks, Serializable {
    @Transient var currentActivity: Activity? = currentActivity

    /** Called when the Activity calls super.onActivityCreated().
     * Sets the currentActivity from the argument.
     * @param activity the Activity from which this method was called.
     * @param savedInstanceState a Bundle containing the state of the Activity.*/
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        currentActivity = activity
    }

    /** Called when the Activity calls super.onActivityStarted().
     * Sets the currentActivity from the argument.
     * @param activity the Activity from which this method was called.*/
    override fun onActivityStarted(activity: Activity) {
        currentActivity = activity
    }

    /** Called when the Activity calls super.onActivityResumed().
     * Sets the currentActivity from the argument.
     * @param activity the Activity from which this method was called.*/
    override fun onActivityResumed(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, instanceState: Bundle) {
    }

    /** Called when the Activity calls super.onActivityDestroyed().
     * Unregisters this activity lifecycle callbacks from the argument Activity 'application'.
     * @param activity the Activity from which this method was called.*/
    override fun onActivityDestroyed(activity: Activity) {
        activity.application.unregisterActivityLifecycleCallbacks(this)
    }
}