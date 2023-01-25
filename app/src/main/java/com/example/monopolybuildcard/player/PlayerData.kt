package com.example.monopolybuildcard.player

import android.os.Parcelable
import androidx.annotation.Keep
import com.example.monopolybuildcard.card.AssetData
import com.example.monopolybuildcard.card.CardData
import com.example.monopolybuildcard.money.MoneyData
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class PlayerData(
    val name: String,
    var money: Int,
    var asset: Int,
    val listAsset: MutableList<AssetData> = mutableListOf(),
    val listMoney: MutableList<MoneyData> = mutableListOf(),
    val listCard: MutableList<CardData> = mutableListOf(),
    var isMyTurn: Boolean
) : Parcelable