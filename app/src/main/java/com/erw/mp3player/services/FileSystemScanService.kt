package com.erw.mp3player.services

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import java.io.File
import java.io.FileFilter
import java.util.*
import java.util.Arrays.sort

class FileSystemScanService {

    companion object {

        // Container for information about each video.
        data class MP3(val uri: Uri,
                       val name: String,
                       val duration: Int,
                       val size: Int,
                       val album: String,
                       val isMusic: Int,
                       val relativePath: String?,
                       val data: String,

        )

        fun getMP3sScopedStorage(context: Context) : List<MP3> {
            val mp3s = ArrayList<MP3>()

            val collection =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    MediaStore.Audio.Media.getContentUri(
                        MediaStore.VOLUME_EXTERNAL
                    )
                } else {
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }

            val projection = arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.IS_MUSIC,
                MediaStore.Audio.Media.RELATIVE_PATH,
                MediaStore.Audio.Media.DATA
            )

            // Show only videos that are at least 5 minutes in duration.
             val selection = "${MediaStore.Audio.Media.IS_MUSIC} = 1"
       /* val selectionArgs = arrayOf(

        )*/

            // Display videos in alphabetical order based on their display name.
            val sortOrder = "${MediaStore.Audio.Media.DISPLAY_NAME} ASC"

            val query = context.contentResolver.query(
                collection,
                projection,
                null,
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
                val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val isMusicColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.IS_MUSIC)
                val relativePathColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.RELATIVE_PATH)
                val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)

                while (cursor.moveToNext()) {
                    // Get values of columns for a given video.
                    val id = cursor.getLong(idColumn)
                    val name = cursor.getString(nameColumn)
                    val duration = cursor.getInt(durationColumn)
                    val size = cursor.getInt(sizeColumn)
                    val album = cursor.getString(albumColumn)
                    val isMusic = cursor.getInt(isMusicColumn)
                    val relativePath = cursor.getString(relativePathColumn)
                    val data = cursor.getString(dataColumn)

                    val contentUri: Uri = ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        id
                    )

                    // Stores column values and the contentUri in a local object
                    // that represents the media file.
                    mp3s += MP3(contentUri, name, duration, size, album,
                        isMusic, relativePath, data)
                }

                return mp3s
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

}