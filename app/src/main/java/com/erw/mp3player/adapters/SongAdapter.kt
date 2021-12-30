package com.erw.mp3player.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.erw.mp3player.holders.AlbumViewHolder
import com.erw.mp3player.holders.SongViewHolder
import com.erw.mp3player.services.FileSystemScanService


class SongAdapter(
    diffCallback: DiffUtil.ItemCallback<FileSystemScanService.MP3>,
    private val songs: Array<FileSystemScanService.MP3>,
    val itemClickListener: OnSongClickListener
    ) : ListAdapter<FileSystemScanService.MP3, RecyclerView.ViewHolder>(diffCallback){


    override fun getItemCount(): Int {
        return songs.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return SongViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var song = songs[position]
        if (holder is SongViewHolder) {
            (holder as SongViewHolder).bind(song, itemClickListener)
        }
    }

    class ListDiff : DiffUtil.ItemCallback<FileSystemScanService.MP3>() {
        override fun areItemsTheSame(
            oldItem: FileSystemScanService.MP3,
            newItem: FileSystemScanService.MP3
        ): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(
            oldItem: FileSystemScanService.MP3,
            newItem: FileSystemScanService.MP3
        ): Boolean {
            return oldItem.name == newItem.name
        }
    }
}

interface OnSongClickListener{
    fun onItemClicked(song: FileSystemScanService.MP3)
}