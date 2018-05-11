package com.github.pi15.AndroidAndArduino.Activities

import android.app.Activity
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import com.github.pi15.AndroidAndArduino.Frontend.GameView
import android.util.DisplayMetrics
import com.github.pi15.AndroidAndArduino.Interfaces.GameStateProvider
import com.github.pi15.AndroidAndArduino.Providers.GameEngine
import com.github.pi15.AndroidAndArduino.R
import java.io.InputStream


class MainActivity : Activity() {
    companion object {
        lateinit var ser : InputStream
    }

    lateinit var gsProvider : GameStateProvider
    lateinit var player : MediaPlayer

    private fun getDpHeight(): Int {
        val displayMetrics = resources.displayMetrics
        return Math.round(displayMetrics.heightPixels / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
    }

    private fun getPxHeight(): Int {
        val displayMetrics = resources.displayMetrics
        return displayMetrics.heightPixels
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dpHeight = getDpHeight()

        player = MediaPlayer.create(this, R.raw.km)
        player.setVolume(100f,100f)
        player.start()

        ser = resources.openRawResource(R.raw.sfd)
        val stream = resources.openRawResource(R.raw.km)

        gsProvider = GameEngine(stream, 4.0,15.0)
        gsProvider.start()
        setContentView(GameView(this, gsProvider, 200))
    }

    override fun onResume() {
        super.onResume()
        gsProvider.resume()
        if (!player.isPlaying)
            player.start()
    }

    override fun onPause() {
        super.onPause()
        gsProvider.pause()
        player.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        gsProvider.stop()
        player.stop()
        player.release()
    }
}
