package com.erw.mp3player.services

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import java.io.File
import java.io.Serializable
import java.util.Arrays.sort
import java.util.stream.Collectors

object FileSystemScanService {


    var mp3s: List<MP3>
    var albumsToMp3s: Map<Album, List<MP3>>

    // Container for information about each video.
    data class MP3(val uri: String = "",
                   val name: String = "",
                   val duration: Int = 0,
                   val size: Int = 0,
                   val albumId: Long = -1,
                   val albumName: String = "",
                   val artist: String = "",
                   val isMusic: Int = 0
                   ) : Serializable

    data class Album(val albumId: Long,
                     val name: String = ""
                     ) : Serializable

    init {
        mp3s = ArrayList<MP3>()
        albumsToMp3s = HashMap<Album, List<MP3>>()

    }

    fun getMP3sFromAlbum(album : Album) : List<MP3>{
        return albumsToMp3s.getOrDefault(album, ArrayList<MP3>())
    }

    fun getAlbumsToMP3sFromMediaStore(context: Context) : Map<Album, List<MP3>> {

        getMp3sFromMediaStore(context)

        albumsToMp3s = mp3s.stream().collect(
            Collectors.groupingBy { mp3 -> Album(mp3.albumId, mp3.albumName) }
        ).toSortedMap(compareBy { album -> album.name })

        return albumsToMp3s
    }

    fun getExternalStorage(): Uri{
        val externalStorageUri =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Audio.Media.getContentUri(
                    MediaStore.VOLUME_EXTERNAL
                )
            } else {
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            }

        return externalStorageUri
    }

    fun getMp3sFromMediaStore(context:Context) : List<MP3> {

        mp3s = ArrayList<MP3>()
        val collection = getExternalStorage()

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

        // Show only videos that are at least 5 minutes in duration.
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} = 1 AND" +
                " ${MediaStore.Audio.Media.DISPLAY_NAME} NOT LIKE'%.wma'"

        // Display videos in alphabetical order based on their display name.
        val sortOrder = "${MediaStore.Audio.Media.DISPLAY_NAME} ASC"

        val query = context.contentResolver.query(
            collection,
            projection,
            selection,
            null,
            sortOrder,
        )
        query?.use { cursor ->
            // Cache column indices.
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val nameColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
            val durationColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
            val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
            val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val isMusicColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.IS_MUSIC)

            while (cursor.moveToNext()) {
                // Get values of columns for a given video.
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val duration = cursor.getInt(durationColumn)
                val size = cursor.getInt(sizeColumn)
                val albumId = cursor.getLong(albumIdColumn)
                val album = cursor.getString(albumColumn)
                val artist = cursor.getString(artistColumn)
                val isMusic = cursor.getInt(isMusicColumn)

                val contentUri: Uri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                // Stores column values and the contentUri in a local object
                // that represents the media file.
                mp3s += MP3(contentUri.toString(), name, duration, size, albumId, album, artist,
                    isMusic)
            }
        }

        return mp3s
    }

    fun getMusicDirectory() : Array<File>{
        val location = "sdcard/Music"
        //val location = "storage/self/primary/Music/BadAss Boss Themes III/"

        val rootFile = File(location)
        val isFile = rootFile.isFile
        val isDirectory = rootFile.isDirectory
        val isHidden = rootFile.isHidden
        val files = rootFile.list()
        sort(files)
        var filesInDirectory = rootFile.listFiles()
        sort(filesInDirectory)
        return filesInDirectory
    }

    fun getFilesInDirectory(directoryPath: String?) : Array<File> {
        val directoryFile = File(directoryPath)
        val files = directoryFile.list()
        var filesInDirectory = directoryFile.listFiles()
        return filesInDirectory
    }

}