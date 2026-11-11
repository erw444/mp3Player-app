package com.erw.mp3player.services

import android.content.ContentUris
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import java.io.Serializable

object FileSystemScanService {

    var mp3s: List<MP3> = emptyList()
        private set
    var albumsToMp3s: Map<Album, List<MP3>> = emptyMap()
        private set

    data class MP3(
        val uri: String = "",
        val name: String = "",
        val duration: Int = 0,
        val size: Long = 0,
        val albumId: Long = -1,
        val albumName: String = "",
        val artist: String = "",
        val isMusic: Int = 0
    ) : Serializable

    data class Album(val name: String = "") : Serializable

    fun getMP3sFromAlbum(album: Album): List<MP3> = albumsToMp3s[album].orEmpty()

    fun getAlbumsToMP3sFromMediaStore(context: Context): Map<Album, List<MP3>> {
        albumsToMp3s = getMp3sFromMediaStore(context)
            .groupBy { Album(it.albumName) }
            .toSortedMap(compareBy { it.name })
        return albumsToMp3s
    }

    private val audioCollection
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }

    fun getMp3sFromMediaStore(context: Context): List<MP3> {
        val collection = audioCollection
        val result = ArrayList<MP3>()

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.IS_MUSIC
        )

        val selection = "${MediaStore.Audio.Media.IS_MUSIC} = 1 AND" +
                " ${MediaStore.Audio.Media.DISPLAY_NAME} NOT LIKE '%.wma'"
        val sortOrder = "${MediaStore.Audio.Media.DISPLAY_NAME} ASC"

        context.contentResolver.query(collection, projection, selection, null, sortOrder)
            ?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
                val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
                val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
                val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val isMusicColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.IS_MUSIC)

                while (cursor.moveToNext()) {
                    val contentUri = ContentUris.withAppendedId(collection, cursor.getLong(idColumn))
                    result += MP3(
                        uri = contentUri.toString(),
                        name = cursor.getString(nameColumn).orEmpty(),
                        duration = cursor.getInt(durationColumn),
                        size = cursor.getLong(sizeColumn),
                        albumId = cursor.getLong(albumIdColumn),
                        albumName = cursor.getString(albumColumn).orEmpty(),
                        artist = cursor.getString(artistColumn).orEmpty(),
                        isMusic = cursor.getInt(isMusicColumn)
                    )
                }
            }

        mp3s = result
        return result
    }
}
