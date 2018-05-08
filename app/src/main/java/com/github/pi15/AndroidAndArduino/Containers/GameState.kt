package com.github.pi15.AndroidAndArduino.Containers

/**
 * Current game state container. Contains information about all active arrows in the game and the current score
 */
class GameState(val arrows : List<GameArrow>, val score : Int, val gameFinished : Boolean)
