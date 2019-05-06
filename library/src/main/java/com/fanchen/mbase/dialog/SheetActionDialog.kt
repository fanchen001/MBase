package com.fanchen.mbase.dialog

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.LayoutAnimationController
import android.view.animation.TranslateAnimation
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import android.widget.LinearLayout.LayoutParams

import java.util.ArrayList

/***
 * Dialog like
 * iOS ActionSheet(iOS风格对话框)
 */
class SheetActionDialog : BottomBaseDialog<SheetActionDialog> {
    /*** ListView */
    private var lv: ListView? = null
    /*** title */
    private var tv_title: TextView? = null
    /*** title underline(标题下划线) */
    private var v_line_title: View? = null
    /*** cancel button(取消按钮) */
    private var tv_cancel: TextView? = null
    /*** corner radius,dp(圆角程度,单位dp) */
    private var cornerRadius_DP = 5f
    /*** title background color(标题背景颜色) */
    private var titleBgColor = Color.parseColor("#ddffffff")
    /*** title text(标题) */
    private var title = "提示"
    /*** title height(标题栏高度) */
    private var titleHeight = 48f
    /*** title textcolor(标题颜色) */
    private var titleTextColor = Color.parseColor("#8F8F8F")
    /*** title textsize(标题字体大小,单位sp) */
    private var titleTextSize_SP = 17.5f
    /*** ListView background color(ListView背景色) */
    private var lvBgColor = Color.parseColor("#ddffffff")
    /*** divider color(ListView divider颜色) */
    private var dividerColor = Color.parseColor("#D7D7D9")
    /*** divider height(ListView divider高度) */
    private var dividerHeight_DP = 0.8f
    /*** item press color(ListView item按住颜色) */
    private var itemPressColor = Color.parseColor("#ffcccccc")
    /*** item textcolor(ListView item文字颜色) */
    private var itemTextColor = Color.parseColor("#44A2FF")
    /*** item textsize(ListView item文字大小) */
    private var itemTextSize_SP = 17.5f
    /*** item height(ListView item高度) */
    private var itemHeight_DP = 48f
    /*** enable title show(是否显示标题) */
    private var isTitleShow = true
    /*** adapter(自定义适配器) */
    private var adapter: BaseAdapter? = null
    /*** operation items(操作items) */
    private val contents = ArrayList<DialogMenuItem>()
    private var onOperItemClickL: com.fanchen.mbase.dialog.OnItemClickListener? = null
    private var lac: LayoutAnimationController? = null

    private val warpListener = OnItemClickListener { parent, view, position, id ->
        onOperItemClickL?.onItemClick(this@SheetActionDialog, parent, view, position, id)
    }

    private val dismissListener = View.OnClickListener { dismiss() }

    fun setOnOperItemClickL(onOperItemClickL: com.fanchen.mbase.dialog.OnItemClickListener): SheetActionDialog {
        this.onOperItemClickL = onOperItemClickL
        return this
    }

    constructor(context: Context?, baseItems: ArrayList<DialogMenuItem>, animateView: View?, style: Int = BaseDialog.DEFAULT_STYLE) : super(context, animateView, style) {
        this.contents.addAll(baseItems)
        init()
    }

    constructor(context: Context?, items: Array<String>, animateView: View?, style: Int = BaseDialog.DEFAULT_STYLE) : super(context, animateView, style) {
        for (item in items) {
            val customBaseItem = DialogMenuItem(item, 0)
            contents.add(customBaseItem)
        }
        init()
    }

    constructor(context: Context?, adapter: BaseAdapter, animateView: View?, style: Int = BaseDialog.DEFAULT_STYLE) : super(context, animateView, style) {
        this.adapter = adapter
        init()
    }

    private fun init() {
        widthScale(0.95f)
        /** LayoutAnimation  */
        val animation = TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 6f, Animation.RELATIVE_TO_SELF, 0f)
        animation.interpolator = DecelerateInterpolator()
        animation.duration = 350
        animation.startOffset = 150
        lac = LayoutAnimationController(animation, 0.12f)
        lac?.interpolator = DecelerateInterpolator()
    }

    override fun onCreateView(): View {
        val ll_container = LinearLayout(context)
        ll_container.orientation = LinearLayout.VERTICAL
        ll_container.setBackgroundColor(Color.TRANSPARENT)
        /** title  */
        tv_title = TextView(context)
        tv_title?.gravity = Gravity.CENTER
        tv_title?.setPadding(dp2px(10f), dp2px(5f), dp2px(10f), dp2px(5f))
        val params = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        params.topMargin = dp2px(20f)
        ll_container.addView(tv_title, params)
        /** title underline  */
        v_line_title = View(context)
        ll_container.addView(v_line_title)
        /** listview  */
        lv = ListView(context)
        lv?.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1f)
        lv?.cacheColorHint = Color.TRANSPARENT
        lv?.setFadingEdgeLength(0)
        lv?.isVerticalScrollBarEnabled = false
        lv?.selector = ColorDrawable(Color.TRANSPARENT)
        ll_container.addView(lv)
        /** cancel btn  */
        tv_cancel = TextView(context)
        tv_cancel?.gravity = Gravity.CENTER
        val lp = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        lp.topMargin = dp2px(7f)
        lp.bottomMargin = dp2px(7f)
        tv_cancel?.layoutParams = lp
        ll_container.addView(tv_cancel)
        return ll_container
    }

    override fun setUiBeforShow() {
        /** title  */
        val radius = dp2px(cornerRadius_DP).toFloat()
        tv_title?.height = dp2px(titleHeight)
        tv_title?.setBackgroundDrawable(Utils.cornerDrawable(titleBgColor, floatArrayOf(radius, radius, radius, radius, 0f, 0f, 0f, 0f)))
        tv_title?.text = title
        tv_title?.setTextSize(TypedValue.COMPLEX_UNIT_SP, titleTextSize_SP)
        tv_title?.setTextColor(titleTextColor)
        tv_title?.visibility = if (isTitleShow) View.VISIBLE else View.GONE
        /** title underline  */
        v_line_title?.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, dp2px(dividerHeight_DP))
        v_line_title?.setBackgroundColor(dividerColor)
        v_line_title?.visibility = if (isTitleShow) View.VISIBLE else View.GONE
        /** cancel btn  */
        tv_cancel?.height = dp2px(itemHeight_DP)
        tv_cancel?.text = "取消"
        tv_cancel?.setTextSize(TypedValue.COMPLEX_UNIT_SP, itemTextSize_SP)
        tv_cancel?.setTextColor(itemTextColor)
        tv_cancel?.setBackgroundDrawable(Utils.listItemSelector(radius, lvBgColor, itemPressColor, 1, 0))
        tv_cancel?.setOnClickListener(dismissListener)
        /** listview  */
        lv?.divider = ColorDrawable(dividerColor)
        lv?.dividerHeight = dp2px(dividerHeight_DP)
        if (isTitleShow) {
            lv?.setBackgroundDrawable(Utils.cornerDrawable(lvBgColor, floatArrayOf(0f, 0f, 0f, 0f, radius, radius, radius, radius)))
        } else {
            lv?.setBackgroundDrawable(Utils.cornerDrawable(lvBgColor, radius))
        }
        if (adapter == null) {
            adapter = ListDialogAdapter()
        }
        lv?.adapter = adapter
        lv?.onItemClickListener = warpListener
        lv?.layoutAnimation = lac
    }

    /**
     * set title background color(设置标题栏背景色)
     *
     * @param titleBgColor
     * @return SheetActionDialog
     */
    fun titleBgColor(titleBgColor: Int): SheetActionDialog {
        this.titleBgColor = titleBgColor
        return this
    }

    /**
     * set title text(设置标题内容)
     *
     * @param title
     * @return SheetActionDialog
     */
    fun title(title: String): SheetActionDialog {
        this.title = title
        return this
    }

    /**
     * set titleHeight(设置标题高度)
     *
     * @param titleHeight
     * @return SheetActionDialog
     */
    fun titleHeight(titleHeight: Float): SheetActionDialog {
        this.titleHeight = titleHeight
        return this
    }

    /**
     * set title textsize(设置标题字体大小)
     *
     * @param titleTextSize_SP
     * @return SheetActionDialog
     */
    fun titleTextSize_SP(titleTextSize_SP: Float): SheetActionDialog {
        this.titleTextSize_SP = titleTextSize_SP
        return this
    }

    /**
     * set title textcolor(设置标题字体颜色)
     *
     * @param titleTextColor
     * @return SheetActionDialog
     */
    fun titleTextColor(titleTextColor: Int): SheetActionDialog {
        this.titleTextColor = titleTextColor
        return this
    }

    /**
     * enable title show(设置标题是否显示)
     *
     * @param isTitleShow
     * @return SheetActionDialog
     */
    fun isTitleShow(isTitleShow: Boolean): SheetActionDialog {
        this.isTitleShow = isTitleShow
        return this
    }

    /**
     * set ListView background color(设置ListView背景)
     *
     * @param lvBgColor
     * @return SheetActionDialog
     */
    fun lvBgColor(lvBgColor: Int): SheetActionDialog {
        this.lvBgColor = lvBgColor
        return this
    }

    /**
     * set corner radius(设置圆角程度,单位dp)
     *
     * @param cornerRadius_DP
     * @return SheetActionDialog
     */
    fun cornerRadius(cornerRadius_DP: Float): SheetActionDialog {
        this.cornerRadius_DP = cornerRadius_DP
        return this
    }

    /**
     * set divider color(ListView divider颜色)
     *
     * @param dividerColor
     * @return SheetActionDialog
     */
    fun dividerColor(dividerColor: Int): SheetActionDialog {
        this.dividerColor = dividerColor
        return this
    }

    /**
     * set divider height(ListView divider高度)
     *
     * @return SheetActionDialog
     */
    fun dividerHeight(dividerHeight_DP: Float): SheetActionDialog {
        this.dividerHeight_DP = dividerHeight_DP
        return this
    }

    /**
     * set item press color(item按住颜色)
     *
     * @param itemPressColor
     * @return SheetActionDialog
     */
    fun itemPressColor(itemPressColor: Int): SheetActionDialog {
        this.itemPressColor = itemPressColor
        return this
    }

    /**
     * set item textcolor(item字体颜色)
     *
     * @param itemTextColor
     * @return SheetActionDialog
     */
    fun itemTextColor(itemTextColor: Int): SheetActionDialog {
        this.itemTextColor = itemTextColor
        return this
    }

    /**
     * set item textsize(item字体大小)
     *
     * @param itemTextSize_SP
     * @return SheetActionDialog
     */
    fun itemTextSize(itemTextSize_SP: Float): SheetActionDialog {
        this.itemTextSize_SP = itemTextSize_SP
        return this
    }

    /**
     * set item height(item高度)
     *
     * @param itemHeight_DP
     * @return SheetActionDialog
     */
    fun itemHeight(itemHeight_DP: Float): SheetActionDialog {
        this.itemHeight_DP = itemHeight_DP
        return this
    }

    /**
     * set layoutAnimation(设置layout动画 ,传入null将不显示layout动画)
     *
     * @param lac
     * @return SheetActionDialog
     */
    fun layoutAnimation(lac: LayoutAnimationController): SheetActionDialog {
        this.lac = lac
        return this
    }

    internal inner class ListDialogAdapter : BaseAdapter() {

        override fun getCount(): Int {
            return contents.size
        }

        override fun getItem(position: Int): Any? {
            return null
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val item = contents[position]
            val ll_item = LinearLayout(context)
            ll_item.orientation = LinearLayout.HORIZONTAL
            ll_item.gravity = Gravity.CENTER_VERTICAL
            val iv_item = ImageView(context)
            iv_item.setPadding(0, 0, dp2px(15f), 0)
            ll_item.addView(iv_item)
            val tv_item = TextView(context)
            tv_item.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            tv_item.setSingleLine(true)
            tv_item.gravity = Gravity.CENTER
            tv_item.setTextColor(itemTextColor)
            tv_item.setTextSize(TypedValue.COMPLEX_UNIT_SP, itemTextSize_SP)
            tv_item.height = dp2px(itemHeight_DP)
            ll_item.addView(tv_item)
            val radius = dp2px(cornerRadius_DP).toFloat()
            if (isTitleShow) {
                ll_item.setBackgroundDrawable(Utils.listItemSelector(radius, Color.TRANSPARENT, itemPressColor, position == contents.size - 1))
            } else {
                ll_item.setBackgroundDrawable(Utils.listItemSelector(radius, Color.TRANSPARENT, itemPressColor, contents.size, position))
            }
            iv_item.setImageResource(item.resId)
            tv_item.text = item.operName
            iv_item.visibility = if (item.resId == 0) View.GONE else View.VISIBLE
            return ll_item
        }
    }

    class DialogMenuItem {

        var operName: String
        var resId: Int = 0

        constructor(operName: String, resId: Int) {
            this.operName = operName
            this.resId = resId
        }

        constructor(operName: Any, resId: Int) {
            this.operName = operName.toString()
            this.resId = resId
        }

    }

    companion object {

        /**
         * @param context
         * @param title
         * @param titles
         * @param l
         * @return
         */
        fun show(context: Context, title: String? = null, titles: Array<String>, l: ((BaseDialog<*>?, AdapterView<*>?, View?, Int, Long) -> Unit)?): SheetActionDialog {
            return show(context, title, titles, object : com.fanchen.mbase.dialog.OnItemClickListener {

                override fun onItemClick(dialog: BaseDialog<*>?, parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    if (l == null) {
                        dialog?.dismiss()
                    } else {
                        l.invoke(dialog, parent, view, position, id)
                    }
                }

            })
        }

        /**
         * @param context
         * @param title
         * @param titles
         * @param l
         * @return
         */
        fun show(context: Context, title: String? = null, titles: Array<String>, l: com.fanchen.mbase.dialog.OnItemClickListener? = null): SheetActionDialog {
            val sheetActionDialog = SheetActionDialog(context, titles, null)
            if (l != null) sheetActionDialog.setOnOperItemClickL(l)
            if (title != null) sheetActionDialog.title(title)
            sheetActionDialog.show()
            return sheetActionDialog
        }
    }
}
