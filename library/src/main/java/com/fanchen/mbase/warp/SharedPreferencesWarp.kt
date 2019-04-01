package com.fanchen.mbase.warp

import android.annotation.SuppressLint
import android.content.SharedPreferences
import java.lang.reflect.Type

/**
 * SharedPreferences 扩展
 * Created by fanchen on 2018/9/5.
 */
@SuppressLint("ApplySharedPref")
fun SharedPreferences.commit(key: String, value: Boolean) {
    this.edit().putBoolean(key, value).commit()
}

@SuppressLint("ApplySharedPref")
fun SharedPreferences.commit(key: String, value: Float) {
    this.edit().putFloat(key, value).commit()
}

@SuppressLint("ApplySharedPref")
fun SharedPreferences.commit(key: String, value: Long) {
    this.edit().putLong(key, value).commit()
}

@SuppressLint("ApplySharedPref")
fun SharedPreferences.commit(key: String, value: Int) {
    this.edit().putInt(key, value).commit()
}

@SuppressLint("ApplySharedPref")
fun SharedPreferences.commit(key: String, value: Set<String>) {
    this.edit().putStringSet(key, value).commit()
}

@SuppressLint("ApplySharedPref")
fun SharedPreferences.commit(key: String, value: String) {
    this.edit().putString(key, value).commit()
}

@SuppressLint("ApplySharedPref")
fun SharedPreferences.commit(key: String, value: Any) {
    this.edit().putString(key, value.toJson()).commit()
}

fun <T : Any> SharedPreferences.getAny(key: String, type: Type): T? {
    return this.getString(key, "")?.fromJson(type)
}

fun SharedPreferences.apply(key: String, value: Boolean) {
    this.edit().putBoolean(key, value).apply()
}

fun SharedPreferences.apply(key: String, value: Float) {
    this.edit().putFloat(key, value).apply()
}

fun SharedPreferences.apply(key: String, value: Long) {
    this.edit().putLong(key, value).apply()
}

fun SharedPreferences.apply(key: String, value: Int) {
    this.edit().putInt(key, value).apply()
}

fun SharedPreferences.apply(key: String, value: Set<String>) {
    this.edit().putStringSet(key, value).apply()
}

fun SharedPreferences.apply(key: String, value: String) {
    this.edit().putString(key, value).apply()
}

fun SharedPreferences.apply(key: String, value: Any) {
    this.edit().putString(key, value.toJson()).apply()
}