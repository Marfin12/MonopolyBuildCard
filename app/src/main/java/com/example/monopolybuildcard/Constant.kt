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
            const val STATUS = "status"
            const val NAME = "name"
        }
    }
    object CardName {
        const val MONEY = "m"
        const val PROPERTY_B = "b"
        const val PROPERTY_C = "c"
        const val PROPERTY_D = "d"
        const val PROPERTY_E = "e"
        const val PROPERTY_F = "f"
        const val PROPERTY_G = "g"
        const val PROPERTY_H = "h"
        const val PROPERTY_BC = "bc"
        const val PROPERTY_FLIP = "z"
        const val ACTION_PASS_GO = "pass_go"
        const val ACTION_SLY_DEAL = "sly_deal"
        const val ACTION_FORCED_DEAL = "forced_deal"
        const val ACTION_DEAL_BREAKER = "deal_breaker"
        const val ACTION_DEBT_COLLECTOR = "debt_collector"
        const val ACTION_HAPPY_BIRTHDAY = "happy_birthday"
        const val ACTION_SAY_NO = "say_no"
        const val DOUBLE_THE_RENT = "double_the_rent"
        const val RENT_BLOK_BC_TYPE = "rent_bc"
        const val RENT_BLOK_DE_TYPE = "rent_de"
        const val RENT_BLOK_FG_TYPE = "rent_fg"
        const val RENT_BLOK_ANY_TYPE = "rent_any"
    }
}