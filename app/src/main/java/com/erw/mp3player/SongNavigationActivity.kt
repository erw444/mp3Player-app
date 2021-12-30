package com.erw.mp3player

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.erw.mp3player.adapters.OnSongClickListener
import com.erw.mp3player.adapters.SongAdapter
import com.erw.mp3player.services.FileSystemScanService
import java.io.File

class SongNavigationActivity: AppCompatActivity(), OnSongClickListener {

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        //TODO Change
        setContentView(R.layout.activity_album_navigation)

        val musicDirectories: Array<File>
        var mp3s : List<FileSystemScanService.MP3> = ArrayList()
        if(intent != null && intent.hasExtra("album")) {
            var album = intent.getSerializableExtra("album") as FileSystemScanService.Album
            val albumsToMp3s = FileSystemScanService.getAlbumsToMP3sFromMediaStore(this)
             mp3s = albumsToMp3s[album]!!

        } else {

        }

        //TODO Create Song Adapter
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_list_view)
        var adapter = SongAdapter(SongAdapter.ListDiff(), mp3s.toTypedArray(), this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun onItemClicked(mp3: FileSystemScanService.MP3) {
        val intent = Intent(this, PlayMusicActivity::class.java).apply {
            putExtra("fileToPlay", mp3)
        }
        startActivity(intent)


    }
}