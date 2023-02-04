package com.example.monopolybuildcard

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class GlobalActionData(
    var fromId: String? = "",
    var toId: String? = "",
    var card: GlobalCardData? = null
) : Parcelable
