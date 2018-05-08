package com.github.pi15.AndroidAndArduino.Interfaces

import com.github.pi15.AndroidAndArduino.Containers.GameArrow

interface ArrowsProvider {
    /**
     * Are any arrows available now
     */
    fun anyArrowsAvaliable() : Boolean

    /**
     * Will the provider generate more arrows
     */
    fun willGenerateMoreArrows() : Boolean

    /**
     * All arrows list
     */
    val allArrows : List<GameArrow>

    /**
     * Returns the bottommost arrow at the given horizontal coordinate if any. Otherwise, returns null.
     */
    fun getBottommostArrowAtX(horizontalIndex : Int) : GameArrow?

    /**
     * Deletes the bottommost arrow at the giver horizontal coordinate
     */
    fun deleteBottommostArrowAtX(horizontalIndex : Int)

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
