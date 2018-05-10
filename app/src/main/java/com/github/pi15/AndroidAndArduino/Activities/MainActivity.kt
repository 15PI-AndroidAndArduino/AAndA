package com.github.pi15.AndroidAndArduino.Activities

import android.app.Activity
import android.os.Bundle
import com.github.pi15.AndroidAndArduino.Frontend.GameView

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(GameView(this))
    }
}
