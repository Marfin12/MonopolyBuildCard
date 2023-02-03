package com.example.monopolybuildcard.money

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class MoneyData(
    val image: Int? = -1
) : Parcelable