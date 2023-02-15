package com.example.monopolybuildcard.player

import android.os.Parcelable
import androidx.annotation.Keep
import com.example.monopolybuildcard.GlobalCardData
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class PlayerActionData(
    var ownerId: String,
    var action: String
) : Parcelable