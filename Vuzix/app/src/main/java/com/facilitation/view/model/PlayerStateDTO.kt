package com.facilitation.view.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PlayerStateDTO(
    @SerializedName("currentTrack") var currentTrack: TrackDTO,
    @SerializedName("playing") var isPlaying: Boolean,
    @SerializedName("shuffled") var isShuffled: Boolean
): Serializable
