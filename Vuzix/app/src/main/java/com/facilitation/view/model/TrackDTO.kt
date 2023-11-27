package com.facilitation.view.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class TrackDTO(
    @SerializedName("title") val title: String,
    @SerializedName("artist") val artist: String,
    @SerializedName("uri") val uri: String,
    @SerializedName("favorite") var isFavorite: Boolean,
    @Transient var isPlaying: Boolean
): Serializable