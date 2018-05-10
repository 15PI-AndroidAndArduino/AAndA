package com.github.pi15.AndroidAndArduino.Frontend

import android.graphics.*
import android.view.SurfaceHolder
import com.github.pi15.AndroidAndArduino.Interfaces.GameStateProvider

internal class GameViewDrawer(private val surfaceHolder: SurfaceHolder,
                              val gsProvider : GameStateProvider,
                              val borderInPxYCoord : Int,
                              val dpToPx : Float) : Thread() {
    private var runFlag = false
    private var prevTime: Long = 0

    val textPaint  = Paint()
    val rectPaint  = Paint()
    val arrowPaint  = Paint()

    init {
        textPaint.textSize = 50f
        textPaint.color = Color.WHITE
        textPaint.style = Paint.Style.FILL

        rectPaint.color = Color.BLUE
        rectPaint.style = Paint.Style.FILL

        arrowPaint.color = Color.GREEN
        arrowPaint.style = Paint.Style.FILL
    }

    fun setRunning(run: Boolean) {
        runFlag = run
    }

    private fun draw(canvas: Canvas) {
        canvas.drawColor(Color.GRAY)

        canvas.drawRect(0 * 1f, borderInPxYCoord * 1f,
                canvas.width * 1f, canvas.height * 1f, rectPaint)

        val state = gsProvider.gameState
        canvas.drawText(state.score.toString(), 25f, 60f, textPaint)

        val horisontalStep = canvas.width / 4 - 100

        for (arrow in state.arrows) {
            canvas.drawCircle(arrow.arrowHorisontalId * horisontalStep + 50f,
                    (arrow.yCoordinateInDp * dpToPx).toFloat(),
                    (arrow.arrowRadiusInDp * dpToPx).toFloat(), arrowPaint)
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