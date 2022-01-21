package com.erw.mp3player

import android.annotation.SuppressLint
import android.content.ContentUris
import android.media.MediaPlayer
import android.net.Uri
import android.os.*
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.erw.mp3player.AlbumNavigationActivity.Companion.intentAlbumToPlay
import com.erw.mp3player.SongNavigationActivity.Companion.intentSongToPlay
import com.erw.mp3player.services.FileSystemScanService
import com.erw.mp3player.services.FileSystemScanService.Album
import com.erw.mp3player.services.FileSystemScanService.MP3

class PlayMusicActivity : AppCompatActivity() {

    private lateinit var mp: MediaPlayer
    private lateinit var playBtn: Button
    private lateinit var positionBar: SeekBar
    private lateinit var elapsedTimeLabel: TextView
    private lateinit var remainingTimeLabel: TextView
    private lateinit var albumArtView: ImageView
    private lateinit var songTitleView: TextView
    private var totalTime: Int = 0
    private var mp3s: List<MP3> = ArrayList<MP3>()
    private var toPlayMp3Id = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_play_music)

        var mp3: MP3 = MP3()
        if(intent.hasExtra(intentAlbumToPlay)){
            val album = intent.getSerializableExtra(intentAlbumToPlay) as Album
            mp3s = FileSystemScanService.getMP3sFromAlbum(album)
            mp3 = mp3s[toPlayMp3Id]
            toPlayMp3Id++

        } else if(intent.hasExtra(intentSongToPlay)){
            mp3 = intent.getSerializableExtra(intentSongToPlay) as MP3
        }

        songTitleView = findViewById(R.id.songTitle)
        albumArtView = findViewById(R.id.albumArt)
        positionBar = findViewById(R.id.positionBar)
        mp = createMediaPlayer(mp3)

        playBtn = findViewById(R.id.playBtn)
        playBtn.setBackgroundResource(R.drawable.stop)
        mp.start()

        Thread(Runnable {
            while(mp != null && !this.isFinishing){
               try {
                   var msg = Message()
                   msg.what = mp.currentPosition
                   handler.sendMessage(msg)
                   Thread.sleep(1000)
               } catch (e: InterruptedException){

               }
            }
        }).start()

    }

    private fun createMediaPlayer(mp3: MP3) : MediaPlayer{
        val songCover: Uri = Uri.parse("content://media/external/audio/albumart");
        val uriSongCover: Uri = ContentUris.withAppendedId(songCover, mp3.albumId)
        albumArtView.setImageURI(uriSongCover)
        songTitleView.setText(mp3.name)
        
        mp = MediaPlayer.create(this, android.net.Uri.parse(mp3.uri))
        mp.setVolume(0.5f, 0.5f)
        totalTime = mp.duration
        mp.setOnCompletionListener {
            Log.i("ONComplete Media palyer", "onComplete hit")

            if(!mp3s.isEmpty() && mp3s.size != toPlayMp3Id){
                var nextMp3 = mp3s[toPlayMp3Id]
                toPlayMp3Id++
                mp.release()
                mp = createMediaPlayer(nextMp3)
                mp.start()
            } else {
                playBtn.setBackgroundResource(R.drawable.play)
            }
        }

        positionBar.max = totalTime
        positionBar.setOnSeekBarChangeListener(
            object: SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if(fromUser) {
                        mp.seekTo(progress)
                    }
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
                }
            }
        )

        return mp
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (mp.isPlaying) {
            mp.stop()
        }
        mp.release()
        finish()
    }

    @SuppressLint("HandlerLeak")
    var handler = object: Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            var currentPosition = msg.what

            positionBar.progress = currentPosition

            var elapsedTime = createTimeLabel(currentPosition)
            elapsedTimeLabel = findViewById(R.id.elapsedTimeLabel)
            elapsedTimeLabel.text = elapsedTime

            var remainingTime = createTimeLabel(totalTime - currentPosition)
            remainingTimeLabel = findViewById(R.id.remainingTimeLabel)
            remainingTimeLabel.text = remainingTime
        }
    }

    fun createTimeLabel(time: Int): String {
        var timeLabel = ""
        var min  = time / 1000 / 60
        var sec = time / 1000 % 60

        timeLabel = "$min:"
        if (sec < 10) timeLabel += "0"
        timeLabel += sec

        return timeLabel
    }

    fun playBtnClick(v: View) {

        if (mp.isPlaying) {
            mp.pause()
            playBtn.setBackgroundResource(R.drawable.play)
        } else {
            mp.start()
            playBtn.setBackgroundResource(R.drawable.stop)
        }
    }
}