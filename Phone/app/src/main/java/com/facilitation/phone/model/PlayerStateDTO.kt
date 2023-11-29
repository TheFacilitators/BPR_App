package com.facilitation.phone.model

import com.google.gson.annotations.SerializedName

data class PlayerStateDTO(
    @SerializedName("currentTrack") var currentTrack: TrackDTO,
    @SerializedName("playing") var isPlaying: Boolean,
    @SerializedName("shuffled") var isShuffled: Boolean
)