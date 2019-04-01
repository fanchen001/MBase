package com.fanchen.mbase.dialog

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.text.TextUtils
import java.lang.reflect.Method

object Utils {

    fun getHeight(context: Context?): Int {
        if (context == null) return 0
        var statusBarHeight = 0
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            statusBarHeight = context.resources.getDimensionPixelSize(resourceId)
        }
        return if (isFlymeOs4x()) {
            2 * statusBarHeight
        } else statusBarHeight
    }

    fun isFlymeOs4x(): Boolean {
        val sysVersion = android.os.Build.VERSION.RELEASE
        if ("4.4.4" == sysVersion) {
            val sysIncrement = android.os.Build.VERSION.INCREMENTAL
            val displayId = android.os.Build.DISPLAY
            return if (!TextUtils.isEmpty(sysIncrement)) {
                sysIncrement.contains("Flyme_OS_4")
            } else {
                displayId.contains("Flyme OS 4")
            }
        }
        return false
    }


    /**
     * 循环向上转型, 获取对象的 DeclaredMethod
     *
     * @param object         : 子类对象
     * @param methodName     : 父类中的方法名
     * @param parameterTypes : 父类中的方法参数类型
     * @return 父类中的方法对象
     */

    fun getDeclaredMethod(`object`: Any, methodName: String, vararg parameterTypes: Class<*>): Method? {
        var method: Method? = null
        var clazz: Class<*> = `object`.javaClass
        while (clazz != Any::class.java) {
            try {
                method = clazz.getDeclaredMethod(methodName, *parameterTypes)
                return method
            } catch (e: Exception) {
                // 这里甚么都不要做！并且这里的异常必须这样写，不能抛出去。
                // 如果这里的异常打印或者往外抛，则就不会执行clazz =
                // clazz.getSuperclass(),最后就不会进入到父类中了
            }
            clazz = clazz.superclass
        }
        return null
    }

    /**
     * invokeMethod 无参数Method
     *
     * @param obj
     * @param methodName
     */
    fun invokeMethod(obj: Any, methodName: String) {
        val declaredMethod = getDeclaredMethod(obj, methodName) ?: return
        declaredMethod.isAccessible = true
        try {
            declaredMethod.invoke(obj, null as Array<Any>?)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * set btn selector with corner drawable for special position
     */
    fun btnSelector(radius: Float, normalColor: Int, pressColor: Int, postion: Int): StateListDrawable {
        val bg = StateListDrawable()
        var normal: Drawable? = null
        var pressed: Drawable? = null
        if (postion == 0) {// left btn
            normal = cornerDrawable(normalColor, floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, radius, radius))
            pressed = cornerDrawable(pressColor, floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, radius, radius))
        } else if (postion == 1) {// right btn
            normal = cornerDrawable(normalColor, floatArrayOf(0f, 0f, 0f, 0f, radius, radius, 0f, 0f))
            pressed = cornerDrawable(pressColor, floatArrayOf(0f, 0f, 0f, 0f, radius, radius, 0f, 0f))
        } else if (postion == -1) {// only one btn
            normal = cornerDrawable(normalColor, floatArrayOf(0f, 0f, 0f, 0f, radius, radius, radius, radius))
            pressed = cornerDrawable(pressColor, floatArrayOf(0f, 0f, 0f, 0f, radius, radius, radius, radius))
        } else if (postion == -2) {// for material dialog
            normal = cornerDrawable(normalColor, radius)
            pressed = cornerDrawable(pressColor, radius)
        }
        bg.addState(intArrayOf(-android.R.attr.state_pressed), normal)
        bg.addState(intArrayOf(android.R.attr.state_pressed), pressed)
        return bg
    }

    fun cornerDrawable(bgColor: Int, cornerradius: Float): Drawable {
        val bg = GradientDrawable()
        bg.cornerRadius = cornerradius
        bg.setColor(bgColor)
        return bg
    }

    fun cornerDrawable(bgColor: Int, cornerradius: FloatArray): Drawable {
        val bg = GradientDrawable()
        bg.cornerRadii = cornerradius
        bg.setColor(bgColor)
        return bg
    }

    /**
     * set ListView item selector with corner drawable for the last position
     * (ListView的item点击效果,只处理最后一项圆角处理)
     */
    fun listItemSelector(radius: Float, normalColor: Int, pressColor: Int, isLastPostion: Boolean): StateListDrawable {
        val bg = StateListDrawable()
        var normal: Drawable? = null
        var pressed: Drawable? = null
        if (!isLastPostion) {
            normal = ColorDrawable(normalColor)
            pressed = ColorDrawable(pressColor)
        } else {
            normal = cornerDrawable(normalColor, floatArrayOf(0f, 0f, 0f, 0f, radius, radius, radius, radius))
            pressed = cornerDrawable(pressColor, floatArrayOf(0f, 0f, 0f, 0f, radius, radius, radius, radius))
        }
        bg.addState(intArrayOf(-android.R.attr.state_pressed), normal)
        bg.addState(intArrayOf(android.R.attr.state_pressed), pressed)
        return bg
    }

    /**
     * set ListView item selector with corner drawable for the first and the last position
     * (ListView的item点击效果,第一项和最后一项圆角处理)
     */
    fun listItemSelector(radius: Float, normalColor: Int, pressColor: Int, itemTotalSize: Int, itemPosition: Int): StateListDrawable {
        val bg = StateListDrawable()
        var normal: Drawable? = null
        var pressed: Drawable? = null
        if (itemPosition == 0 && itemPosition == itemTotalSize - 1) {// 只有一项
            normal = cornerDrawable(normalColor, floatArrayOf(radius, radius, radius, radius, radius, radius, radius, radius))
            pressed = cornerDrawable(pressColor, floatArrayOf(radius, radius, radius, radius, radius, radius, radius, radius))
        } else if (itemPosition == 0) {
            normal = cornerDrawable(normalColor, floatArrayOf(radius, radius, radius, radius, 0f, 0f, 0f, 0f))
            pressed = cornerDrawable(pressColor, floatArrayOf(radius, radius, radius, radius, 0f, 0f, 0f, 0f))
        } else if (itemPosition == itemTotalSize - 1) {
            normal = cornerDrawable(normalColor, floatArrayOf(0f, 0f, 0f, 0f, radius, radius, radius, radius))
            pressed = cornerDrawable(pressColor, floatArrayOf(0f, 0f, 0f, 0f, radius, radius, radius, radius))
        } else {
            normal = ColorDrawable(normalColor)
            pressed = ColorDrawable(pressColor)
        }
        bg.addState(intArrayOf(-android.R.attr.state_pressed), normal)
        bg.addState(intArrayOf(android.R.attr.state_pressed), pressed)
        return bg
    }
}