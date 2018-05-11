package com.github.pi15.AndroidAndArduino.Providers

import android.net.rtp.AudioStream
import com.github.pi15.AndroidAndArduino.Containers.ButtonEvent
import com.github.pi15.AndroidAndArduino.Containers.GameArrow
import com.github.pi15.AndroidAndArduino.Containers.GameState
import com.github.pi15.AndroidAndArduino.Interfaces.ArrowsProvider
import com.github.pi15.AndroidAndArduino.Interfaces.ButtonEventsProvider
import com.github.pi15.AndroidAndArduino.Interfaces.GameStateProvider
import java.io.InputStream
import java.util.*
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import java.util.logging.Handler
import kotlin.concurrent.thread
import kotlin.concurrent.timer
import kotlin.math.abs
import kotlin.math.max

/**
 * deflection - разница между шириной границы и диаметром стрелки
 * arrowRadius - радиус стрелок
 */
class GameEngine(private val audioStream: InputStream, private val deflection : Double, private val arrowRadius: Double) : GameStateProvider {

    private var arrowsProvider: ArrowsProvider? = null
    private var buttonEventsProvider: ButtonEventsProvider? = null
    private var gameScore: Int = 0
    private val arrowsCount: Int = 4
    private var correctSequence: Int = 0
    private lateinit var timer: Timer
    private val updatePeriod: Long = 50
    private var isPaused = false
    private val lock = ReentrantLock()

    override val gameState: GameState
        get() = GameState(arrowsProvider!!.allArrows, gameScore,
                !arrowsProvider!!.willGenerateMoreArrows() && !arrowsProvider!!.anyArrowsAvaliable())

    override fun start() {
        arrowsProvider = ArrowsManager(audioStream, arrowRadius,
                arrowRadius*2 + max(arrowRadius, deflection), 2.5)
        buttonEventsProvider = ButtonProviderClass()

        arrowsProvider?.start()
        buttonEventsProvider?.start()

        timer = timer(period = updatePeriod) {
            mainHandle()
        }
    }

    override fun pause() {
        isPaused = true
        timer.cancel()
        arrowsProvider?.pause()
        buttonEventsProvider?.pause()
    }

    override fun resume() {
        if (!isPaused) return

        isPaused = false
        arrowsProvider?.resume()
        buttonEventsProvider?.resume()
        timer = timer(period = updatePeriod) {
            mainHandle()
        }
    }

    override fun stop() {
        timer.cancel()
        arrowsProvider?.stop()
        buttonEventsProvider?.stop()
        arrowsProvider = null
        buttonEventsProvider = null

    }

    private fun mainHandle() {
        if (!arrowsProvider!!.willGenerateMoreArrows() && !arrowsProvider!!.anyArrowsAvaliable()) {
            timer.cancel()
            buttonEventsProvider?.stop()
            arrowsProvider?.stop()
        } else {
            val res = getScore(getPressedButtons(), getPressableArrows())
            lock.lock()
            gameScore+=res
            lock.unlock()
        }
    }

    private fun getScore(buttons: ArrayList<ButtonEvent>, arrows: ArrayList<GameArrow>): Int {
        var result = 0
        var wasMistake = false
        for (button in buttons) {
            val theArrow = arrows.firstOrNull { a -> a.arrowHorisontalId == button.buttonId }
            if (theArrow == null) {
                result--
                wasMistake = true
            } else {
                result++
                arrowsProvider!!.deleteBottommostArrowAtX(theArrow.arrowHorisontalId)
                arrows.remove(theArrow)
            }
        }
        if (!wasMistake) {
            correctSequence += result
            if (correctSequence != 0 && correctSequence % 10 == 0)
                result += 5
        }
        return result
    }


    private fun getPressedButtons(): ArrayList<ButtonEvent> {
        val pressed: ArrayList<ButtonEvent> = ArrayList()

        for (i in 0..(arrowsCount - 1)) {
            if (!buttonEventsProvider!!.anyEventsAvaliable())
                break
            pressed.add(buttonEventsProvider!!.popButtonEvent()!!)
        }
        return pressed
    }

    private fun getPressableArrows(): ArrayList<GameArrow> {
        val arrows: ArrayList<GameArrow> = ArrayList()

        for (i in 0..(arrowsCount - 1)) {
            val arrow = arrowsProvider!!.getBottommostArrowAtX(i)
            if (arrow != null) {
                if (arrowOnBorder(arrow))
                    arrows.add(arrow)
                else if (arrowOutOfScreen(arrow)) {
                    arrowsProvider?.deleteBottommostArrowAtX(i)
                    lock.lock()
                    gameScore--
                    lock.unlock()
                }
            }

        }
        return arrows
    }

    private fun arrowOnBorder(arrow: GameArrow): Boolean {
        return (abs(arrow.yCoordinateInDp) <= deflection)
    }

    private fun arrowOutOfScreen(arrow: GameArrow): Boolean {
        correctSequence = 0
        return arrow.yCoordinateInDp > deflection
    }
}
