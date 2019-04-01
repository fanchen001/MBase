package com.fanchen.mbase.warp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.view.ViewGroup.LayoutParams.MATCH_PARENT

/**
 * 对ViewGroup的扩展
 * Created by fanchen on 2018/8/31.
 */
fun ViewGroup.inflate(layoutRes: Int, attach: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attach)
}

fun ViewGroup.addToGroup(view: View?, newParams: Boolean = false) {
    val params = view?.layoutParams
    if (view != null && params != null) {
        addView(view, ViewGroup.LayoutParams(params.width, params.height))
        if (newParams) layoutParams = params
    } else if (view != null) {
        addView(view, ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT))
        if (newParams) layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
    }
}

fun <T : View> ViewGroup.findViewPosition(pos: Int): T? {
    return getChildAt(pos) as? T
}

fun <T : View> ViewGroup.children(clazz: Class<T>): List<T> {
    val list = ArrayList<T>()
    children.forEach {
        if (it.javaClass == clazz) {
            list.add(it as T)
        }
    }
    return list
}

fun ViewGroup.childPosition(child: View?): Int {
    children.forEachIndexed { index, view ->
        if (view == child) {
            return index
        }
    }
    return -1
}

fun <T : View> ViewGroup.childVisible(clazz: Class<T>, visible: Int) {
    children(clazz).forEach {
        it.visibility = visible
    }
}

val ViewGroup.children: List<View>
    get() = (0 until childCount).map { getChildAt(it) }


