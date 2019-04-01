package com.fanchen.mbase.warp

/**
 * ArrayList
 */
fun <T> ArrayList<T>.addEmpty(data: T?) {
    if (data != null) add(data)
}

/**
 * ArrayList
 */
fun <T> ArrayList<T>.addAllEmpty(data: List<T>?) {
    if (data != null) addAll(data)
}

/**
 * ArrayList
 */
fun <T> ArrayList<T>.getPosition(position: Int): T? {
    if (position < size) {
        return this[position]
    }
    return null
}