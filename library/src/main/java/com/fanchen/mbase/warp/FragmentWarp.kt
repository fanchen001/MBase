package com.fanchen.mbase.warp

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.support.v4.view.ViewCompat
import android.graphics.drawable.Drawable
import android.os.Parcelable
import android.support.annotation.DrawableRes
import android.support.annotation.ColorInt
import com.fanchen.mbase.util.ImageUtil

/**
 * Fragment 扩展
 * Created by fanchen on 2018/8/31.
 */

/**
 * 通过id查找控件，在mMainView上
 *
 * @param id  控件id
 * @param <T>
 * @return
</T> */
fun <T : View> Fragment.findViewById(id: Int): T? {
    return view?.findViewById<View>(id) as? T
}

/**
 * 通过Child position 查找控件。在mMainView上
 *
 * @param position 下标
 * @param <T>
 * @return
</T> */
fun <T : View> Fragment.getChild(position: Int): T? {
    val group = view as? ViewGroup
    if (group == null || group.childCount <= position) {
        return null
    }
    return group.getChildAt(position) as? T
}

/**
 * 打开一个Activity
 *
 * @param clazz
 * @param bundle
 */
fun Fragment.startActivity(clazz: Class<*>, bundle: Bundle? = null) {
    if (isAdded && !isDetached && activity != null) {
        val intent = Intent(activity, clazz)
        if (bundle != null) intent.putExtras(bundle)
        startActivity(intent)
    }
}

/**
 * 打开一个Service
 *
 * @param clazz
 * @param bundle
 */
fun Fragment.startService(clazz: Class<*>, bundle: Bundle? = null) {
    if (isAdded && !isDetached && activity != null) {
        val intent = Intent(activity, clazz)
        if (bundle != null) intent.putExtras(bundle)
        activity?.startService(intent)
    }
}

/**
 * 开启activity并在返回时获取返回值
 *
 * @param clazz
 * @param bundle
 * @param code
 */
fun Fragment.startActivityForResult(clazz: Class<*>, bundle: Bundle? = null, code: Int = Activity.RESULT_FIRST_USER) {
    if (isAdded && !isDetached && activity != null) {
        val intent = Intent(activity, clazz)
        if (bundle != null) intent.putExtras(bundle)
        startActivityForResult(intent, code)
    }
}

fun Fragment.startActivityForResult(clazz: Class<*>, key: String? = null, value: Int? = 0, code: Int = Activity.RESULT_FIRST_USER) {
    if (isAdded && !isDetached && activity != null) {
        val intent = Intent(activity, clazz)
        if (key != null && value != null) intent.putExtra(key, value)
        startActivityForResult(intent, code)
    }
}

fun Fragment.startActivityForResult(clazz: Class<*>, key: String? = null, value: String? = null, code: Int = Activity.RESULT_FIRST_USER) {
    if (isAdded && !isDetached && activity != null) {
        val intent = Intent(activity, clazz)
        if (key != null && value != null) intent.putExtra(key, value)
        startActivityForResult(intent, code)
    }
}

fun Fragment.startActivityForResult(clazz: Class<*>, key: String? = null, value: Parcelable? = null, code: Int = Activity.RESULT_FIRST_USER) {
    if (isAdded && !isDetached && activity != null) {
        val intent = Intent(activity, clazz)
        if (key != null && value != null) intent.putExtra(key, value)
        startActivityForResult(intent, code)
    }
}

fun Fragment.startActivityForResult(intent: Intent?) {
    if (intent != null) startActivityForResult(intent, Activity.RESULT_FIRST_USER)
}

fun Fragment.startActivity(clazz: Class<*>, key: String? = null, value: String? = null) {
    if (isAdded && !isDetached && activity != null) {
        val intent = Intent(activity, clazz)
        if (key != null&& value != null) intent.putExtra(key, value)
        startActivity(intent)
    }
}

fun Fragment.startActivity(clazz: Class<*>, key: String? = null, value: Parcelable? = null) {
    if (isAdded && !isDetached && activity != null) {
        val intent = Intent(activity, clazz)
        if (key != null&& value != null) intent.putExtra(key, value)
        startActivity(intent)
    }
}

fun Fragment.startActivity(clazz: Class<*>, key: String? = null, value: Int? = 0) {
    if (isAdded && !isDetached && activity != null) {
        val intent = Intent(activity, clazz)
        if (key != null && value != null) intent.putExtra(key, value)
        startActivity(intent)
    }
}

/**
 * 显示一个Toast
 *
 * @param c
 */
fun Fragment.showToast(c: CharSequence, len: Int = Toast.LENGTH_SHORT) {
    if (isAdded && !isDetached && activity != null) {
        activity?.showToast(c, len)
    }
}

/**
 * 显示一个Toast
 *
 * @param id
 * @param len
 */
fun Fragment.showToast(id: Int, len: Int) {
    if (isAdded && !isDetached && activity != null) {
        activity?.showToast(id, len)
    }
}

/**
 * 显示一个Snackbar
 *
 * @param view
 * @param c
 * @param title
 * @param l
 */
fun Fragment.showSnackbar(view: View, title: Int, c: Int, duration: Int = Toast.LENGTH_SHORT, l: ((View) -> Unit)? = null) {
    if (isAdded && !isDetached && activity != null) {
        activity?.showSnackbar(view, title, c, duration, l)
    }
}

/**
 * @param view
 * @param c
 * @param title
 * @param l
 */
fun Fragment.showSnackbar(view: View, title: CharSequence, c: CharSequence, duration: Int = Toast.LENGTH_SHORT, l: ((View) -> Unit)? = null) {
    if (isAdded && !isDetached && activity != null) {
        activity?.showSnackbar(view, title, c, duration, l)
    }
}

/**
 * 设置背景色
 *
 * @param fragment fragment
 * @param color    背景色
 */
fun Fragment.setBackgroundColor(@ColorInt color: Int) {
    view?.setBackgroundColor(color)
}

/**
 * 设置背景资源
 *
 * @param fragment fragment
 * @param resId    资源Id
 */
fun Fragment.setBackgroundResource(@DrawableRes resId: Int) {
    view?.setBackgroundResource(resId)
}

/**
 * 设置背景
 *
 * @param fragment   fragment
 * @param background 背景
 */
fun Fragment.setBackground(background: Drawable) {
    val v = view ?: return
    ViewCompat.setBackground(v, background)
}

/**
 * 设置背景
 *
 * @param fragment   fragment
 * @param background 背景
 */
fun Fragment.setBackgroundBitmap(bitmap: Bitmap) {
    val v = view ?: return
    ViewCompat.setBackground(v, ImageUtil.bitmap2Drawable(context, bitmap))
}