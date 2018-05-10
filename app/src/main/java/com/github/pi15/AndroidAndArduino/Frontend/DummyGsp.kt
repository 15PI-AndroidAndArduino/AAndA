package com.github.pi15.AndroidAndArduino.Frontend

import com.github.pi15.AndroidAndArduino.Containers.GameArrow
import com.github.pi15.AndroidAndArduino.Containers.GameState
import com.github.pi15.AndroidAndArduino.Interfaces.GameStateProvider
import java.util.*

class DummyGsp : GameStateProvider {
    override val gameState: GameState
        get() {
            val rnd = Random()
            val arrs = ArrayList<GameArrow>()
            for (i in 0 .. 6)
                arrs.add(GameArrow(rnd.nextInt(4), rnd.nextInt(700) * 1.0, 15.0))
            return GameState(arrs, rnd.nextInt(1024), false)
        }

    override fun start() {

    }

    override fun pause() {

    }

    override fun resume() {

    }

    override fun stop() {

    }

}