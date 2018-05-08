package com.github.pi15.AndroidAndArduino.Interfaces

import com.github.pi15.AndroidAndArduino.Containers.GameState

interface GameStateProvider {
    val gameState : GameState

    /**
     * Starts the provider
     */
    fun start()

    /**
     * Pauses the provider
     */
    fun pause()

    /**
     * Resumes the provider
     */
    fun resume()

    /**
     * Stops the provider
     */
    fun stop()
}