package com.shivamsingh.groceryquiz.data.disc

import android.content.Context
import android.content.SharedPreferences
import com.shivamsingh.groceryquiz.domain.SHARED_PREF_NAME

class SharedPreferencesStore constructor(val context: Context) : DictionaryStore {
    val preferences: SharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)

    override fun storeValue(key: String, value: Boolean) {
        val editor = preferences.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    override fun storeValue(key: String, value: Long) {
        val editor = preferences.edit()
        editor.putLong(key, value)
        editor.apply()
    }

    override fun storeValue(key: String, value: String) {
        val editor = preferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    override fun retrieveBoolean(key: String, defaultValue: Boolean): Boolean {
        return preferences.getBoolean(key, defaultValue)
    }

    override fun retrieveLong(key: String, defaultValue: Long): Long {
        return preferences.getLong(key, defaultValue)
    }

    override fun retrieveValue(key: String, defaultValue: String): String {
        return preferences.getString(key, defaultValue)
    }
}