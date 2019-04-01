package com.fanchen.mbase.warp

fun String.int(default:Int = 0):Int{
    try {
        return this.toInt()
    }catch (e : Exception){
        e.printStackTrace()
    }
    return default
}

fun String.float(default:Float = 0f):Float{
    try {
        return this.toFloat()
    }catch (e : Exception){
        e.printStackTrace()
    }
    return default
}

fun String.double(default:Double = 0.toDouble()):Double{
    try {
        return this.toDouble()
    }catch (e : Exception){
        e.printStackTrace()
    }
    return default
}
