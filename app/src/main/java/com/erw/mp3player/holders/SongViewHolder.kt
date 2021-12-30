package com.erw.mp3player.holders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.erw.mp3player.R
import com.erw.mp3player.adapters.OnItemClickListener
import com.erw.mp3player.adapters.OnSongClickListener
import com.erw.mp3player.services.FileSystemScanService
import java.io.File

class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private var listItemView: TextView? = null

    init {
        listItemView = itemView.findViewById(R.id.list_item_name)
    }

    fun bind(song: FileSystemScanService.MP3, clickListener: OnSongClickListener) {
        listItemView!!.text = song.name

        itemView.setOnClickListener {
            clickListener.onItemClicked(song)
        }
    }

    companion object {
        fun create(parent: ViewGroup): SongViewHolder {
            val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.recyclerview_list_item, parent, false)
            return SongViewHolder(view)
        }
    }
}