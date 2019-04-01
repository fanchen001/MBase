package com.fanchen.mbase.dialog

import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView

/**
 * @param <T>
</T> */
abstract class BaseAlertDialog<T : BaseAlertDialog<T>> : BaseDialog<T> {
    /*** container */
    protected var ll_container: LinearLayout? = null
    /*** title */
    protected var tv_title: TextView? = null
    /*** title content(标题) */
    protected var title: String? = ""
    /*** title textcolor(标题颜色) */
    protected var titleTextColor: Int = 0
    /*** title textsize(标题字体大小,单位sp) */
    protected var titleTextSize_SP: Float = 0.toFloat()
    /*** enable title show(是否显示标题) */
    protected var isTitleShow = true
    /*** content */
    protected var tv_content: TextView? = null
    /*** content text */
    protected var content: CharSequence? = ""
    /*** show gravity of content(正文内容显示位置) */
    protected var contentGravity = Gravity.CENTER_VERTICAL
    /*** content textcolor(正文字体颜色) */
    protected var contentTextColor: Int = 0
    /*** content textsize(正文字体大小) */
    protected var contentTextSize_SP: Float = 0.toFloat()
    /*** num of btns, [1,3] */
    protected var btnNum = 2
    /*** btn container */
    protected var ll_btns: LinearLayout? = null
    /*** btns */
    protected var tv_btn_left: TextView? = null
    protected var tv_btn_right: TextView? = null
    protected var tv_btn_middle: TextView? = null
    /*** btn text(按钮内容) */
    protected var btnLeftText = "取消"
    protected var btnRightText = "确定"
    protected var btnMiddleText = "继续"
    /*** btn textcolor(按钮字体颜色) */
    protected var leftBtnTextColor: Int = 0
    protected var rightBtnTextColor: Int = 0
    protected var middleBtnTextColor: Int = 0
    /*** btn textsize(按钮字体大小) */
    protected var leftBtnTextSize_SP = 15f
    protected var rightBtnTextSize_SP = 15f
    protected var middleBtnTextSize_SP = 15f
    /*** btn press color(按钮点击颜色) */
    protected var btnPressColor = Color.parseColor("#E3E3E3")// #85D3EF,#ffcccccc,#E3E3E3
    /*** left btn click listener(按钮接口) */
    protected var onBtnClickL: OnButtonClickListener? = null
    protected var onConfirmClickL: OnConfirmListener? = null
    /*** corner radius,dp(圆角程度,单位dp) */
    protected var cornerRadius_DP = 3f
    /*** background color(背景颜色) */
    protected var bgColor = Color.parseColor("#ffffff")

    protected var touchView: View? = null
    protected var view: View? = null

    private val btnListener = View.OnClickListener { v ->
        touchView = v
        if (onConfirmClickL != null) {
            dismiss()
            if (btnNum == 1 || tv_btn_right == v)
                onConfirmClickL?.onConfirm(view, this@BaseAlertDialog)
        } else if (onBtnClickL != null && tv_btn_left == v) {
            onBtnClickL?.onButtonClick(this@BaseAlertDialog, OnButtonClickListener.LIFT)
        } else if (onBtnClickL != null && tv_btn_right == v) {
            onBtnClickL?.onButtonClick(this@BaseAlertDialog, OnButtonClickListener.RIGHT)
        } else if (onBtnClickL != null && tv_btn_middle == v) {
            onBtnClickL?.onButtonClick(this@BaseAlertDialog, OnButtonClickListener.CENTRE)
        } else {
            dismiss()
        }
    }

    constructor(context: Context?) : this(context, BaseDialog.DEFAULT_STYLE)

    constructor(context: Context?, themeResId: Int) : super(context, themeResId) {
        widthScale(0.88f)
        ll_container = LinearLayout(context)
        ll_container?.orientation = LinearLayout.VERTICAL
        /** title  */
        tv_title = TextView(context)
        /** content  */
        tv_content = TextView(context)
        /** btns  */
        ll_btns = LinearLayout(context)
        ll_btns?.orientation = LinearLayout.HORIZONTAL
        tv_btn_left = TextView(context)
        tv_btn_left?.gravity = Gravity.CENTER
        tv_btn_middle = TextView(context)
        tv_btn_middle?.gravity = Gravity.CENTER
        tv_btn_right = TextView(context)
        tv_btn_right?.gravity = Gravity.CENTER
    }

    override fun setUiBeforShow() {
        /** title  */
        tv_title?.visibility = if (isTitleShow) View.VISIBLE else View.GONE
        tv_title?.text = if (TextUtils.isEmpty(title)) "温馨提示" else title
        tv_title?.setTextColor(titleTextColor)
        tv_title?.setTextSize(TypedValue.COMPLEX_UNIT_SP, titleTextSize_SP)
        /** content  */
        tv_content?.gravity = contentGravity
        tv_content?.text = content
        tv_content?.setTextColor(contentTextColor)
        tv_content?.setTextSize(TypedValue.COMPLEX_UNIT_SP, contentTextSize_SP)
        tv_content?.setLineSpacing(0f, 1.3f)
        /** btns  */
        tv_btn_left?.text = btnLeftText
        tv_btn_right?.text = btnRightText
        tv_btn_middle?.text = btnMiddleText
        tv_btn_left?.setTextColor(leftBtnTextColor)
        tv_btn_right?.setTextColor(rightBtnTextColor)
        tv_btn_middle?.setTextColor(middleBtnTextColor)
        tv_btn_left?.setTextSize(TypedValue.COMPLEX_UNIT_SP, leftBtnTextSize_SP)
        tv_btn_right?.setTextSize(TypedValue.COMPLEX_UNIT_SP, rightBtnTextSize_SP)
        tv_btn_middle?.setTextSize(TypedValue.COMPLEX_UNIT_SP, middleBtnTextSize_SP)
        if (btnNum == 1) {
            tv_btn_left?.visibility = View.GONE
            tv_btn_right?.visibility = View.GONE
        } else if (btnNum == 2) {
            tv_btn_middle?.visibility = View.GONE
        }
        tv_btn_left?.setOnClickListener(btnListener)
        tv_btn_right?.setOnClickListener(btnListener)
        tv_btn_middle?.setOnClickListener(btnListener)
    }

    fun <T : View> findView(id: Int): T? {
        return if (view == null) {
            null
        } else view!!.findViewById<View>(id) as T
    }


    /**
     * set title text(设置标题内容) @return MaterialDialog
     */
    open fun title(title: String): T {
        this.title = title
        return this as T
    }

    /**
     * set title textcolor(设置标题字体颜色)
     */
    fun titleTextColor(titleTextColor: Int): T {
        this.titleTextColor = titleTextColor
        return this as T
    }

    /**
     * set title textsize(设置标题字体大小)
     */
    fun titleTextSize(titleTextSize_SP: Float): T {
        this.titleTextSize_SP = titleTextSize_SP
        return this as T
    }

    /**
     * enable title show(设置标题是否显示)
     */
    fun isTitleShow(isTitleShow: Boolean): T {
        this.isTitleShow = isTitleShow
        return this as T
    }

    /**
     * set content text(设置正文内容)
     */
    fun content(content: CharSequence): T {
        this.content = content
        return this as T
    }

    /**
     * set content gravity(设置正文内容,显示位置)
     */
    fun contentGravity(contentGravity: Int): T {
        this.contentGravity = contentGravity
        return this as T
    }

    /**
     * set content textcolor(设置正文字体颜色)
     */
    fun contentTextColor(contentTextColor: Int): T {
        this.contentTextColor = contentTextColor
        return this as T
    }

    /**
     * set content textsize(设置正文字体大小,单位sp)
     */
    fun contentTextSize(contentTextSize_SP: Float): T {
        this.contentTextSize_SP = contentTextSize_SP
        return this as T
    }

    /**
     * set btn text(设置按钮文字内容) btnTexts size 1, middle btnTexts size 2, left
     * right btnTexts size 3, left right middle
     */
    fun btnNum(btnNum: Int): T {
        if (btnNum < 1) {
            this.btnNum = 1
        } else if (btnNum > 3) {
            this.btnNum = 3
        } else {
            this.btnNum = btnNum
        }
        return this as T
    }

    /**
     * set btn text(设置按钮文字内容) btnTexts size 1, middle btnTexts size 2, left
     * right btnTexts size 3, left right middle
     */
    fun btnText(vararg btnTexts: String): T {
        if (btnTexts.isEmpty()) {
            this.btnMiddleText = "确定"
        } else if (btnTexts.size > 3) {
            this.btnLeftText = btnTexts[0]
            this.btnRightText = btnTexts[1]
            this.btnMiddleText = btnTexts[2]
        } else if (btnTexts.size == 1) {
            this.btnMiddleText = btnTexts[0]
        } else if (btnTexts.size == 2) {
            this.btnLeftText = btnTexts[0]
            this.btnRightText = btnTexts[1]
        } else if (btnTexts.size == 3) {
            this.btnLeftText = btnTexts[0]
            this.btnRightText = btnTexts[1]
            this.btnMiddleText = btnTexts[2]
        }
        return this as T
    }

    /**
     * set btn textcolor(设置按钮字体颜色) btnTextColors size 1, middle btnTextColors
     * size 2, left right btnTextColors size 3, left right middle
     */
    fun btnTextColor(vararg btnTextColors: Int): T {
        if (btnTextColors.isEmpty()) {
        } else if (btnTextColors.size > 3) {
            this.leftBtnTextColor = btnTextColors[0]
            this.rightBtnTextColor = btnTextColors[1]
            this.middleBtnTextColor = btnTextColors[2]
        } else if (btnTextColors.size == 1) {
            this.middleBtnTextColor = btnTextColors[0]
        } else if (btnTextColors.size == 2) {
            this.leftBtnTextColor = btnTextColors[0]
            this.rightBtnTextColor = btnTextColors[1]
        } else if (btnTextColors.size == 3) {
            this.leftBtnTextColor = btnTextColors[0]
            this.rightBtnTextColor = btnTextColors[1]
            this.middleBtnTextColor = btnTextColors[2]
        }
        return this as T
    }

    /**
     * set btn textsize(设置字体大小,单位sp) btnTextSizes size 1, middle btnTextSizes
     * size 2, left right btnTextSizes size 3, left right middle
     */
    fun btnTextSize(vararg btnTextSizes: Float): T {
        if (btnTextSizes.isEmpty()) {
        } else if (btnTextSizes.size > 3) {
            this.leftBtnTextSize_SP = btnTextSizes[0]
            this.rightBtnTextSize_SP = btnTextSizes[1]
            this.middleBtnTextSize_SP = btnTextSizes[2]
        } else if (btnTextSizes.size == 1) {
            this.middleBtnTextSize_SP = btnTextSizes[0]
        } else if (btnTextSizes.size == 2) {
            this.leftBtnTextSize_SP = btnTextSizes[0]
            this.rightBtnTextSize_SP = btnTextSizes[1]
        } else if (btnTextSizes.size == 3) {
            this.leftBtnTextSize_SP = btnTextSizes[0]
            this.rightBtnTextSize_SP = btnTextSizes[1]
            this.middleBtnTextSize_SP = btnTextSizes[2]
        }
        return this as T
    }

    /**
     * set btn press color(设置按钮点击颜色)
     */
    fun btnPressColor(btnPressColor: Int): T {
        this.btnPressColor = btnPressColor
        return this as T
    }

    /**
     * set corner radius (设置圆角程度)
     */
    fun cornerRadius(cornerRadius_DP: Float): T {
        this.cornerRadius_DP = cornerRadius_DP
        return this as T
    }

    /**
     * set backgroud color(设置背景色)
     */
    fun bgColor(bgColor: Int): T {
        this.bgColor = bgColor
        return this as T
    }

    /**
     * set btn click listener(设置按钮监听事件) onBtnClickLs size 1, middle onBtnClickLs
     * size 2, left right onBtnClickLs size 3, left right middle
     */
    fun setButtonClickListener(onBtnClickLs: OnButtonClickListener) {
        this.onBtnClickL = onBtnClickLs
    }

    fun setOnConfirmClickListener(onBtnClickLs: OnConfirmListener) {
        this.onConfirmClickL = onBtnClickLs
    }

    fun updateBtnText(vararg btnTexts: String): T {
        btnText(*btnTexts)
        tv_btn_left?.text = btnLeftText
        tv_btn_right?.text = btnRightText
        tv_btn_middle?.text = btnMiddleText
        return this as T
    }

}
