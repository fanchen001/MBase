package com.fanchen.mbase.dialog

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout

import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import com.fanchen.mbase.util.DisplayUtil

/**
 * NormalDialog
 * 默认弹窗
 */
class NormalDialog : BaseAlertDialog<NormalDialog> {
    /*** title underline */
    private var v_line_title: View? = null
    /*** vertical line between btns */
    private var v_line_vertical: View? = null
    /*** vertical line between btns */
    private var v_line_vertical2: View? = null
    /*** horizontal line above btns */
    private var v_line_horizontal: View? = null
    /*** title underline color(标题下划线颜色) */
    private var titleLineColor = Color.parseColor("#DCDCDC")
    /*** title underline height(标题下划线高度) */
    private var titleLineHeight_DP = 1
    /*** btn divider line color(对话框之间的分割线颜色(水平+垂直)) */
    private var dividerColor = Color.parseColor("#DCDCDC")
    private var style = STYLE_ONE

    constructor(context: Context?) : this(context, DEFAULT_STYLE)

    constructor(context: Context?, themeResId: Int) : this(context, null,themeResId)

    constructor(context: Context?, view: View?, style:Int) : super(context, style) {
        this.view = view
        /** default value */
        titleTextColor = Color.parseColor("#383838")
        titleTextSize_SP = 20f
        contentTextColor = Color.parseColor("#383838")
        contentTextSize_SP = 16f
        leftBtnTextColor = Color.parseColor("#AA000000")
        rightBtnTextColor = Color.parseColor("#AA000000")
        middleBtnTextColor = Color.parseColor("#AA000000")
        /** default value */
    }

    override fun onCreateView(): View {
        /** title  */
        val layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        layoutParams.gravity = Gravity.CENTER
        tv_title?.layoutParams = layoutParams
        ll_container?.gravity = Gravity.CENTER
        ll_container?.addView(tv_title)
        /** title underline  */
        v_line_title = View(context)
        ll_container?.addView(v_line_title)
        /** content  */
        if (view != null) {
            ll_container?.addView(view)
        } else {
            view = tv_content
            tv_content?.layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            ll_container?.addView(tv_content)
        }
        v_line_horizontal = View(context)
        v_line_horizontal?.layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, 1)
        ll_container?.addView(v_line_horizontal)
        /** btns  */
        tv_btn_left?.layoutParams = LinearLayout.LayoutParams(0, dp2px(45f), 1f)
        ll_btns?.addView(tv_btn_left)
        v_line_vertical = View(context)
        v_line_vertical?.layoutParams = LinearLayout.LayoutParams(1, MATCH_PARENT)
        ll_btns?.addView(v_line_vertical)
        tv_btn_middle?.layoutParams = LinearLayout.LayoutParams(0, dp2px(45f), 1f)
        ll_btns?.addView(tv_btn_middle)
        v_line_vertical2 = View(context)
        v_line_vertical2?.layoutParams = LinearLayout.LayoutParams(1, MATCH_PARENT)
        ll_btns?.addView(v_line_vertical2)
        tv_btn_right?.layoutParams = LinearLayout.LayoutParams(0, dp2px(45f), 1f)
        ll_btns?.addView(tv_btn_right)
        ll_container?.addView(ll_btns)
        return ll_container!!
    }

    override fun dismiss() {
        if (!cancel) {
            super.dismiss()
        } else {
            if (touchView != null)
                super.dismiss()
        }
        //手动调用 Activity  onPause
        Utils.invokeMethod(context, "onResume")
    }

    override fun setUiBeforShow() {
        super.setUiBeforShow()
        /** title  */
        if (style == STYLE_ONE) {
            tv_title?.minHeight = dp2px(48f)
            tv_title?.gravity = Gravity.CENTER_VERTICAL
            tv_title?.setPadding(dp2px(15f), dp2px(5f), dp2px(0f), dp2px(5f))
            tv_title?.visibility = if (isTitleShow) View.VISIBLE else View.GONE
        } else if (style == STYLE_TWO) {
            tv_title?.gravity = Gravity.CENTER
            tv_title?.setPadding(dp2px(0f), dp2px(15f), dp2px(0f), dp2px(0f))
        }
        /** title underline  */
        v_line_title?.layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, titleLineHeight_DP)
        v_line_title?.setBackgroundColor(titleLineColor)
        v_line_title?.visibility = if (isTitleShow && style == STYLE_ONE) View.VISIBLE else View.GONE
        /** content  */
        if (style == STYLE_ONE) {
            tv_content?.setPadding(dp2px(15f), dp2px(10f), dp2px(15f), dp2px(10f))
            tv_content?.minHeight = dp2px(68f)
            tv_content?.gravity = contentGravity
        } else if (style == STYLE_TWO) {
            tv_content?.setPadding(dp2px(15f), dp2px(7f), dp2px(15f), dp2px(20f))
            tv_content?.minHeight = dp2px(56f)
            tv_content?.gravity = Gravity.CENTER
        }
        /** btns  */
        v_line_horizontal?.setBackgroundColor(dividerColor)
        v_line_vertical?.setBackgroundColor(dividerColor)
        v_line_vertical2?.setBackgroundColor(dividerColor)
        if (btnNum == 1) {
            tv_btn_left?.visibility = View.GONE
            tv_btn_right?.visibility = View.GONE
            v_line_vertical?.visibility = View.GONE
            v_line_vertical2?.visibility = View.GONE
        } else if (btnNum == 2) {
            tv_btn_middle?.visibility = View.GONE
            v_line_vertical?.visibility = View.GONE
        }
        /**set background color and corner radius  */
        val radius = dp2px(cornerRadius_DP).toFloat()
        ll_container?.setBackgroundDrawable(Utils.cornerDrawable(bgColor, radius))
        tv_btn_left?.setBackgroundDrawable(Utils.btnSelector(radius, bgColor, btnPressColor, 0))
        tv_btn_right?.setBackgroundDrawable(Utils.btnSelector(radius, bgColor, btnPressColor, 1))
        tv_btn_middle?.setBackgroundDrawable(Utils.btnSelector(if (btnNum == 1) radius else 0f, bgColor, btnPressColor, -1))
    }

    /**
     * set style(设置style)
     */
    fun style(style: Int): NormalDialog {
        this.style = style
        return this
    }

    /**
     * set title underline color(设置标题下划线颜色)
     */
    fun titleLineColor(titleLineColor: Int): NormalDialog {
        this.titleLineColor = titleLineColor
        return this
    }

    /**
     * set title underline height(设置标题下划线高度)
     */
    fun titleLineHeight(titleLineHeight_DP: Int): NormalDialog {
        this.titleLineHeight_DP = titleLineHeight_DP
        return this
    }

    /**
     * set divider color between btns(设置btn分割线的颜色)
     */
    fun dividerColor(dividerColor: Int): NormalDialog {
        this.dividerColor = dividerColor
        return this
    }

    override fun show() {
        super.show()
        //手动调用 Activity  onPause
        Utils.invokeMethod(context, "onPause")
    }



    class TelClickListener(private val context: Context, private val phone: String) : OnButtonClickListener {

        override fun onButtonClick(dialog: BaseAlertDialog<*>?, btn: Int) {
            dialog?.dismiss()
            if (btn == OnButtonClickListener.RIGHT) {
                try {
                    val parse = Uri.parse("tel:$phone")
                    val intent = Intent(Intent.ACTION_DIAL, parse)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    companion object {

        const val STYLE_ONE = 0
        const val STYLE_TWO = 1

        /**
         * showDialog
         *
         * @param context
         * @param title
         * @param content
         * @param cl
         * @param listener
         */
        fun show(context: Context, title: String, content: String, cl: Boolean, listener: OnButtonClickListener) {
            val dialog = NormalDialog(context)
            dialog.title(title)
            dialog.content(content)
            dialog.setCanceledOnTouchOutside(!cl)
            dialog.setCancelable(cl)
            dialog.setButtonClickListener(listener)
            dialog.show()
        }

        fun showConfirm(context: Context, content: String, listener: OnConfirmListener) {
            val dialog = NormalDialog(context)
            dialog.content(content)
            dialog.setCanceledOnTouchOutside(true)
            dialog.setCancelable(false)
            dialog.setOnConfirmClickListener(listener)
            dialog.show()
        }

        fun showInput(context: Context, title: String, content: String, listener: OnConfirmListener) {
            val editText = EditText(context)
            editText.setText(content)
            editText.hint = "请输入$title"
            val dp15 = DisplayUtil.dp2px( context,15f)
            editText.setPadding(dp15, dp15, dp15, dp15)
            editText.setBackgroundColor(Color.TRANSPARENT)
            val dialog = NormalDialog(context, editText,BaseDialog.DEFAULT_STYLE)
            dialog.title(title)
            dialog.setCanceledOnTouchOutside(true)
            dialog.setCancelable(false)
            dialog.setOnConfirmClickListener(listener)
            dialog.show()
        }

        fun showInput(context: Context, title: String, listener: OnConfirmListener) {
            showInput(context, title, "", listener)
        }

        fun showAlert(context: Context, drawable: Drawable) {
            val imageView = ImageView(context)
            imageView.setImageDrawable(drawable)
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            imageView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            val dialog = NormalDialog(context, imageView,BaseDialog.DEFAULT_STYLE)
            dialog.btnNum(1)
            dialog.btnText("确定")
            dialog.setCanceledOnTouchOutside(true)
            dialog.setCancelable(false)
            dialog.show()
        }

        @JvmOverloads
        fun showAlert(context: Context, content: String, listener: OnConfirmListener? = null) {
            val dialog = NormalDialog(context)
            dialog.btnNum(1)
            dialog.btnText("确定")
            dialog.content(content)
            dialog.setCanceledOnTouchOutside(true)
            dialog.setCancelable(false)
            if (listener != null) dialog.setOnConfirmClickListener(listener)
            dialog.show()
        }

        /**
         * showDialog
         *
         * @param context
         * @param title
         * @param content
         * @param listener
         */
        fun show(context: Context, title: String, content: String, listener: OnButtonClickListener) {
            val dialog = NormalDialog(context)
            dialog.title(title)
            dialog.content(content)
            dialog.setButtonClickListener(listener)
            dialog.show()
        }

        /**
         * showDialog
         *
         * @param context
         * @param content
         * @param btns
         * @param listener
         */
        fun show(context: Context, content: String, btns: Array<String>, listener: OnButtonClickListener) {
            val dialog = NormalDialog(context)
            dialog.btnText(*btns)
            dialog.btnNum(btns.size)
            dialog.content(content)
            dialog.setCancelable(false)
            dialog.setCanceledOnTouchOutside(true)
            dialog.setButtonClickListener(listener)
            dialog.show()
        }

        fun show(context: Context, title: String, view: View) {
            val dialog = NormalDialog(context, view,BaseDialog.DEFAULT_STYLE)
            dialog.btnText("确定")
            dialog.btnNum(1)
            dialog.title(title)
            dialog.show()
        }

        fun showCancelable(context: Context, content: String, l: OnButtonClickListener) {
            val dialog = NormalDialog(context)
            dialog.btnNum(1)
            dialog.setButtonClickListener(l)
            dialog.setCanceledOnTouchOutside(true)
            dialog.setCancelable(false)
            dialog.content(content)
            dialog.show()
        }

        fun show(context: Context, content: String) {
            val dialog = NormalDialog(context)
            dialog.btnNum(1)
            dialog.content(content)
            dialog.show()
        }

        /**
         * @param context
         * @param content
         * @param listener
         */
        fun show(context: Context, content: String, listener: OnButtonClickListener) {
            val dialog = NormalDialog(context)
            dialog.content(content)
            dialog.setButtonClickListener(listener)
            dialog.show()
        }

        fun showTel(context: Context, tel: String) {
            val replace = tel.replace("tel:", "")
            val listener = TelClickListener(context, replace)
            NormalDialog.show(context, String.format("确认拨打电话%s？", replace), listener)
        }

    }
}
