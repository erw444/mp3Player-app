package com.erw.mp3player.adapters

import android.R.attr
import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.erw.mp3player.domain.FileVO
import com.erw.mp3player.holders.FileViewHolder
import java.io.File
import android.R.attr.data




class FileAdapter(
    diffCallback: DiffUtil.ItemCallback<File>,
    private val files: Array<File>,
    val itemClickListener: OnItemClickListener
    ) : ListAdapter<File, RecyclerView.ViewHolder>(diffCallback){


    override fun getItemCount(): Int {
        return files.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return FileViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var file = files[position]
        if (holder is FileViewHolder) {
            (holder as FileViewHolder).bind(file, itemClickListener)
        }
    }

    class ListDiff : DiffUtil.ItemCallback<File>() {
        override fun areItemsTheSame(
            oldItem: File,
            newItem: File
        ): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(
            oldItem: File,
            newItem: File
        ): Boolean {
            return oldItem.name == newItem.name
        }
    }
}

interface OnItemClickListener{
    fun onItemClicked(file: File)
}