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
    private var input:  InputStream? = null
    private var buttonsID: Queue<ButtonEvent>? = null


    private fun connectToServer()
    {
        try {
            if (client == null) {
                client = Socket("192.168.1.140", 80)
                input = client!!.getInputStream()
                println("connected")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun disConnectWithServer()  {
        if (client != null) {
            if (client!!.isConnected()) {
                try {
                    input?.close()
                    client!!.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }
    fun receiveDataFromServer(bytes: ByteArray) {
        try {
            if (!client?.isConnected()!!) {
                throw IOException("Socket not connected");
            }

            input?.read(bytes)

            //return message
        } catch (e: IOException) {
            e.printStackTrace()
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