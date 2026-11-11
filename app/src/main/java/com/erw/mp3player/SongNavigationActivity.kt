package com.erw.mp3player

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.erw.mp3player.adapters.OnSongClickListener
import com.erw.mp3player.adapters.SongAdapter
import com.erw.mp3player.databinding.ActivityAlbumNavigationBinding
import com.erw.mp3player.services.FileSystemScanService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SongNavigationActivity : AppCompatActivity(), OnSongClickListener {

    companion object {
        const val intentSongToPlay = "songToPlay"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val binding = ActivityAlbumNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.root.applySystemBarInsets()
        binding.randomizeFab.hide()

        val recyclerView = binding.contentFileNavigation.recyclerListView
        recyclerView.layoutManager = LinearLayoutManager(this)

        val album = IntentCompat.getSerializableExtra(
            intent, "album", FileSystemScanService.Album::class.java
        ) ?: return
        binding.screenTitle.text = album.name

        lifecycleScope.launch {
            val mp3s = withContext(Dispatchers.IO) { loadAlbumSongs(album) }
            recyclerView.adapter = SongAdapter(SongAdapter.ListDiff(), mp3s.toTypedArray(), this@SongNavigationActivity)
        }
    }

    // The cache is empty if the process was recreated, so rescan in that case.
    private fun loadAlbumSongs(album: FileSystemScanService.Album): List<FileSystemScanService.MP3> {
        if (FileSystemScanService.albumsToMp3s.isEmpty()) {
            FileSystemScanService.getAlbumsToMP3sFromMediaStore(applicationContext)
        }
        return FileSystemScanService.getMP3sFromAlbum(album)
    }

    override fun onItemClicked(mp3: FileSystemScanService.MP3) {
        startActivity(Intent(this, PlayMusicActivity::class.java).putExtra(intentSongToPlay, mp3))
    }
}
