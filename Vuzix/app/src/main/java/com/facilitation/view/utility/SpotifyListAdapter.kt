package com.facilitation.view.utility

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.facilitation.view.R
import com.facilitation.view.model.Track

class SpotifyListAdapter(private val trackList: List<Track>) : RecyclerView.Adapter<SpotifyListAdapter.TrackViewHolder>() {

    class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewName: TextView = itemView.findViewById(R.id.spotify_track_name)
        val textViewArtist: TextView = itemView.findViewById(R.id.spotify_track_artist)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.spotify_list, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = trackList[position]
        holder.textViewName.text = track.name
        holder.textViewArtist.text = track.artist
    }

    override fun getItemCount(): Int {
        return trackList.size
    }
}