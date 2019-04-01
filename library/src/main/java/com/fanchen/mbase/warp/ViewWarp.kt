package com.fanchen.mbase.warp

import android.graphics.Bitmap
import android.view.View
import android.util.SparseArray


/**
 * 对View 的扩展
 * Created by fanchen on 2018/8/31.
 */
/**
 * @param lab
 * @param count
 */

/**
 * 当前view的父view设置可见状态visible
 */

fun View.setParentVisible(visible: Int = View.GONE,leve: Int = 0) {
    getParentView(leve)?.visibility = visible
}

fun View.setParentGone(visible: Boolean?,leve: Int = 0) {
    getParentView(leve)?.visibility = if(visible == true) View.VISIBLE else View.GONE
}

fun View.setGone(gone: Boolean?) {
    visibility = if (gone == false) View.GONE else View.VISIBLE
}

fun View.setInVisibility(visible: Boolean?) {
    visibility = if (visible == false) View.INVISIBLE else View.VISIBLE
}

/**
 * 当前view的第leve级父view，leve从0开始
 * 默认获取当前view的第一级父view
 */
fun View.getParentView(leve: Int = 0): View? {
    var sLeve = 0
    var parent: View? = this.parent as? View
    while (parent != null && sLeve in 0..(leve - 1)) {
        sLeve++
        parent = parent.parent as? View
    }
    return parent
}

/**
 * findViewById
 */
fun <T : View> View.findViewId(id: Int): T? {
    return findViewById<View>(id) as? T
}

/***
 * 设置当前view的子节点的可见状态visible
 */
fun View.setChildsVisible(id: Int, visible: Int = View.GONE) {
    findViewById<View>(id)?.visibility = visible
}

/**
 * viewHolder 用的东东
 */
fun <T : View> View.getHolderView(id: Int): T? {
    var viewHolder: SparseArray<View>? = this.tag as? SparseArray<View>
    if (viewHolder == null) {
        viewHolder = SparseArray()
        this.tag = viewHolder
    }
    var childView: View? = viewHolder.get(id)
    if (childView == null) {
        childView = this.findViewById(id)
        viewHolder.put(id, childView)
    }
    return childView as? T
}

/**
 * 獲取當前view的截圖
 */
fun View.screenshot(nwidth: Int = 0, nheight: Int = 0): Bitmap? {
    isDrawingCacheEnabled = true
    buildDrawingCache()
    val bmp = drawingCache
    val width = if (nwidth == 0) width else nwidth
    val height = if (nheight == 0) height else nheight
    var bp: Bitmap? = null
    bp = Bitmap.createBitmap(bmp, 0, 0, width, height)
    destroyDrawingCache()
    return bp
}

/***
 * 设置延迟时间的View扩展
 * @param delay Long 延迟时间，默认600毫秒
 * @return T
 */
fun View.withTrigger(delay: Long = 600): View {
    triggerDelay = delay
    return this
}

/***
 * 点击事件的View扩展
 * @param block: (T) -> Unit 函数
 * @return Unit
 */
fun View.click(block: (View) -> Unit) = setOnClickListener {
    if (clickEnable()) {
        block(it as View)
    }
}

/***
 * 带延迟过滤的点击事件View扩展
 * @param delay Long 延迟时间，默认600毫秒
 * @param block: (T) -> Unit 函数
 * @return Unit
 */
fun View.clickWithTrigger(time: Long = 600, block: (View) -> Unit) {
    triggerDelay = time
    setOnClickListener {
        if (clickEnable()) {
            block(it as View)
        }
    }
}

/**
 * 上一次点击时间
 */
private var View.triggerLastTime: Long
    get() = if (getTag(1123460103) != null) getTag(1123460103) as Long else 0
    set(value) {
        setTag(1123460103, value)
    }

/**
 * 防止重复点击间隔时间
 */
private var View.triggerDelay: Long
    get() = if (getTag(1123461123) != null) getTag(1123461123) as Long else -1
    set(value) {
        setTag(1123461123, value)
    }

/**
 * 点击状态
 */
private fun View.clickEnable(): Boolean {
    var flag = false
    val currentClickTime = System.currentTimeMillis()
    if (currentClickTime - triggerLastTime >= triggerDelay) {
        flag = true
    }
    triggerLastTime = currentClickTime
    return flag
}