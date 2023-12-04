package com.facilitation.view.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/** A serializable data transfer object model class for a music track.*/
class TrackDTO(
    @SerializedName("title") val title: String,
    @SerializedName("artist") val artist: String,
    @SerializedName("uri") val uri: String,
    @SerializedName("favorite") var isFavorite: Boolean
): Serializable