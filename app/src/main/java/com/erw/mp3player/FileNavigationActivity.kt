package com.erw.mp3player

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.erw.mp3player.adapters.FileAdapter
import com.erw.mp3player.adapters.OnItemClickListener
import com.erw.mp3player.services.FileSystemScanService
import java.io.File

class FileNavigationActivity () : AppCompatActivity(), OnItemClickListener {


    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_file_navigation)

        val musicDirectories: Array<File>

        if(intent != null && intent.hasExtra("directory")) {
            var directoryPath = intent.getStringExtra("directory")
            musicDirectories = FileSystemScanService.getFilesInDirectory(directoryPath)
        } else {
            musicDirectories = FileSystemScanService.getMusicDirectory()
        }
        //val mp3s = FileSystemScanService.getMP3sScopedStorage(this)

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_list_view)
        var adapter = FileAdapter(FileAdapter.ListDiff(), musicDirectories, this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun onItemClicked(file: File) {
        if(file.isFile){
            val intent = Intent(this, PlayMusicActivity::class.java).apply {
                putExtra("fileToPlay", file)
            }
            startActivity(intent)
        } else {
            val intent = Intent(this, FileNavigationActivity::class.java).apply {
                putExtra("directory", file.path)
            }
            startActivity(intent)
        }

    }

}