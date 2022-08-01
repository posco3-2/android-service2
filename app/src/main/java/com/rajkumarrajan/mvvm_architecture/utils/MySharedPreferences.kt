package com.rajkumarrajan.mvvm_architecture.utils

import android.content.Context
import android.content.SharedPreferences

class MySharedPreferences(context: Context) {

    private val prefs = context.getSharedPreferences("storage", Context.MODE_PRIVATE)

    fun getString(key: String, defValue: String) : String {
        return prefs.getString(key, defValue).toString()
    }

    fun setString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }
}