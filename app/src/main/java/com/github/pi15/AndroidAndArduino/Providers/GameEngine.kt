package com.github.pi15.AndroidAndArduino.Providers

import com.github.pi15.AndroidAndArduino.Containers.ButtonEvent
import com.github.pi15.AndroidAndArduino.Containers.GameArrow
import com.github.pi15.AndroidAndArduino.Containers.GameState
import com.github.pi15.AndroidAndArduino.Interfaces.ArrowsProvider
import com.github.pi15.AndroidAndArduino.Interfaces.ButtonEventsProvider
import com.github.pi15.AndroidAndArduino.Interfaces.GameStateProvider
import kotlin.concurrent.thread

/**
 * borderCoordsY - интервал нажатия
 * arrowsCount - количество стрелок/кнопок
 */
class GameEngine(private val yBorderCoordinatesInDp: Pair<Double, Double>, private val arrowsCount: Int) : GameStateProvider {

    private var arrowsProvider: ArrowsProvider? = null
    private var buttonEventsProvider: ButtonEventsProvider? = null
    private var isStopped: Boolean = false
    private var isPaused: Boolean = false
    private var gameScore: Int = 0

    override val gameState: GameState
        get() = GameState(arrowsProvider!!.allArrows, gameScore, !arrowsProvider!!.willGenerateMoreArrows())


    override fun start() {
        isPaused = false
        isStopped = false
        if (arrowsProvider == null)
            arrowsProvider = TODO("not implemented")
        arrowsProvider?.start()
        if (buttonEventsProvider == null)
            buttonEventsProvider = TODO("not implemented")
        buttonEventsProvider?.start()

        thread { mainHandle() }.start()
    }

    override fun pause() {
        isPaused = true
        arrowsProvider?.pause()
        buttonEventsProvider?.pause()
    }

    override fun resume() {
        isPaused = false
        arrowsProvider?.resume()
        buttonEventsProvider?.resume()
        thread { mainHandle() }.start()
    }

    override fun stop() {
        isStopped = true
        arrowsProvider?.stop()
        buttonEventsProvider?.stop()
        arrowsProvider = null
        buttonEventsProvider = null
    }

    private fun mainHandle() {
        while (!isStopped && !isPaused && arrowsProvider!!.willGenerateMoreArrows()) {
            gameScore += getScore(getPressedButtons(), getPressableArrows())
        }
    }

    private fun getScore(buttons: ArrayList<ButtonEvent>, arrows: ArrayList<GameArrow>): Int {
        var result = 0
        for (button in buttons) {
            val theArrow = arrows.firstOrNull { a -> a.arrowHorisontalId == button.buttonId }
            if (theArrow == null) {
                result--
            } else {
                result++
                arrowsProvider!!.deleteBottommostArrowAtX(theArrow.arrowHorisontalId)
                arrows.remove(theArrow)
            }
        }
        return result
    }


    private fun getPressedButtons(): ArrayList<ButtonEvent> {
        val pressed: ArrayList<ButtonEvent> = ArrayList()

        for (i in 0..arrowsCount) {
            if (!buttonEventsProvider!!.anyEventsAvaliable())
                break
            pressed.add(buttonEventsProvider!!.popButtonEvent()!!)
        }
        return pressed
    }

    private fun getPressableArrows(): ArrayList<GameArrow> {
        val arrows: ArrayList<GameArrow> = ArrayList()

        for (i in 0..arrowsCount) {
            val arrow = arrowsProvider!!.getBottommostArrowAtX(i)
            if (arrow != null) {
                if (arrowOnBorder(arrow))
                    arrows.add(arrow)
                else if (arrowOutOfScreen(arrow)) {
                    arrowsProvider?.deleteBottommostArrowAtX(i)
                    gameScore--
                }
            }

        }
        return arrows
    }

    private fun arrowOnBorder(arrow: GameArrow): Boolean {
        if (yBorderCoordinatesInDp.first <= arrow.yCoordinateInDp - arrow.arrowRadiusInDp
                && yBorderCoordinatesInDp.second >= arrow.yCoordinateInDp +  arrow.arrowRadiusInDp)
            return true
        return false
    }

    private fun arrowOutOfScreen(arrow: GameArrow): Boolean {
        return (yBorderCoordinatesInDp.second < arrow.yCoordinateInDp +  arrow.arrowRadiusInDp)
    }
}
