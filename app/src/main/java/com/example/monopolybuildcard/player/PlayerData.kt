package com.example.monopolybuildcard.player

import android.os.Parcelable
import androidx.annotation.Keep
import com.example.monopolybuildcard.GlobalCardData
import com.example.monopolybuildcard.asset.AssetData
import com.example.monopolybuildcard.money.MoneyData
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class PlayerData(
    var id: String = "",
    var name: String = "",
    var money: MutableList<GlobalCardData> = mutableListOf(),
    var properties: MutableList<GlobalCardData> = mutableListOf(),
    var cards: MutableList<GlobalCardData> = mutableListOf(),
    var shouldRunning: Boolean? = null,
    var shouldHost: Boolean? = null
) : Parcelable