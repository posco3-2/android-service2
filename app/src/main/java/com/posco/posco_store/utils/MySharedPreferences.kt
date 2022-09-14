package com.posco.posco_store.utils

import android.content.Context

class MySharedPreferences(context: Context) {

    private val prefs = context.getSharedPreferences("storage", Context.MODE_PRIVATE)

    fun getString(key: String, defValue: String) : String {
        return prefs.getString(key, defValue).toString()
    }

    fun setString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    fun getInt(key: String, defValue: Int) : Int {
        return prefs.getInt(key, defValue)
    }

    fun setInt(key: String, defValue: Int)  {
        prefs.edit().putInt(key, defValue).apply()
    }
}