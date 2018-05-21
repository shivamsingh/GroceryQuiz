package com.shivamsingh.groceryquiz.data.disc

interface DictionaryStore {

    fun storeValue(key: String, value: Boolean)

    fun storeValue(key: String, value: Long)

    fun storeValue(key: String, value: String)

    fun retrieveBoolean(key: String, defaultValue: Boolean = false): Boolean

    fun retrieveLong(key: String, defaultValue: Long = 0): Long

    fun retrieveValue(key: String, defaultValue: String = ""): String
}