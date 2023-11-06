package com.facilitation.view.utility.enums

enum class ActivityEnum {
    MAIN { override fun name(): String = "MainActivity"; override fun number(): Int = 0 },
    SPOTIFY { override fun name(): String = "SpotifyActivity"; override fun number(): Int = 1 },
    SPOTIFY_LIST { override fun name(): String = "SpotifyListActivity"; override fun number(): Int = 2 };
    abstract fun name(): String
    abstract fun number(): Int
}