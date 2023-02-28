package com.example.monopolybuildcard

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class GlobalCardData(
    var id: String? = "",
    var value: Int? = -1,
    var type: String? = "",
    var price: Int? = 0,
    var ownerId: String?= ""
    ) : Parcelable
