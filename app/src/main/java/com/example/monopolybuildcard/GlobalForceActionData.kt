package com.example.monopolybuildcard

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class GlobalForceActionData(
    var card: GlobalCardData,
    var ownerId: Int
) : Parcelable
