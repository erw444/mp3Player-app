package com.erw.mp3player.holders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.erw.mp3player.R
import com.erw.mp3player.adapters.OnAlbumItemClickListener
import com.erw.mp3player.services.FileSystemScanService

class AlbumViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private var listItemView: TextView? = null
    private var playButton: ImageView? = null

    init {
        listItemView = itemView.findViewById(R.id.list_item_name)
        playButton = itemView.findViewById(R.id.list_item_play_image)
    }

    fun bind(album: FileSystemScanService.Album, clickListener: OnAlbumItemClickListener) {
        listItemView!!.text = album.name

        itemView.setOnClickListener {
            clickListener.onAlbumClicked(album)
        }

        playButton?.setOnClickListener{
            clickListener.onPlayButtonClicked(album)
        }
    }

    companion object {
        fun create(parent: ViewGroup): AlbumViewHolder {
            val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.recyclerview_list_item, parent, false)
            return AlbumViewHolder(view)
        }
    }
}