package com.fanchen.mbase.warp

import android.content.res.ColorStateList
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import com.fanchen.mbase.util.ValidatorUtil

/**
 * TextView 扩展
 * Created by fanchen on 2018/8/31.
 */
fun TextView.drawable(res: Int, fangxiang: Int = 1) {
    val drawable = resources.getDrawable(res) ?: return
    drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)  //此为必须写的
    when (fangxiang) {
        1 -> this.setCompoundDrawables(drawable, null, null, null)
        2 -> this.setCompoundDrawables(null, drawable, null, null)
        3 -> this.setCompoundDrawables(null, null, drawable, null)
        else -> this.setCompoundDrawables(null, null, null, drawable)
    }
}

/**
 *
 */
fun TextView.getIntValue(def: Int = 0): Int {
    try {
        return getTextTrim().toInt()
    } catch (e: NumberFormatException) {
        e.printStackTrace()
    }
    return def
}

fun TextView.getDoubleValue(def: Double = 0.0): Double {
    try {
        return getTextTrim().toDouble()
    } catch (e: NumberFormatException) {
        e.printStackTrace()
    }
    return def
}

fun TextView.getFloatValue(def: Float = 0f): Float {
    try {
        return getTextTrim().toFloat()
    } catch (e: NumberFormatException) {
        e.printStackTrace()
    }
    return def
}

fun TextView.getLongValue(def: Long = 0): Long {
    try {
        return getTextTrim().toLong()
    } catch (e: NumberFormatException) {
        e.printStackTrace()
    }
    return def
}

fun TextView.getTextTrim(): String {
    return text.toString().trim()
}

fun TextView.isNotEmpty(): Boolean {
    return getTextTrim().isNotEmpty()
}

fun TextView.checkEmpty(msg: String? = null): Boolean {
    return if (isNotEmpty()) {
        false
    } else {
        if (msg?.isEmpty() == false) {
            context?.showToast(msg)
        } else {
            val replace = hint.toString().replace("请输入", "")
            context?.showToast(replace + "不能为空")
        }
        true
    }
}

fun TextView.checkPhone(): Boolean {
    if (!isNotEmpty()) {
        context?.showToast("手机号不能为空")
        return false
    } else if (!ValidatorUtil.isMobileExact(getTextTrim())) {
        context?.showToast("手机号不正确")
        return false
    }
    return true
}

fun TextView.checkEmail(): Boolean {
    if (!isNotEmpty()) {
        context?.showToast("Email不能为空")
        return false
    } else if (!ValidatorUtil.isEmail(getTextTrim())) {
        context?.showToast("Email不正确")
        return false
    }
    return true
}

fun TextView.checkEquals(text: TextView): Boolean {
    if (getTextTrim() != text.getTextTrim()) {
        context?.showToast("两次输入不一致")
        return false
    }
    return true
}


fun TextView.setTextColorStateListId(resId: Int) {
    try {
        val stateList = ColorStateList.createFromXml(resources, resources.getXml(resId))
        this.setTextColor(stateList)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

open class TextWatcherImpl : TextWatcher {

    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (s != null) {
            onTextChanged(s.toString())
        }
    }

    open fun onTextChanged(s: String) {

    }
}

open class EnabledTextWatcher(private val view: View, vararg texts: TextView) : TextWatcherImpl() {
    private var texts: List<TextView>? = null

    init {
        this.texts = texts.filter { true }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        var isNotEmpty = true
        texts?.forEach { if (!it.isNotEmpty()) isNotEmpty = false }
        view.isEnabled = isNotEmpty
    }

}
