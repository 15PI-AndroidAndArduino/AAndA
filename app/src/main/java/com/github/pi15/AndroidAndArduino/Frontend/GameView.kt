package com.github.pi15.AndroidAndArduino.Frontend

import android.content.Context
import android.view.SurfaceHolder
import android.view.SurfaceView


class GameView(context: Context) : SurfaceView(context), SurfaceHolder.Callback {
    private lateinit var drawThread: GameViewDrawer

    init {
        holder.addCallback(this)
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int,
                                height: Int) {
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        drawThread = GameViewDrawer(holder, resources)
        drawThread.setRunning(true)
        drawThread.start()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        var retry = true
        // завершаем работу потока
        drawThread.setRunning(false)
        while (retry) {
            try {
                drawThread.join()
                retry = false
            } catch (e: InterruptedException) {
                // если не получилось, то будем пытаться еще и еще
            }
        }
    }
}