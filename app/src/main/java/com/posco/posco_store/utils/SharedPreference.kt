package com.posco.posco_store.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE

class SharedPreference(context: Context) {


    private val sharedPreferences = context.getSharedPreferences("posco_store", MODE_PRIVATE)

    var token: String?
        get() = sharedPreferences.getString("token", "0")
        set(value) = sharedPreferences.edit().putString("token", value).apply()

    var userId: Int
        get() =sharedPreferences.getInt("id",0)
        set(value) = sharedPreferences.edit().putInt("id", value).apply()

    var tokenActive: Int
        get() =sharedPreferences.getInt("tokenActive", 0)
        set(value) = sharedPreferences.edit().putInt("tokenActive", value).apply()

    var updateTokenActive: Int
        get() =sharedPreferences.getInt("updateTokenActive", 0)
        set(value) = sharedPreferences.edit().putInt("updateTokenActive", value).apply()

    var deviceId: Int
        get() =sharedPreferences.getInt("deviceId", 0)
        set(value) = sharedPreferences.edit().putInt("deviceId", value).apply()
}