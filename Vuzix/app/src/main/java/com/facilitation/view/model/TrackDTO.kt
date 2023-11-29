package com.facilitation.view.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class TrackDTO(
    @SerializedName("title") var title: String,
    @SerializedName("artist") var artist: String,
    @SerializedName("uri") var uri: String,
    @SerializedName("favorite") var isFavorite: Boolean,
    // Marked transient so this variable doesn't need to be on the server side as well
    @Transient var isPlaying: Boolean
): Serializable