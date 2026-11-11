package com.erw.mp3player

import android.content.ContentUris
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.widget.SeekBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.erw.mp3player.AlbumNavigationActivity.Companion.intentAlbumToPlay
import com.erw.mp3player.AlbumNavigationActivity.Companion.intentRandomPlay
import com.erw.mp3player.SongNavigationActivity.Companion.intentSongToPlay
import com.erw.mp3player.databinding.ActivityPlayMusicBinding
import com.erw.mp3player.services.FileSystemScanService
import com.erw.mp3player.services.FileSystemScanService.Album
import com.erw.mp3player.services.FileSystemScanService.MP3
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlayMusicActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayMusicBinding
    private var player: MediaPlayer? = null
    private var queue: List<MP3> = emptyList()
    private var queueIndex = 0
    private var randomize = false

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        binding = ActivityPlayMusicBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.root.applySystemBarInsets()

        binding.albumArt.setOnTouchListener(object : OnSwipeTouchListener(this) {
            override fun onSwipeLeft() = playNextSong()
        })
        binding.playBtn.setOnClickListener { togglePlayback() }
        binding.restartBtn.setOnClickListener { restart() }
        binding.SkipBtn.setOnClickListener { playNextSong() }
        binding.positionBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) player?.seekTo(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
        })

        lifecycleScope.launch {
            queue = withContext(Dispatchers.IO) { buildQueue() }
            if (queue.isEmpty()) finish() else playCurrent()
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                while (true) {
                    updateProgress()
                    delay(500)
                }
            }
        }
    }

    private fun buildQueue(): List<MP3> {
        val song = IntentCompat.getSerializableExtra(intent, intentSongToPlay, MP3::class.java)
        val album = IntentCompat.getSerializableExtra(intent, intentAlbumToPlay, Album::class.java)
        return when {
            album != null -> {
                if (FileSystemScanService.albumsToMp3s.isEmpty()) {
                    FileSystemScanService.getAlbumsToMP3sFromMediaStore(applicationContext)
                }
                FileSystemScanService.getMP3sFromAlbum(album)
            }
            song != null -> listOf(song)
            intent.hasExtra(intentRandomPlay) -> {
                randomize = true
                FileSystemScanService.getMp3sFromMediaStore(applicationContext).shuffled()
            }
            else -> emptyList()
        }
    }

    private fun playCurrent() {
        val mp3 = queue[queueIndex]
        val albumArt = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), mp3.albumId)
        binding.albumArt.setImageURI(albumArt)
        if (binding.albumArt.drawable == null) {
            binding.albumArt.setImageResource(R.drawable.ic_album_placeholder)
        }
        binding.songTitle.text = mp3.name

        player?.release()
        player = MediaPlayer.create(this, Uri.parse(mp3.uri))?.apply {
            setVolume(0.5f, 0.5f)
            setOnCompletionListener { advance(userInitiated = false) }
            binding.positionBar.max = duration
            start()
        }
        if (player == null) {
            advance(userInitiated = false)
            return
        }
        showPlaying(true)
    }

    private fun advance(userInitiated: Boolean) {
        val hasNext = queueIndex + 1 < queue.size
        when {
            hasNext -> queueIndex++
            randomize -> {
                queue = queue.shuffled()
                queueIndex = 0
            }
            else -> {
                if (!userInitiated) showPlaying(false)
                return
            }
        }
        playCurrent()
    }

    private fun playNextSong() {
        if (queue.isNotEmpty()) advance(userInitiated = true)
    }

    private fun restart() {
        player?.apply {
            seekTo(0)
            start()
        }
        showPlaying(true)
    }

    private fun togglePlayback() {
        val mp = player ?: return
        if (mp.isPlaying) mp.pause() else mp.start()
        showPlaying(mp.isPlaying)
    }

    private fun showPlaying(playing: Boolean) {
        binding.playBtn.setIconResource(if (playing) R.drawable.ic_pause else R.drawable.ic_play)
    }

    private fun updateProgress() {
        val mp = player ?: return
        val position = mp.currentPosition
        binding.positionBar.progress = position
        binding.elapsedTimeLabel.text = createTimeLabel(position)
        binding.remainingTimeLabel.text = createTimeLabel(mp.duration - position)
    }

    fun createTimeLabel(time: Int): String {
        val min = time / 1000 / 60
        val sec = time / 1000 % 60
        return "$min:${sec.toString().padStart(2, '0')}"
    }

    override fun onDestroy() {
        player?.release()
        player = null
        super.onDestroy()
    }
}
