package com.example.monopolybuildcard.room

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class RoomResponseData(
    val isSuccessful: Boolean = false,
    val roomName: String = ""
) : Parcelable