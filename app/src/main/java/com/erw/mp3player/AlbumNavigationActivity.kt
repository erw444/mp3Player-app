package com.erw.mp3player

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.erw.mp3player.adapters.AlbumAdapter
import com.erw.mp3player.adapters.OnAlbumItemClickListener
import com.erw.mp3player.databinding.ActivityAlbumNavigationBinding
import com.erw.mp3player.services.FileSystemScanService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AlbumNavigationActivity : AppCompatActivity(), OnAlbumItemClickListener {

    companion object {
        const val intentAlbumToPlay = "albumToPlay"
        const val intentRandomPlay = "randomPlay"

        val audioPermission: String =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Manifest.permission.READ_MEDIA_AUDIO
            } else {
                Manifest.permission.READ_EXTERNAL_STORAGE
            }
    }

    private lateinit var binding: ActivityAlbumNavigationBinding

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) loadAlbums() else Toast.makeText(this, R.string.storage_permission_denied, Toast.LENGTH_LONG).show()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        binding = ActivityAlbumNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.root.applySystemBarInsets()

        binding.contentFileNavigation.recyclerListView.layoutManager = LinearLayoutManager(this)
        binding.randomizeFab.setOnClickListener {
            startActivity(Intent(this, PlayMusicActivity::class.java).putExtra(intentRandomPlay, true))
        }

        if (hasAudioPermission()) loadAlbums() else permissionLauncher.launch(audioPermission)
    }

    private fun hasAudioPermission() =
        ContextCompat.checkSelfPermission(this, audioPermission) == PackageManager.PERMISSION_GRANTED

    private fun loadAlbums() {
        lifecycleScope.launch {
            val albums = withContext(Dispatchers.IO) {
                FileSystemScanService.getAlbumsToMP3sFromMediaStore(applicationContext).keys
            }
            binding.contentFileNavigation.recyclerListView.adapter =
                AlbumAdapter(AlbumAdapter.ListDiff(), albums.toTypedArray(), this@AlbumNavigationActivity)
        }
    }

    override fun onAlbumClicked(album: FileSystemScanService.Album) {
        startActivity(Intent(this, SongNavigationActivity::class.java).putExtra("album", album))
    }

    override fun onPlayButtonClicked(album: FileSystemScanService.Album) {
        startActivity(Intent(this, PlayMusicActivity::class.java).putExtra(intentAlbumToPlay, album))
    }
}
