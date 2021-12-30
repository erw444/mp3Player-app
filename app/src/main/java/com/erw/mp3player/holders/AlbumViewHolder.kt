package com.erw.mp3player.holders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.erw.mp3player.R
import com.erw.mp3player.adapters.OnItemClickListener
import com.erw.mp3player.services.FileSystemScanService
import java.io.File

class AlbumViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private var listItemView: TextView? = null

    init {
        listItemView = itemView.findViewById(R.id.list_item_name)
    }

    fun bind(album: FileSystemScanService.Album, clickListener: OnItemClickListener) {
        listItemView!!.text = album.name

        itemView.setOnClickListener {
            clickListener.onItemClicked(album)
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