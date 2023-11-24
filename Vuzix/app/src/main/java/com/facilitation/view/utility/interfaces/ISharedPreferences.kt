package com.facilitation.view.utility.interfaces

import com.facilitation.view.model.TrackDTO

interface ISharedPreferences {
    fun updateSharedPreferences(track: TrackDTO)
    fun getSharedPreferencesTrackDTO(): TrackDTO?
}