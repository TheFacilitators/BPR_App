package com.facilitation.view.utility.interfaces

import android.content.Context
import com.facilitation.view.model.TrackDTO
import com.facilitation.view.utility.MyActivityLifecycleCallbacks

interface ICache {
    fun setCachedActivityLifecycleCallback(context: Context, callback: MyActivityLifecycleCallbacks)
    fun getCachedActivityLifecycleCallback(context: Context): MyActivityLifecycleCallbacks?
    fun setCachedTrackDTO(context: Context, track: TrackDTO)
    fun getCachedTrackDTO(context: Context): TrackDTO?
}