package com.github.pi15.AndroidAndArduino.Frontend

import android.graphics.*
import android.view.SurfaceHolder
import com.github.pi15.AndroidAndArduino.Interfaces.GameStateProvider

internal class GameViewDrawer(private val surfaceHolder: SurfaceHolder,
                              val gsProvider : GameStateProvider,
                              val zoneHeight : Int,
                              val dpToPx : Float) : Thread() {
    private var runFlag = false
    private var prevTime: Long = 0

    val textPaint  = Paint()
    val rectPaint  = Paint()
    val arrowPaint = Array(4, { _ ->  Paint()})
    val arrowPaint2  = Paint()
    val arrowPaint3  = Paint()
    val arrowPaint4  = Paint()

    init {
        textPaint.textSize = 50f
        textPaint.color = Color.WHITE
        textPaint.style = Paint.Style.FILL

        rectPaint.color = Color.DKGRAY
        rectPaint.style = Paint.Style.FILL

        arrowPaint[0].color = Color.BLUE
        arrowPaint[0].style = Paint.Style.FILL
        arrowPaint[1].color = Color.GREEN
        arrowPaint[1].style = Paint.Style.FILL
        arrowPaint[2].color = Color.YELLOW
        arrowPaint[2].style = Paint.Style.FILL
        arrowPaint[3].color = Color.RED
        arrowPaint[3].style = Paint.Style.FILL
    }

    fun setRunning(run: Boolean) {
        runFlag = run
    }

    private fun draw(canvas: Canvas) {
        canvas.drawColor(Color.GRAY)

        canvas.drawRect(0 * 1f, canvas.height - zoneHeight * 1f,
                canvas.width * 1f, canvas.height * 1f, rectPaint)

        val state = gsProvider.gameState
        canvas.drawText(state.score.toString(), 25f, 60f, textPaint)

        val horisontalStep = canvas.width / 5f

        for (arrow in state.arrows) {
            canvas.drawCircle((1 + arrow.arrowHorisontalId) * horisontalStep,
                    (arrow.yCoordinateInDp * dpToPx).toFloat(),
                    (arrow.arrowRadiusInDp * dpToPx).toFloat(), arrowPaint[arrow.arrowHorisontalId])
        }
    }

    override fun run() {
        var canvas: Canvas?
        while (runFlag) {
            canvas = null
            try {
                // получаем объект Canvas и выполняем отрисовку
                canvas = surfaceHolder.lockCanvas(null)
                synchronized(surfaceHolder) {
                    draw(canvas)
                }
            }
            catch (ex: Exception) {
                // ignored
            }
            finally {
                if (canvas != null) {
                    // отрисовка выполнена. выводим результат на экран
                    surfaceHolder.unlockCanvasAndPost(canvas)
                }
            }
            sleep(33)
        }
    }
}