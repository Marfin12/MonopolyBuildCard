package com.example.monopolybuildcard.player

import androidx.annotation.Keep

@Keep
object PlayerStatus {
    const val RUNNING = "running"
    const val WAITING = "waiting"
    const val COLLECTING = "collecting"
    const val RESPONDING = "responding"
}