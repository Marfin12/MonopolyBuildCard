package com.example.monopolybuildcard

import android.provider.Settings
import androidx.annotation.Keep
import java.security.AccessController.getContext
import java.util.*

@Keep
object Constant {
    const val ROOMS = "rooms"
    const val CARDS = "cardsAndroid"
    object RoomFields {
        const val CARDS = "cards"
        const val MAX_PLAYER = "maxPlayer"
        const val STATUS = "status"
        const val USERS = "users"
        object PlayerFields {
            const val ID = "id"
            const val SHOULD_HOST = "shouldHost"
            const val SHOULD_RUNNING = "shouldRunning"
            const val NAME = "name"
        }
    }
}