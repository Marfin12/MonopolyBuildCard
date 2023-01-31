package com.example.monopolybuildcard.menu

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class MenuData(
    val isSuccessful: Boolean?,
    val roomName: String?
) : Parcelable