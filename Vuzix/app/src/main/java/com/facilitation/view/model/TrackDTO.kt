package com.facilitation.view.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/** A serializable data transfer object model class for a music track.
 * @constructor
 * @param title a string containing the title of the track.
 * @param artist a string containing the artist(s) of the track.
 * @param uri a string containing the URI of the track.
 * @param isFavorite a boolean indicating whether the track is a favorite.*/
class TrackDTO(
    @SerializedName("title") val title: String,
    @SerializedName("artist") val artist: String,
    @SerializedName("uri") val uri: String,
    @SerializedName("favorite") var isFavorite: Boolean
): Serializable