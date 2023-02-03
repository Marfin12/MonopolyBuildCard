package com.example.monopolybuildcard

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import androidx.annotation.Keep
import com.example.monopolybuildcard.asset.AssetAdapter
import com.example.monopolybuildcard.asset.AssetData
import com.example.monopolybuildcard.card.CardData
import com.example.monopolybuildcard.card.CardType
import com.example.monopolybuildcard.main.PostedCardData
import com.example.monopolybuildcard.main.RoomCardData
import com.example.monopolybuildcard.main.RoomData
import com.example.monopolybuildcard.money.MoneyData
import com.example.monopolybuildcard.player.PlayerData

@Keep
object Util {
    @SuppressLint("HardwareIds")
    fun getAndroidId(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    fun updateRoomData(
        curRoomData: RoomData,
        roomCardData: RoomCardData
    ): RoomData {
        return RoomData(
            roomCardData,
            curRoomData.actions,
            curRoomData.status,
            curRoomData.maxPlayer,
            curRoomData.users
        )
    }
}
