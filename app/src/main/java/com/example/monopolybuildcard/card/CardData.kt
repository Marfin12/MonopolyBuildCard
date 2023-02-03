package com.example.monopolybuildcard.card

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class CardData(
    val image: Int,
    val type: String,
    var stepType: String
) : Parcelable