package com.fanchen.mbase.dialog

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView

/**
 * Material Design Dialog
 *
 * @author fanchen
 */
open class MaterialDialog : BaseAlertDialog<MaterialDialog> {

    private var vi1: Int = 0
    private var vi2: Int = 0

    constructor(context: Context?) : this(context, BaseDialog.DEFAULT_STYLE)

    constructor(context: Context?, themeResId: Int) : this(context, null, themeResId)

    constructor(context: Context?, resId: Int, themeResId: Int) : this(context, LayoutInflater.from(context).inflate(resId, null), themeResId)

    constructor(context: Context?, view: View?, style: Int) : super(context, style) {
        this.view = view
        titleTextColor = Color.parseColor("#DE000000")
        titleTextSize_SP = 22f
        contentTextColor = Color.parseColor("#8a000000")
        contentTextSize_SP = 16f
        leftBtnTextColor = Color.parseColor("#383838")
        rightBtnTextColor = Color.parseColor("#468ED0")
        middleBtnTextColor = Color.parseColor("#00796B")
    }

    override fun onCreateView(): View {
        tv_title?.gravity = Gravity.CENTER_VERTICAL
        tv_title?.setPadding(dp2px(20f), dp2px(20f), dp2px(20f), dp2px(0f))
        tv_title?.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        ll_container?.addView(tv_title)
        if (view != null) {
            val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0)
            layoutParams.weight = 1f
            layoutParams.leftMargin = dp2px(10f)
            layoutParams.rightMargin = dp2px(10f)
            view!!.layoutParams = layoutParams
            ll_container?.addView(view)
        } else {
            val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0)
            layoutParams.weight = 1f
            tv_content?.setPadding(dp2px(20f), dp2px(20f), dp2px(20f), dp2px(20f))
            tv_content?.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)
            val scrollView = ScrollView(context)
            scrollView.addView(tv_content)
            scrollView.layoutParams = layoutParams
            ll_container?.addView(scrollView)
        }
        ll_btns?.gravity = Gravity.RIGHT
        ll_btns?.addView(tv_btn_left)
        ll_btns?.addView(tv_btn_middle)
        ll_btns?.addView(tv_btn_right)
        tv_btn_left?.setPadding(dp2px(15f), dp2px(8f), dp2px(15f), dp2px(8f))
        tv_btn_right?.setPadding(dp2px(15f), dp2px(8f), dp2px(15f), dp2px(8f))
        tv_btn_middle?.setPadding(dp2px(15f), dp2px(8f), dp2px(15f), dp2px(8f))
        ll_btns?.setPadding(dp2px(20f), dp2px(0f), dp2px(10f), dp2px(10f))
        ll_container?.addView(ll_btns)
        return ll_container!!
    }

    override fun setUiBeforShow() {
        super.setUiBeforShow()
        val radius = dp2px(cornerRadius_DP).toFloat()
        ll_container?.setBackgroundDrawable(Utils.cornerDrawable(bgColor, radius))
        if (vi1 == View.GONE) {
            ll_container?.removeView(tv_title)
        }
        if (vi2 == View.GONE) {
            ll_container?.removeView(ll_btns)
        }
        tv_btn_left?.setBackgroundDrawable(Utils.btnSelector(radius, bgColor, btnPressColor, -2))
        tv_btn_right?.setBackgroundDrawable(Utils.btnSelector(radius, bgColor, btnPressColor, -2))
        tv_btn_middle?.setBackgroundDrawable(Utils.btnSelector(radius, bgColor, btnPressColor, -2))
    }

    fun setTitleVisble(vis: Int) {
        vi1 = vis
    }

    fun setButtonVisble(vis: Int) {
        vi2 = vis
    }

    override fun dismiss() {
        if (!cancel) {
            super.dismiss()
        } else {
            if (touchView != null)
                super.dismiss()
            else {
                super.dismiss()
            }
        }
    }

    companion object {

        /**
         * @param context
         * @param msg
         */
        fun show(context: Context, msg: CharSequence) {
            val dialog = MaterialDialog(context)
            dialog.setTitleVisble(View.GONE)
            dialog.content(msg)
            dialog.btnNum(1)
            dialog.btnText("关闭")
            dialog.setButtonClickListener(object : OnButtonClickListener {

                override fun onButtonClick(dialog: BaseAlertDialog<*>?, btn: Int) {
                    dialog?.dismiss()
                }

            })
            dialog.show()
        }
    }
}
