package com.github.pi15.AndroidAndArduino.Providers

import com.github.pi15.AndroidAndArduino.Containers.GameArrow
import com.github.pi15.AndroidAndArduino.Interfaces.ArrowsProvider
import v4lk.lwbd.BeatDetector
import v4lk.lwbd.util.Beat
import java.io.*
import java.lang.Thread.sleep
import java.util.*
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.thread
import kotlin.math.min

class ArrowsManager public constructor(audioInputStream: InputStream, val arrowRadius: Double,
                                       minimalDistanceBetweenArrows: Double, val deltaDp : Double,
                                       val lag : Int) : ArrowsProvider {
    override val allArrows : MutableList<GameArrow>
        get() {
            val toRet = MutableList(0, {_ -> GameArrow(0, 0.0, 0.0) })
            for (i in 0 until 4)
                for (x in arrowColumns[i])
                    toRet.add(x)
            return toRet
        }
    private val beats = LinkedBlockingQueue<Beat>()
    private val arrowColumns = Array(4, { _ -> ArrayDeque<GameArrow>()})
    private var startTime : Long = 0
    private var pauseStartTime : Long = 0
    private var summPauseTime : Long = 0

    override fun anyArrowsAvaliable(): Boolean {
        return !allArrows.isEmpty()
    }

    override fun willGenerateMoreArrows(): Boolean {
        return !beats.isEmpty()
    }

    override fun getBottommostArrowAtX(horizontalIndex: Int): GameArrow? {
        return if (arrowColumns[horizontalIndex].isEmpty()) null else arrowColumns[horizontalIndex].first()
    }

    fun getUpperostArrowAtX(horizontalIndex: Int): GameArrow? {
        return if (arrowColumns[horizontalIndex].isEmpty()) null else arrowColumns[horizontalIndex].last()
    }

    override fun deleteBottommostArrowAtX(horizontalIndex: Int) {
        arrowColumns[horizontalIndex].pollFirst()
    }

    private fun worker() {
        val spawn = -300.0
        while (willGenerateMoreArrows() || anyArrowsAvaliable()) {
            val elps = System.currentTimeMillis() + lag - startTime - summPauseTime
            while (elps > beats.peek().timeMs) {
                //gen
                beats.poll()
                for (i in 0 until 4) {
                    val ba = getUpperostArrowAtX(i)
                    if (ba == null || ba.yCoordinateInDp - spawn > 2 * arrowRadius) {
                        arrowColumns[i].add(GameArrow(i, spawn, arrowRadius))
                        break
                    }
                }
            }
            for (x in allArrows)
                x.yCoordinateInDp += deltaDp
            sleep(100)
        }
    }

    override fun start() {
        startTime = System.currentTimeMillis()
        thread {
            worker()
        }
    }

    override fun pause() {
        pauseStartTime = System.currentTimeMillis()
    }

    override fun resume() {
        summPauseTime += System.currentTimeMillis() - pauseStartTime
    }

    override fun stop() {
        beats.clear()
        for (x in arrowColumns)
            x.clear()
    }

    init {
        val bts = BeatDetector.detectBeats(audioInputStream, BeatDetector.AudioType.MP3)
        bts.sortByDescending { x -> -x.energy }
        val ln = min(100, bts.size)
        val sr = Array<Beat>(ln, {i -> bts[i]})
        sr.sortBy { x->x.timeMs }
        for (x in sr)
            beats.offer(x)
    }
}