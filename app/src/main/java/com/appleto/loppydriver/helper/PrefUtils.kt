package com.appleto.loppydriver.helper

import android.content.Context
import android.content.SharedPreferences


object PrefUtils {

    /**
     * Storing API Key in shared preferences to
     * add it in header part of every retrofit request
     */
    val PREF_NAME = "LOPPY_DRIVER"

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun clear(context: Context) {
        val editor = getSharedPreferences(context).edit()
        editor.clear()
        editor.apply()
    }

    fun storeStringValue(context: Context, key: String, value: String) {
        val editor = getSharedPreferences(context).edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getStringValue(context: Context, key: String): String? {
        return getSharedPreferences(context).getString(key, "")
    }

    fun storeIntValue(context: Context, key: String, value: Int) {
        val editor = getSharedPreferences(context).edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun getIntValue(context: Context, key: String): Int {
        return getSharedPreferences(context).getInt(key, 0)
    }

    fun storeBooleanValue(context: Context, key: String, value: Boolean) {
        val editor = getSharedPreferences(context).edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getBooleanValue(context: Context, key: String): Boolean {
        return getSharedPreferences(context).getBoolean(key, false)
    }
}