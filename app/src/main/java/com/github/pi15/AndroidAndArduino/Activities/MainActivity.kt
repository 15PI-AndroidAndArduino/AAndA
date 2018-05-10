package com.github.pi15.AndroidAndArduino.Activities

import android.app.Activity
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

        ser = resources.openRawResource(R.raw.sfd)
        val stream = resources.openRawResource(R.raw.km)

        gsProvider = GameEngine(stream, 4.0,15.0)
        gsProvider.start()
        setContentView(GameView(this, gsProvider, 200))
    }

    override fun onResume() {
        super.onResume()
        gsProvider.resume()
    }

    override fun onPause() {
        super.onPause()
        gsProvider.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        gsProvider.stop()
    }
}
