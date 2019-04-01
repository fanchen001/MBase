package com.fanchen.mbase.util

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.content.res.Configuration
import android.provider.Settings


/**
 * 屏幕亮度显示相关
 * Created by fanchen on 2018/9/3.
 */
object DisplayUtil {
    /**
     * dp转px
     *
     */
    fun dp2px(context: Context?, dpVal: Float): Int {
        try {
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, context?.resources?.displayMetrics).toInt()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0
    }

    /**
     * sp转px
     */
    fun sp2px(context: Context?, spVal: Float): Int {
        try {
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spVal, context?.resources?.displayMetrics).toInt()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0
    }

    /**
     * px转dp
     *
     */
    fun px2dp(context: Context?, pxVal: Float): Int {
        try {
            val scale = context?.resources?.displayMetrics?.density ?: 1f
            return (pxVal / scale).toInt()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0
    }

    /**
     * px转sp
     */
    fun px2sp(context: Context?, pxVal: Float): Float {
        try {
            val result = context?.resources?.displayMetrics?.scaledDensity ?: 1f
            return (pxVal / result)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0f
    }

    /**
     * 获取屏幕尺寸与密度.
     *
     * @param context
     * the context
     * @return mDisplayMetrics
     */
    fun getDisplayMetrics(context: Context?): DisplayMetrics {
        return context?.resources?.displayMetrics ?: Resources.getSystem().displayMetrics
    }

    /**
     * 獲取狀態欄高度
     */
    fun getActionBarSize(context: Context?, barSize: Int): Int {
        try {
            val typedValue = TypedValue()
            val textSizeAttr = intArrayOf(barSize)
            val indexOfAttrTextSize = 0
            val a = context?.obtainStyledAttributes(typedValue.data, textSizeAttr)
            val actionBarSize = a?.getDimensionPixelSize(indexOfAttrTextSize, -1)
            a?.recycle()
            return actionBarSize ?: 0
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0
    }

    /**
     * getScreenHeight
     */
    fun getScreenHeight(activity: Activity?): Int {
        return activity?.findViewById<View>(android.R.id.content)?.height ?: 0
    }

    /**
     * getScreenWidth
     */
    fun getScreenWidth(activity: Activity?): Int {
        return activity?.findViewById<View>(android.R.id.content)?.width ?: 0
    }

    /**
     * 判断屏幕方向
     * true 横屏 false 竖屏
     * @return
     */
    fun isScreenChange(context: Context?): Boolean {
        val mConfiguration = context?.resources?.configuration //获取设置的配置信息
        return mConfiguration?.orientation == Configuration.ORIENTATION_LANDSCAPE
    }


    /**
     * 判断是否开启了自动亮度调节
     */
    fun isAutoBrightness(context: Context?): Boolean {
        try {
            return Settings.System.getInt( context?.contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE) === Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    /**
     * 获取屏幕的亮度
     */
    fun getScreenBrightness(context: Context?): Int {
        try {
            return Settings.System.getInt( context?.contentResolver, Settings.System.SCREEN_BRIGHTNESS)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0
    }

    /**
     * 设置亮度
     */
    fun setBrightness(activity: Activity?, brightness: Int) {
        val lp = activity?.window?.attributes
        lp?.screenBrightness = java.lang.Float.valueOf(brightness.toFloat()) * (1f / 255f)
        activity?.window?.attributes = lp
    }

    /**
     * 开启、关闭自动调节亮度
     */
    fun setAutoBrightness(context: Context?,auto : Boolean ){
        try {
            if (auto){
                Settings.System.putInt(context?.contentResolver,  Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC)
            }else{
                Settings.System.putInt(context?.contentResolver,  Settings.System.SCREEN_BRIGHTNESS_MODE,Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL)
            }
        }catch (e : Exception){
            e.printStackTrace()
        }
    }

    /**
     * 保存亮度设置状态
     */
    fun saveBrightness(context: Context?, brightness: Int) {
        val uri = Settings.System.getUriFor("screen_brightness")
        Settings.System.putInt(context?.contentResolver, "screen_brightness", brightness)
        context?.contentResolver?.notifyChange(uri, null)
    }

}