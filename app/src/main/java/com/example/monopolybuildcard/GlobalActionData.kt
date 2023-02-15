package com.example.monopolybuildcard

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class GlobalActionData(
    var ownerId: String?= "",
    var card: GlobalCardData? = null,
    var cardTaken: GlobalCardData? = null,
    var cardGiven: GlobalCardData? = null
) : Parcelable
