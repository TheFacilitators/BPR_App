package com.facilitation.view.utility

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.facilitation.view.R
import com.facilitation.view.model.TrackDTO

/** Model class for a customized RecyclerView Adapter.
 * @constructor
 * @param trackDisplayList a List of TrackDTOs to populate the RecyclerView with.*/
class SpotifyListAdapter(var trackDisplayList: List<TrackDTO>) : RecyclerView.Adapter<SpotifyListAdapter.TrackViewHolder>() {

    /** Model class for a customized RecyclerView ViewHolder.
     * @constructor
     * @param itemView the View in which class instances will be portrayed.
     * @property textViewTitle a string of a title to be displayed in a TextView.
     * @property textViewArtist a string of an artist to be displayed in a TextView.*/
    class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewTitle: TextView = itemView.findViewById(R.id.spotify_track_title)
        val textViewArtist: TextView = itemView.findViewById(R.id.spotify_track_artist)
    }

    /** Called when RecyclerView needs a new ViewHolder for an item.
     * @param parent the parent group to which the new ViewHolder will belong.
     * @param viewType an integer specifying which kind of View it is.
     * @return an empty TrackViewHolder instance.*/
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.spotify_list, parent, false)
        return TrackViewHolder(view)
    }

    /** Binds the argument ViewHolder properties to the instance variables of the item in the given
     * position in trackDisplayList.
     * @param holder the TrackViewHolder whose properties should be set.
     * @param position an integer of where in the RecyclerView this particular item is.*/
    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = trackDisplayList[position]
        holder.textViewTitle.text = track.title
        holder.textViewArtist.text = track.artist
    }

    /** Getter for the number of items in trackDisplayList.
     * @return an integer size.*/
    override fun getItemCount(): Int {
        return trackDisplayList.size
    }
}