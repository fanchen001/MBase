package com.fanchen.mbase.util

import android.content.Context
import android.util.Log
import java.util.*

/**
 * 日志相关
 * Created by fanchen on 2018/9/4.
 */
object LogUtil {
    /** debug开关.  */
    var D = true

    /** info开关.  */
    var I = true

    /** error开关.  */
    var E = true

    /** 起始执行时间.  */
    var startLogTimeInMillis: Long = 0


    /**
     * debug日志
     * @param tag
     * @param message
     */
    fun d(tag: String, message: String) {
        if (D) Log.d(tag, message)
    }

    /**
     * debug日志
     * @param context
     * @param message
     */
    fun d(context: Context, message: String) {
        val tag = context.javaClass.getSimpleName()
        d(tag, message)
    }

    /**
     * debug日志
     * @param clazz
     * @param message
     */
    fun d(clazz: Class<*>, message: String) {
        val tag = clazz.simpleName
        d(tag, message)
    }

    /**
     * debug日志
     * @param context
     * @param format
     * @param args
     */
    fun d(context: Context, format: String, vararg args: Any) {
        val tag = context.javaClass.getSimpleName()
        d(tag, buildMessage(format, *args))
    }

    /**
     * debug日志
     * @param clazz
     * @param format
     * @param args
     */
    fun d(clazz: Class<*>, format: String, vararg args: Any) {
        val tag = clazz.simpleName
        d(tag, buildMessage(format, *args))
    }

    /**
     * info日志
     * @param tag
     * @param message
     */
    fun i(tag: String, message: String) {
        Log.i(tag, message)
    }

    /**
     * info日志
     * @param context
     * @param message
     */
    fun i(context: Context, message: String) {
        val tag = context.javaClass.getSimpleName()
        i(tag, message)
    }

    /**
     * info日志
     * @param clazz
     * @param message
     */
    fun i(clazz: Class<*>, message: String) {
        val tag = clazz.simpleName
        i(tag, message)
    }

    /**
     * info日志
     * @param context
     * @param format
     * @param args
     */
    fun i(context: Context, format: String, vararg args: Any) {
        val tag = context.javaClass.getSimpleName()
        i(tag, buildMessage(format, *args))
    }

    /**
     * info日志
     * @param clazz
     * @param format
     * @param args
     */
    fun i(clazz: Class<*>, format: String, vararg args: Any) {
        val tag = clazz.simpleName
        i(tag, buildMessage(format, *args))
    }


    /**
     * error日志
     * @param tag
     * @param message
     */
    fun e(tag: String, message: String) {
        Log.e(tag, message)
    }

    /**
     * error日志
     * @param context
     * @param message
     */
    fun e(context: Context, message: String) {
        val tag = context.javaClass.getSimpleName()
        e(tag, message)
    }

    /**
     * error日志
     * @param clazz
     * @param message
     */
    fun e(clazz: Class<*>, message: String) {
        val tag = clazz.simpleName
        e(tag, message)
    }


    /**
     * error日志
     * @param context
     * @param format
     * @param args
     */
    fun e(context: Context, format: String, vararg args: Any) {
        val tag = context.javaClass.getSimpleName()
        e(tag, buildMessage(format, *args))
    }

    /**
     * error日志
     * @param clazz
     * @param format
     * @param args
     */
    fun e(clazz: Class<*>, format: String, vararg args: Any) {
        val tag = clazz.simpleName
        e(tag, buildMessage(format, *args))
    }

    /**
     * 描述：记录当前时间毫秒.
     *
     */
    fun prepareLog(tag: String) {
        val current = Calendar.getInstance()
        startLogTimeInMillis = current.getTimeInMillis()
        Log.d(tag, "日志计时开始：$startLogTimeInMillis")
    }

    /**
     * 描述：记录当前时间毫秒.
     *
     */
    fun prepareLog(context: Context) {
        val tag = context.javaClass.getSimpleName()
        prepareLog(tag)
    }

    /**
     * 描述：记录当前时间毫秒.
     *
     */
    fun prepareLog(clazz: Class<*>) {
        val tag = clazz.simpleName
        prepareLog(tag)
    }

    /**
     * 描述：打印这次的执行时间毫秒，需要首先调用prepareLog().
     *
     * @param tag 标记
     * @param message 描述
     * @param printTime 是否打印时间
     */
    fun d(tag: String, message: String, printTime: Boolean) {
        val current = Calendar.getInstance()
        val endLogTimeInMillis = current.getTimeInMillis()
        Log.d(tag, "$message  :  ${endLogTimeInMillis - startLogTimeInMillis} ms")
    }


    /**
     * 描述：打印这次的执行时间毫秒，需要首先调用prepareLog().
     *
     * @param tag 标记
     * @param message 描述
     * @param printTime 是否打印时间
     */
    fun d(context: Context, message: String, printTime: Boolean) {
        val tag = context.javaClass.getSimpleName()
        d(tag, message, printTime)
    }

    /**
     * 描述：打印这次的执行时间毫秒，需要首先调用prepareLog().
     *
     * @param clazz 标记
     * @param message 描述
     * @param printTime 是否打印时间
     */
    fun d(clazz: Class<*>, message: String, printTime: Boolean) {
        val tag = clazz.simpleName
        d(tag, message, printTime)
    }

    /**
     * debug日志的开关
     * @param d
     */
    fun debug(d: Boolean) {
        D = d
    }

    /**
     * info日志的开关
     * @param i
     */
    fun info(i: Boolean) {
        I = i
    }

    /**
     * error日志的开关
     * @param e
     */
    fun error(e: Boolean) {
        E = e
    }

    /**
     * 设置日志的开关
     * @param e
     */
    fun setVerbose(d: Boolean, i: Boolean, e: Boolean) {
        D = d
        I = i
        E = e
    }

    /**
     * 打开所有日志，默认全打开
     * @param d
     */
    fun openAll() {
        D = true
        I = true
        E = true
    }

    /**
     * 关闭所有日志
     * @param d
     */
    fun closeAll() {
        D = false
        I = false
        E = false
    }

    /**
     * format日志
     * @param format
     * @param args
     * @return
     */
    private fun buildMessage(format: String, vararg args: Any): String {
        val msg = if (args == null) format else String.format(Locale.US, format, args)
        val trace = Throwable().fillInStackTrace().stackTrace
        var caller = "<unknown>"
        for (i in 2 until trace.size) {
            val clazz = trace[i].javaClass
            if (clazz != LogUtil::class.java) {
                var callingClass = trace[i].className
                callingClass = callingClass.substring(callingClass.lastIndexOf('.') + 1)
                callingClass = callingClass.substring(callingClass.lastIndexOf('$') + 1)
                caller = callingClass + "." + trace[i].methodName
                break
            }
        }
        return String.format(Locale.US, "[%d] %s: %s",Thread.currentThread().id, caller, msg)
    }

}