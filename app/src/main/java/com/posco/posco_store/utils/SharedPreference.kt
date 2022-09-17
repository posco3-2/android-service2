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
        set(value) = value.let { sharedPreferences.edit().putInt("id", it).apply() }

    var tokenActive: Int
        get() =sharedPreferences.getInt("tokenActive", 0)
        set(value) = value.let { sharedPreferences.edit().putInt("tokenActive", it).apply() }

    var updateTokenActive: Int
        get() =sharedPreferences.getInt("updateTokenActive", 0)
        set(value) = value.let { sharedPreferences.edit().putInt("updateTokenActive", it).apply() }

    var deviceId: Int
        get() =sharedPreferences.getInt("deviceId", 0)
        set(value) = value.let { sharedPreferences.edit().putInt("deviceId", it).apply() }
}