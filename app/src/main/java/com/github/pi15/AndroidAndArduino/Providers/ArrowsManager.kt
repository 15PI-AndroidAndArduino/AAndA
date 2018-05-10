package com.github.pi15.AndroidAndArduino.Providers

import com.github.pi15.AndroidAndArduino.Containers.GameArrow
import com.github.pi15.AndroidAndArduino.Interfaces.ArrowsProvider
import v4lk.lwbd.BeatDetector
import v4lk.lwbd.util.Beat
import java.io.*
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.thread

class ArrowsManager : ArrowsProvider {
    override val allArrows: MutableList<GameArrow>

    private val lock = ReentrantLock()
    private val timerLock = ReentrantLock()
    private val speed:Double
    private val updateRate: Long
    private val beats: Array<Beat>
    private val arrowRadius: Double

    private val random: Random
    private var arrowType: Int

    private var isStoped: Boolean
    private var isPaused: Boolean
    private var alreadyStart: Boolean
    private var pauseTime: Long
    private var elapsedTime: Long
    private var indexInBeats: Int


    public constructor(pathToFile: String, arrowRadius: Double, updateRateMs: Long) {
        val audioFile = File(pathToFile)
        beats = BeatDetector.detectBeats(audioFile, BeatDetector.AudioType.MP3)
        var minDistance: Long = 0
        for (i in 0..(beats.size - 1)) {
            if (minDistance > beats[i + 1].timeMs - beats[i].timeMs)
                minDistance = beats[i + 1].timeMs - beats[i].timeMs
        }

        //arrowRadius * 2 * 2 Чтобы между стрелками точно помещалась ещё одна. Избежания наложения
        speed = arrowRadius * 2 * 2 / minDistance
        updateRate = updateRateMs

        //Тут происходит генерация стрелок
        random = Random()

        isStoped = false
        isPaused = false
        alreadyStart = false
        pauseTime = 0
        indexInBeats = 0
        elapsedTime = 0
        arrowType = -1

        this.arrowRadius = arrowRadius
        allArrows = mutableListOf()
    }

    override fun anyArrowsAvaliable(): Boolean {
        return allArrows.size > 0
    }

    override fun willGenerateMoreArrows(): Boolean {
        return indexInBeats < beats.size
    }

    override fun getBottommostArrowAtX(horizontalIndex: Int): GameArrow? {
        var result: GameArrow?

        lock.lock()
        result = if(allArrows.size != 0)
            allArrows[0]
        else
            null
        lock.unlock()

        return result
    }

    override fun deleteBottommostArrowAtX(horizontalIndex: Int) {
        lock.lock()
        allArrows.removeAt(0)
        lock.unlock()
    }

    override fun start() {
        if(!alreadyStart){
            alreadyStart = true
            isPaused = false
            isStoped = false

            thread {
                while (!isStoped){
                    if(!isPaused){
                        if(pauseTime.compareTo(0) == 0){
                            Thread.sleep(updateRate - (pauseTime - elapsedTime))
                            pauseTime = 0
                        }
                        else
                            Thread.sleep(updateRate)

                        if(isPaused)
                            continue//Двойная проверка, в случае если пауза была нажата во время sleep

                        elapsedTime += updateRate

                        lock.lock()
                        for(x in allArrows){
                            x.yCoordinateInDp += speed*updateRate
                        }
                        tryGenerateNewArrow()
                        lock.unlock()

                    } else {
                        timerLock.lock()
                        Thread.sleep(updateRate)
                        timerLock.unlock()
                    }
                }
            }.start()
        }
    }

    override fun pause() {
        isPaused = true
        isStoped = false
        pauseTime = System.currentTimeMillis()
    }

    override fun resume() {
        timerLock.lock()
        isPaused = false
        isStoped = false
        timerLock.unlock()
    }

    override fun stop() {
        isPaused = false
        isStoped = true
    }

    private fun tryGenerateNewArrow() {
        if(indexInBeats == beats.size)
            return
        if (beats[indexInBeats].timeMs > elapsedTime)
            return

        var moveType: Int

        //Generate arrow type
        if (arrowType == -1)
            arrowType = random.nextInt(4)// [0; 3]
        else {
            moveType = random.nextInt(3) - 1//[-1; 1]
            arrowType += moveType
            if (arrowType < 0) arrowType = 0
            if (arrowType > 3) arrowType = 3
        }

        allArrows.add(GameArrow(arrowType, (beats[indexInBeats].timeMs - elapsedTime) * speed, arrowRadius))
        indexInBeats++
    }
}