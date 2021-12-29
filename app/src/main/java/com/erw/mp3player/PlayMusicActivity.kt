package com.erw.mp3player

import android.annotation.SuppressLint
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.*
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class PlayMusicActivity : AppCompatActivity() {

    private lateinit var mp: MediaPlayer
    private lateinit var volumeBar: SeekBar
    private lateinit var positionBar: SeekBar
    private lateinit var elapsedTimeLabel: TextView
    private lateinit var remainingTimeLabel: TextView
    private var totalTime: Int = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_play_music)

        val file = intent.getSerializableExtra("fileToPlay")
        val mp3 : File = file as File
        mp = MediaPlayer.create(this, Uri.fromFile(mp3))
        mp.isLooping = true
        mp.setVolume(0.5f, 0.5f)
        totalTime = mp.duration

        volumeControlStream = AudioManager.STREAM_MUSIC

        positionBar = findViewById(R.id.positionBar)
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

        val playBtn: Button = findViewById(R.id.playBtn)
        mp.start()
        playBtn.setBackgroundResource(R.drawable.stop)

        Thread(Runnable {
            while(mp != null){
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
        val playBtn: Button = findViewById(R.id.playBtn)
        if (mp.isPlaying) {
            mp.pause()
            playBtn.setBackgroundResource(R.drawable.play)
        } else {
            mp.start()
            playBtn.setBackgroundResource(R.drawable.stop)
        }
    }
}