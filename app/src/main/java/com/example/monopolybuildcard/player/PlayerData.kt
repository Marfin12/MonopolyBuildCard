package com.example.monopolybuildcard.player

import android.os.Parcelable
import androidx.annotation.Keep
import com.example.monopolybuildcard.GlobalCardData
import com.example.monopolybuildcard.card.AssetData
import com.example.monopolybuildcard.card.CardData
import com.example.monopolybuildcard.money.MoneyData
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class PlayerData(
    var id: String = "",
    var name: String = "",
    var shouldHost: Boolean? = null,
    var money: Int = 0,
    var asset: Int = 0,
    var listAsset: MutableList<AssetData> = mutableListOf(),
    var listMoney: MutableList<MoneyData> = mutableListOf(),
    var cards: MutableList<GlobalCardData> = mutableListOf(),
    var shouldRunning: Boolean? = null
) : Parcelable