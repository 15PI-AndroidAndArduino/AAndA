package com.github.pi15.AndroidAndArduino.Providers

import com.github.pi15.AndroidAndArduino.Containers.ButtonEvent
import com.github.pi15.AndroidAndArduino.Interfaces.ButtonEventsProvider
import java.io.IOException
import java.io.InputStream
import java.net.Socket
import java.nio.ByteBuffer
import java.util.*

class ButtonProviderClass : ButtonEventsProvider
{
    private var client: Socket? = null
    private var inputButtonID:  InputStream? = null
    private var buttonsID: Queue<ButtonEvent>? = null
    private val hostIP = "192.168.1.140"
    private val port = 90

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
    override fun anyEventsAvaliable(): Boolean? {
        return buttonsID?.isEmpty()
    }

    override fun popButtonEvent(): ButtonEvent? {
        return buttonsID?.poll()
    }

    override fun start() {
        connectToServer()

        val receiveSizeBytes = ByteArray(1)
        receiveDataFromServer(receiveSizeBytes)
        buttonsID?.offer(ButtonEvent(ByteBuffer.wrap(receiveSizeBytes).get().toInt()))
    }

    override fun pause() {
        stop()
    }

    override fun resume() {
        start()
    }

    override fun stop() {
        disConnectWithServer()
    }
}