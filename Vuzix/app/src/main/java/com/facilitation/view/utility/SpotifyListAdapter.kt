package com.facilitation.view.utility

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.facilitation.view.R
import com.facilitation.view.model.TrackDTO

class SpotifyListAdapter(var trackDisplayList: List<TrackDTO>) : RecyclerView.Adapter<SpotifyListAdapter.TrackViewHolder>() {

    class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewTitle: TextView = itemView.findViewById(R.id.spotify_track_title)
        val textViewArtist: TextView = itemView.findViewById(R.id.spotify_track_artist)
        val buttonViewControls: Button = itemView.findViewById(R.id.spotify_track_controls_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.spotify_list, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = trackDisplayList[position]
        holder.textViewTitle.text = track.title
        holder.textViewArtist.text = track.artist
        holder.buttonViewControls.isVisible = track.isPlaying
    }

    override fun getItemCount(): Int {
        return trackDisplayList.size
    }
}