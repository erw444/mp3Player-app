package com.erw.mp3player.holders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.erw.mp3player.R
import com.erw.mp3player.adapters.OnItemClickListener
import java.io.File

class FileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private var listItemView: TextView? = null

    init {
        listItemView = itemView.findViewById(R.id.list_item_name)
    }

    fun bind(file: File, clickListener: OnItemClickListener) {
        listItemView!!.text = file.name

        itemView.setOnClickListener {
            clickListener.onItemClicked(file)
        }
    }

    companion object {
        fun create(parent: ViewGroup): FileViewHolder {
            val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.recyclerview_list_item, parent, false)
            return FileViewHolder(view)
        }
    }
}