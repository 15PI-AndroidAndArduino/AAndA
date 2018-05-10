package com.github.pi15.AndroidAndArduino.Providers

import com.github.pi15.AndroidAndArduino.Containers.ButtonEvent
import com.github.pi15.AndroidAndArduino.Interfaces.ButtonEventsProvider
import java.io.IOException
import java.io.InputStream
import java.net.Socket
import java.nio.ByteBuffer
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.concurrent.thread

class ButtonProviderClass : ButtonEventsProvider
{
    private var client: Socket? = null
    private var inputButtonID:  InputStream? = null
    private var buttonsID: ConcurrentLinkedQueue<ButtonEvent>? = null
    private val hostIP = "192.168.0.100"
    private val port = 30123
    private var stopFlag = false
    private var startFlag = false

    private fun connectToServer() {
        try {
            if (client == null) {
                client = Socket(hostIP, port)
                inputButtonID = client!!.getInputStream()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun disConnectWithServer()  {
        if (client != null) {
            if (client!!.isConnected()) {
                try {
                    client!!.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }
    private fun receiveDataFromServer(bytes: ByteArray) {
        try {
            if (!client?.isConnected()!!) {
                throw IOException("Socket not connected");
            }
            inputButtonID?.read(bytes)
        } catch (e: IOException) {
            e.message
        }

    }
    override fun anyEventsAvaliable(): Boolean {
        return buttonsID?.isEmpty()!!
    }

    override fun popButtonEvent(): ButtonEvent? {
        return buttonsID?.poll()
    }

    override fun start() {

        if(!startFlag) {
            startFlag = true
            stopFlag = false

            thread {
                connectToServer()
            }.join()

            thread {
                while (!stopFlag) {
                    val receiveSizeBytes = ByteArray(1)
                    receiveDataFromServer(receiveSizeBytes)
                    val buttonID: Int = ByteBuffer.wrap(receiveSizeBytes).get().toInt()
                    when (buttonID) {
                        6 -> buttonsID?.offer(ButtonEvent(0))
                        7 -> buttonsID?.offer(ButtonEvent(1))
                        8 -> buttonsID?.offer(ButtonEvent(2))
                        9 -> buttonsID?.offer(ButtonEvent(3))
                    }
                }
            }
        }
    }
    override fun pause() {
        stop()
    }

    override fun resume() {
        start()
    }

    override fun stop() {
        stopFlag = true
        disConnectWithServer()
    }
}