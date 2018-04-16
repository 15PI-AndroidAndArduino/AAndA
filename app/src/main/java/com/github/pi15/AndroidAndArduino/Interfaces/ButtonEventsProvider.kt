package com.github.pi15.AndroidAndArduino.Interfaces

import com.github.pi15.AndroidAndArduino.Containers.ButtonEvent

interface ButtonEventsProvider {
    /**
     * Are more button events avaliable
     */
    fun isProviderEmpty() : Boolean

    /**
     * Returns and deletes the oldest recieved button event. If the provider is empty, returns null.
     */
    fun popButtonEvent() : ButtonEvent?
}
