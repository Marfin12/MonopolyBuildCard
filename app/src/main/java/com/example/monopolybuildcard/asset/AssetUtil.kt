package com.example.monopolybuildcard.asset

import androidx.annotation.Keep
import com.example.monopolybuildcard.Constant.CardName

@Keep
object AssetUtil {
    val assetPrice = hashMapOf(
        CardName.PROPERTY_B to 1, CardName.PROPERTY_C to 2, CardName.PROPERTY_D to 3,
        CardName.PROPERTY_E to 4, CardName.PROPERTY_F to 5, CardName.PROPERTY_G to 6,
        CardName.PROPERTY_H to 7
    )

    val assetPosted = hashMapOf(
        CardName.PROPERTY_B to 0, CardName.PROPERTY_C to 0, CardName.PROPERTY_D to 0,
        CardName.PROPERTY_E to 0, CardName.PROPERTY_F to 0, CardName.PROPERTY_G to 0,
        CardName.PROPERTY_H to 0
    )

    val assetWildCard = mutableListOf(CardName.PROPERTY_BC)

    fun resetAllAssetPosted() {
        assetPosted[CardName.PROPERTY_B] = 0
        assetPosted[CardName.PROPERTY_C] = 0
        assetPosted[CardName.PROPERTY_D] = 0
        assetPosted[CardName.PROPERTY_E] = 0
        assetPosted[CardName.PROPERTY_F] = 0
        assetPosted[CardName.PROPERTY_G] = 0
        assetPosted[CardName.PROPERTY_H] = 0
    }
}
