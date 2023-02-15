package com.example.monopolybuildcard.asset

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import androidx.annotation.Keep
import com.example.monopolybuildcard.Constant.CardName
import com.example.monopolybuildcard.GlobalActionData
import com.example.monopolybuildcard.GlobalCardData
import com.example.monopolybuildcard.R
import com.example.monopolybuildcard.main.RoomCardData
import com.example.monopolybuildcard.main.RoomData

@Keep
object AssetUtil {
    val assetPrice = hashMapOf(
        CardName.PROPERTY_B to 1, CardName.PROPERTY_C to 2, CardName.PROPERTY_D to 3,
        CardName.PROPERTY_E to 4, CardName.PROPERTY_F to 5, CardName.PROPERTY_G to 6,
        CardName.PROPERTY_H to 7
    )

    val assetWildCard = mutableListOf("bc")
}
