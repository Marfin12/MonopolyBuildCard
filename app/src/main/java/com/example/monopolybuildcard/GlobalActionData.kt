package com.example.monopolybuildcard

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class GlobalActionData(
    var card: GlobalCardData? = null,
    var cardTaken: GlobalCardData? = null,
    var cardGiven: GlobalCardData? = null,
    var cardDealBreaker: MutableList<GlobalCardData>? = null
) : Parcelable
