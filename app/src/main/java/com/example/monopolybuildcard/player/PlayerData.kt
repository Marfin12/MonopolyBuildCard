package com.example.monopolybuildcard.player

import android.os.Parcelable
import androidx.annotation.Keep
import com.example.monopolybuildcard.GlobalCardData
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class PlayerData(
    var id: String = "",
    var name: String = "",
    var status: String = "",
    var money: MutableList<GlobalCardData> = mutableListOf(),
    var properties: MutableList<GlobalCardData> = mutableListOf(),
    var cards: MutableList<GlobalCardData> = mutableListOf(),
    var shouldHost: Boolean? = null
) : Parcelable