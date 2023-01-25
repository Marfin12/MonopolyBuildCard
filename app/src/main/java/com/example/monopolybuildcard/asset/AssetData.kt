package com.example.monopolybuildcard.card

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class AssetData(
    val image: Int,
    var level: Int
) : Parcelable