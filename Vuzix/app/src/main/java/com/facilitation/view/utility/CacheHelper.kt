package com.facilitation.view.utility

import android.content.Context
import android.util.Log
import com.facilitation.view.model.TrackDTO
import com.facilitation.view.utility.interfaces.ICache
import com.google.gson.Gson

class CacheHelper: ICache {
    private val gson = Gson()
    override fun setCachedActivityLifecycleCallback(context: Context, callback: MyActivityLifecycleCallbacks) {
        val editor = context.getSharedPreferences("ActivityLifecycleCallback", 0).edit()
        editor.clear()
        editor.putString("callback", gson.toJson(callback))
        editor.apply()
    }

    override fun getCachedActivityLifecycleCallback(context: Context): MyActivityLifecycleCallbacks? {
        return try {
            gson.fromJson(context.getSharedPreferences("ActivityLifecycleCallback", 0).getString("callback", null), MyActivityLifecycleCallbacks::class.java)
        } catch (e: NullPointerException) {
            Log.i("INFO", "No custom activity lifecycle callback saved in shared preferences")
            null
        }
    }

    override fun setCachedTrackDTO(context: Context, track: TrackDTO) {
        val editor = context.getSharedPreferences("LastSong", 0).edit()
        editor.clear()
        editor.putString("trackDTO", gson.toJson(track))
        editor.putString("trackTitle", gson.toJson(track.title))
        editor.putString("trackArtist", gson.toJson(track.artist))
        editor.putString("trackUri", gson.toJson(track.uri))
        editor.putBoolean("trackFavorite", track.isFavorite)
        editor.putBoolean("trackPlaying", track.isPlaying)
        editor.apply()
    }

    override fun getCachedTrackDTO(context: Context): TrackDTO? {
        return try {
            val track = gson.fromJson(context.getSharedPreferences("LastSong", 0).getString("trackDTO", null), TrackDTO::class.java)
            // Getting the isPlaying boolean separately because it is marked as transient and therefore isn't serialized
            track.isPlaying = context.getSharedPreferences("LastSong", 0).getBoolean("trackPlaying", false)
            track
        } catch (e: NullPointerException) {
            Log.e("ERROR", "No track saved in shared preferences")
            null
        }
    }
}