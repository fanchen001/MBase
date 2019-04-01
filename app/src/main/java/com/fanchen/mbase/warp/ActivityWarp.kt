package com.fanchen.mbase.warp

import android.os.Bundle
import android.app.Activity
import android.content.*
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentTransaction
import android.text.TextUtils
import android.graphics.Bitmap
import android.os.Parcelable
import android.support.v4.app.FragmentManager
import com.fanchen.mbase.util.DisplayUtil
import java.util.*


/**
 * Activity 扩展
 * Created by fanchen on 2018/8/31.
 */
/**
 * 开启一个Activity
 *
 * @param clazz
 */

/**
 * startActivityForResult
 * @param clazz
 * @param bundle
 * @param code
 */
fun Activity.startActivityForResult(clazz: Class<*>, bundle: Bundle? = null, code: Int = Activity.RESULT_FIRST_USER) {
   try {
       val intent = Intent(this, clazz)
       if (bundle != null) intent.putExtras(bundle)
       startActivityForResult(intent, code)
   }catch (e:Exception){
       e.printStackTrace()
   }
}

fun Activity.startActivityForResult(clazz: Class<*>, key: String? = null, value: Int? = 0, code: Int = Activity.RESULT_FIRST_USER) {
    try {
        val intent = Intent(this, clazz)
        if (key != null && value != null) intent.putExtra(key, value)
        startActivityForResult(intent, code)
    }catch (e:Exception){
        e.printStackTrace()
    }
}

fun Activity.startActivityForResult(clazz: Class<*>, key: String? = null, value: String? = null, code: Int = Activity.RESULT_FIRST_USER) {
    try {
        val intent = Intent(this, clazz)
        if (key != null && value != null) intent.putExtra(key, value)
        startActivityForResult(intent, code)
    }catch (e:Exception){
        e.printStackTrace()
    }
}

fun Activity.startActivityForResult(clazz: Class<*>, key: String? = null, value: Parcelable? = null, code: Int = Activity.RESULT_FIRST_USER) {
    try {
        val intent = Intent(this, clazz)
        if (key != null && value != null) intent.putExtra(key, value)
        startActivityForResult(intent, code)
    }catch (e:Exception){
        e.printStackTrace()
    }
}

fun Activity.startActivityForResult(intent: Intent?) {
    try {
        if (intent != null) startActivityForResult(intent, Activity.RESULT_FIRST_USER)
    }catch (e:Exception){
        e.printStackTrace()
    }
}

/**
 * 获取当前可见、不可见的Fragment
 */
fun <T : Fragment> FragmentActivity.getVisibleFragment(visible: Boolean = true): T? {
    val fm = supportFragmentManager ?: return null
    val fragments = fm.fragments ?: return null
    for (f in fragments) {
        if (f != null && visible && f.userVisibleHint) {
            return f as? T
        } else if (f != null && !visible && !f.userVisibleHint) {
            return f as? T
        }
    }
    return null
}


/**
 * 移除同级别fragment
 */
fun FragmentActivity.removeFragments() {
    val fragments = getFragments()
    if (fragments.isEmpty()) return
    for (fragment in fragments) {
        removeFragment(fragment)
    }
}

/**
 * 移除所有fragment
 */
fun FragmentActivity.removeAllFragments(fragmentManager: FragmentManager = this.supportFragmentManager) {
    val fragments = fragmentManager.fragments
    if (fragments.isEmpty()) return
    for (fragment in fragments) {
        if (fragment != null) {
            removeAllFragments(fragment.childFragmentManager)
            removeFragment(fragment)
        }
    }
}

/**
 * 移除同级别fragment
 */
fun FragmentActivity.removeFragment(f: Fragment) {
    supportFragmentManager.fragments.remove(f)
}

fun FragmentActivity.getFragments(): List<Fragment> {
    val fragments = supportFragmentManager.fragments
    if (fragments == null || fragments.isEmpty()) return Collections.emptyList()
    val result = ArrayList<Fragment>()
    for (fragment in fragments) {
        if (fragment != null) {
            result.add(fragment)
        }
    }
    return result
}

/**
 * @param id
 * @param name
 * @param f
 */
fun FragmentActivity.changeFragment(id: Int, f: Fragment, name: String? = "") {
    if (isFinishing) return
    val fm = supportFragmentManager ?: return
    // 切换动画
    val ft = fm.beginTransaction()
    // 替换布局为fragment
    ft.replace(id, f)
    // 将当前fragment添加到Application列表里面
    if (!TextUtils.isEmpty(name)) ft.addToBackStack(name)
    ft.setTransition(FragmentTransaction.TRANSIT_NONE)
    ft.commitAllowingStateLoss()
}

/**
 * 获取当前屏幕截图，包含状态栏
 *
 * @param activity
 * @return
 */
fun Activity.screenshotWithStatusBar(): Bitmap? {
    try {
        val view = this.window.decorView
        val width = DisplayUtil.getScreenWidth(this)
        val height = DisplayUtil.getScreenHeight(this)
        return view.screenshot(width, height)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}
