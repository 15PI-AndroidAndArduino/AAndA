package com.github.pi15.AndroidAndArduino.Frontend

import android.content.res.Resources
import android.graphics.*
import android.view.SurfaceHolder


internal class GameViewDrawer(private val surfaceHolder: SurfaceHolder, resources: Resources) : Thread() {
    private var runFlag = false
    private var prevTime: Long = 0

    init {
        // сохраняем текущее время
        prevTime = System.currentTimeMillis()
    }

    fun setRunning(run: Boolean) {
        runFlag = run
    }

    private fun draw(canvas: Canvas) {
        canvas.drawColor(Color.GRAY)
        // TODO
    }

    override fun run() {
        var canvas: Canvas?
        while (runFlag) {
            // получаем текущее время и вычисляем разницу с предыдущим
            // сохраненным моментом времени
            val now = System.currentTimeMillis()
            val elapsedTime = now - prevTime
            if (elapsedTime > 30) {


                prevTime = now
            }
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
        }
    }
}