package com.erw.mp3player.services

import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.annotation.RequiresApi
import java.io.File
import java.nio.file.FileSystems

class FileSystemScanService {

    @RequiresApi(Build.VERSION_CODES.O)
    fun getMusicDirectory(){
        val location = "/storage/self/primary/Music"

        val rootFile = File(location)
        val isDirectory = rootFile.isDirectory
        val children = rootFile.list()

        for(dir in rootFile.list()){
            Log.d("Directory: ", dir.toString() + "/n")
        }


    }

}