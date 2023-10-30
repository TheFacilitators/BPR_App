package com.facilitation.view.model

import com.google.gson.annotations.SerializedName

class TrackDTO(
    @SerializedName("title") val title: String,
    @SerializedName("artist") val artist: String,
    @SerializedName("uri") val uri: String
)