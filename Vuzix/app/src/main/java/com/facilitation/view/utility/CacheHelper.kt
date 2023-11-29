package com.facilitation.view.utility

import android.content.Context
import android.util.Log
import com.facilitation.view.model.PlayerStateDTO
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

    override fun setCachedPlayerState(context: Context, playerStateDTO: PlayerStateDTO) {
        val editor = context.getSharedPreferences("PlayerState", 0).edit()
        editor.putString("playerState", gson.toJson(playerStateDTO))
        editor.apply()
    }

    override fun getCachedPlayerState(context: Context): PlayerStateDTO? {
        return try {
            gson.fromJson(context.getSharedPreferences("PlayerState", 0).getString("playerState", null), PlayerStateDTO::class.java)
        } catch (e: NullPointerException) {
            Log.e("ERROR", "No player state saved in shared preferences.")
            null
        }
    }
//    override fun getCachedTrackDTO(context: Context): TrackDTO? {
//        return try {
//            val track = gson.fromJson(context.getSharedPreferences("LastSong", 0).getString("trackDTO", null), TrackDTO::class.java)
//            track
//        } catch (e: NullPointerException) {
//            Log.e("ERROR", "No track saved in shared preferences")
//            null
//        }
//    }

//    override fun setCachedTrackDTO(context: Context, track: TrackDTO) {
//        val editor = context.getSharedPreferences("LastSong", 0).edit()
//        editor.clear()
//        editor.putString("trackDTO", gson.toJson(track))
//        editor.putString("trackTitle", gson.toJson(track.title))
//        editor.putString("trackArtist", gson.toJson(track.artist))
//        editor.putString("trackUri", gson.toJson(track.uri))
//        editor.putBoolean("trackFavorite", track.isFavorite)
//        editor.putBoolean("trackPlaying", track.isPlaying)
//        editor.apply()
//    }

//    override fun getCachedTrackDTOUri(context: Context): String {
//        return try {
//            val uri = gson.fromJson(context.getSharedPreferences("LastSong", 0).getString("trackUri", null), String::class.java)
//            uri
//        } catch (e: NullPointerException) {
//            Log.e("ERROR", "No track saved in shared preferences")
//            ""
//        }
//    }
}