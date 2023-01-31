package com.example.monopolybuildcard.main

import android.os.Parcelable
import androidx.annotation.Keep
import com.example.monopolybuildcard.GlobalCardData
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class RoomCardData(
    var ready: MutableList<GlobalCardData>? = mutableListOf()
) : Parcelable