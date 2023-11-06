package com.facilitation.view.utility.enums

enum class ActivityEnum {
    MAIN { override fun activityName(): String = "MainActivity"; override fun number(): Int = 0 },
    SPOTIFY { override fun activityName(): String = "SpotifyActivity"; override fun number(): Int = 1 },
    SPOTIFY_LIST { override fun activityName(): String = "SpotifyListActivity"; override fun number(): Int = 2 };
    abstract fun activityName(): String
    abstract fun number(): Int
}