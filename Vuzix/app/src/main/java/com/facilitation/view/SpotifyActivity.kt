package com.facilitation.view

import android.app.Activity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.vuzix.hud.actionmenu.ActionMenuActivity


class SpotifyActivity : ActionMenuActivity() {
    var PlayPauseMenuItem: MenuItem? = null
    var NextSongMenuItem: MenuItem? = null
    var PrevSongMenuItem: MenuItem? = null
    var SongDetailsMenuItem: MenuItem? = null
    private var isPlaying = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spotify)
    }

    override fun onCreateActionMenu(menu: Menu): Boolean {
        super.onCreateActionMenu(menu)
        menuInflater.inflate(R.menu.menu_spotify, menu)
        PrevSongMenuItem = menu.findItem(R.id.menu_spotify_item1)
        PlayPauseMenuItem = menu.findItem(R.id.menu_spotify_item2)
        NextSongMenuItem = menu.findItem(R.id.menu_spotify_item3)
        SongDetailsMenuItem = menu.findItem(R.id.menu_spotify_item4)
        return true
    }

    override fun getDefaultAction(): Int {
        return 2
    }

    override fun alwaysShowActionMenu(): Boolean {
        return true
    }

    override fun setCurrentMenuItem(item: MenuItem?, animate: Boolean) {
        super.setCurrentMenuItem(item, animate)
    }

    fun previousSong(item: MenuItem?) {
        showToast("Previous Song!")
    }

    fun togglePlayPause(item: MenuItem?) {
        if (isPlaying) {
            showToast("Pause")
            item?.setIcon(R.drawable.ic_play)
        }
        else
        {
            showToast("Play")
            item?.setIcon(R.drawable.ic_pause)
        }
        isPlaying = !isPlaying
    }


    fun nextSong(item: MenuItem?) {
        showToast("Next Song")
    }

    fun showSongDetails(item: MenuItem?) {
        showToast("Total Eclipse of The Heart - Bonnie Tyler")
    }

    private fun showToast(text: String) {
        val activity: Activity = this
        activity.runOnUiThread { Toast.makeText(activity, text, Toast.LENGTH_SHORT).show() }
    }
}