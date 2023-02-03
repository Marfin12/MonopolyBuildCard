package com.example.monopolybuildcard

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class GlobalCardData(
    var id: String? = "",
    var type: String? = ""
) : Parcelable
