package com.example.monopolybuildcard

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import androidx.annotation.Keep

@Keep
object Util {
    @SuppressLint("HardwareIds")
    fun getAndroidId(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }
}
