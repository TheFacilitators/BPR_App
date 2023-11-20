package com.facilitation.phone.model

import com.google.gson.annotations.SerializedName

data class TrackDTO(
    @SerializedName("title") val title: String,
    @SerializedName("artist") val artist: String,
    @SerializedName("uri") val uri: String,
    @SerializedName("favorite") var favorite: Boolean
)