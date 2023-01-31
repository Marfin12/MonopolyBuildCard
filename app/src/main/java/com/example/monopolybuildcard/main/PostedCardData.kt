package com.example.monopolybuildcard.main

import android.os.Parcelable
import androidx.annotation.Keep
import com.example.monopolybuildcard.GlobalCardData
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class PostedCardData(
    var cardId: String? = "",
    var fromId: String? = "",
    var toId: String? = ""
) : Parcelable