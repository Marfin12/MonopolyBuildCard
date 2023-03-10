package com.example.monopolybuildcard.main

import android.os.Parcelable
import androidx.annotation.Keep
import com.example.monopolybuildcard.GlobalActionData
import com.example.monopolybuildcard.GlobalCardData
import com.example.monopolybuildcard.player.PlayerData
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class RoomData(
    var cards: RoomCardData? = RoomCardData(mutableListOf()),
    var actions: MutableList<GlobalActionData>? = mutableListOf(),
    var status: String? = "",
    var maxPlayer: Int? = -1,
    var users: MutableList<PlayerData>? = mutableListOf()
) : Parcelable

@Keep
@Parcelize
data class RoomCardData(
    var ready: MutableList<GlobalCardData>? = mutableListOf()
) : Parcelable