package com.erw.mp3player

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.erw.mp3player.adapters.AlbumAdapter
import com.erw.mp3player.adapters.OnAlbumItemClickListener
import com.erw.mp3player.services.FileSystemScanService


class AlbumNavigationActivity () : AppCompatActivity(), OnAlbumItemClickListener {

    companion object {
            private const val STORAGE_PERMISSION_CODE = 100

            public const val intentAlbumToPlay = "albumToPlay"
    }

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

       checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE)

        setContentView(R.layout.activity_album_navigation)

        val albumsToMp3s = FileSystemScanService.getAlbumsToMP3sFromMediaStore(this)

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_list_view)
        var adapter = AlbumAdapter(AlbumAdapter.ListDiff(), albumsToMp3s.keys.toTypedArray(), this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun onAlbumClicked(album: FileSystemScanService.Album) {
        val intent = Intent(this, SongNavigationActivity::class.java).apply {
            putExtra("album", album)
        }

        startActivity(intent)
    }

    override fun onPlayButtonClicked(album: FileSystemScanService.Album) {
        val intent = Intent(this, PlayMusicActivity::class.java).apply {
            putExtra(intentAlbumToPlay, album)
        }

        startActivity(intent)
    }



    // Function to check and request permission.
    private fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Storage Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Storage Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

}