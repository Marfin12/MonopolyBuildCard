package com.example.monopolybuildcard.asset

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class AssetData(
    val image: Int? = -1,
    var level: Int? = -1
) : Parcelable