package com.erw.mp3player.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.erw.mp3player.holders.AlbumViewHolder
import com.erw.mp3player.services.FileSystemScanService


class AlbumAdapter(
    diffCallback: DiffUtil.ItemCallback<FileSystemScanService.Album>,
    private val albums: Array<FileSystemScanService.Album>,
    val itemClickListener: OnItemClickListener
    ) : ListAdapter<FileSystemScanService.Album, RecyclerView.ViewHolder>(diffCallback){


    override fun getItemCount(): Int {
        return albums.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AlbumViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var album = albums[position]
        if (holder is AlbumViewHolder) {
            (holder as AlbumViewHolder).bind(album, itemClickListener)
        }
    }

    class ListDiff : DiffUtil.ItemCallback<FileSystemScanService.Album>() {
        override fun areItemsTheSame(
            oldItem: FileSystemScanService.Album,
            newItem: FileSystemScanService.Album
        ): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(
            oldItem: FileSystemScanService.Album,
            newItem: FileSystemScanService.Album
        ): Boolean {
            return oldItem.name == newItem.name
        }
    }
}

interface OnItemClickListener{
    fun onItemClicked(album: FileSystemScanService.Album)
}