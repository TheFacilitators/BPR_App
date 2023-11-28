package com.facilitation.phone.model

import com.google.gson.annotations.SerializedName

data class TrackDTO(
    @SerializedName("title") var title: String,
    @SerializedName("artist") var artist: String,
    @SerializedName("uri") var uri: String,
    @SerializedName("favorite") var isFavorite: Boolean
)