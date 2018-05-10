package com.github.pi15.AndroidAndArduino.Activities

import android.app.Activity
import android.content.Context
import android.os.Bundle
import com.github.pi15.AndroidAndArduino.Frontend.GameView
import android.util.DisplayMetrics
import android.view.WindowManager
import com.github.pi15.AndroidAndArduino.Interfaces.GameStateProvider


class MainActivity : Activity() {
    private fun getDpHeight(): Int {
        val displayMetrics = resources.displayMetrics
        return Math.round(displayMetrics.heightPixels / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dpHeight = getDpHeight()

        val gsProvider : GameStateProvider = TODO("GS")

        setContentView(GameView(this, gsProvider, 900))
    }

    override fun onResume() {
        super.onResume()

    }
}
