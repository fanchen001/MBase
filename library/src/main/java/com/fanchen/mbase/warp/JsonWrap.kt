//package com.fanchen.mbase.warp
//
//import com.google.gson.Gson
//import java.lang.reflect.Type
//
///**
// * JsonWrap.kt
// *
// * 为Any 提供 转换json字串的能力
// * 为CharSequence 提供转成 bean的能力
// * Created by fanchen on 2018/8/31.
// */
//
///**
// * 转换json字串
// */
//fun Any.toJson(): String {
//    return Gson().toJson(this)
//}
//
///**
// * 转成对应data class
// */
//fun <T : Any> CharSequence.fromJson(type: Type): T {
//    return Gson().fromJson<T>(this.toString(), type)
//}
//
//fun <T> Any.fromJson(json: String): T {
//    return Gson().fromJson<T>(json, this.javaClass)
//}