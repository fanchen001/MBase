package com.fanchen.mbase.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager.LayoutParams
import android.widget.LinearLayout

import com.nineoldandroids.animation.Animator

/**
 * @param <T>
</T> */
@Suppress("UNCHECKED_CAST")
abstract class BaseDialog<T : BaseDialog<T>> : Dialog {

    companion object {
        var DEFAULT_STYLE = 0
    }

    protected var TAG = "BaseDialog"
    /*** (DisplayMetrics)设备密度  */
    protected var dm: DisplayMetrics? = null
    /*** enable dismiss outside dialog(设置点击对话框以外区域,是否dismiss) */
    protected var cancel: Boolean = false
    /*** dialog width scale(宽度比例) */
    protected var widthScale = 1f
    /*** dialog height scale(高度比例) */
    protected var heightScale: Float = 0.toFloat()
    /*** showAnim(对话框显示动画) */
    private var showAnim: BaseAnimatorSet? = null
    /*** dismissAnim(对话框消失动画) */
    private var dismissAnim: BaseAnimatorSet? = null
    /*** top container(最上层容器) */
    protected var ll_top: LinearLayout? = null
    /*** container to control dialog height(用于控制对话框高度) */
    protected var ll_control_height: LinearLayout? = null
    /*** is showAnim running(显示动画是否正在执行) */
    private var isShowAnim: Boolean = false
    /*** is DismissAnim running(消失动画是否正在执行) */
    private var isDismissAnim: Boolean = false
    /*** max height(最大高度) */
    protected var maxHeight: Float = 0.toFloat()

    constructor(context: Context?) : this(context, DEFAULT_STYLE)

    constructor(context: Context?, themeResId: Int) : super(context, themeResId) {
        setDialogTheme()
    }

    private val showAnimSet = object : BaseAnimatorSet.AnimatorListener {

        override fun onAnimationStart(animator: Animator) {
            isShowAnim = true
        }

        override fun onAnimationRepeat(animator: Animator) {}

        override fun onAnimationEnd(animator: Animator) {
            isShowAnim = false
        }

        override fun onAnimationCancel(animator: Animator) {
            isShowAnim = false
        }

    }

    private val dismissAnimSet = object : BaseAnimatorSet.AnimatorListener {

        override fun onAnimationStart(animator: Animator) {
            isDismissAnim = true
        }

        override fun onAnimationRepeat(animator: Animator) {}

        override fun onAnimationEnd(animator: Animator) {
            isDismissAnim = false
            superDismiss()
        }

        override fun onAnimationCancel(animator: Animator) {
            isDismissAnim = false
            superDismiss()
        }

    }

    private val defaultListener = View.OnClickListener { dismiss() }

    /**
     * set dialog theme(设置对话框主题)
     */
    private fun setDialogTheme() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)// android:windowNoTitle
    }

    /**
     * inflate layout for dialog ui and return (填充对话框所需要的布局并返回)
     * <pre>
     *
     * public View onCreateView() {
     * View inflate = View.inflate(context, R.layout.dialog_share, null);
     * return inflate;
     * }
    </pre> *
     */
    abstract fun onCreateView(): View

    /**
     * set Ui data or logic opreation before attatched window(在对话框显示之前,设置界面数据或者逻辑)
     */
    abstract fun setUiBeforShow()

    override fun onCreate(savedInstanceState: Bundle?) {
        dm = context?.resources?.displayMetrics
        ll_top = LinearLayout(context)
        ll_top?.gravity = Gravity.CENTER
        ll_control_height = LinearLayout(context)
        ll_control_height?.orientation = LinearLayout.VERTICAL
        ll_control_height?.addView(onCreateView())
        ll_top?.addView(ll_control_height)
        val heightPixels = dm?.heightPixels ?: 0
        val widthPixels = dm?.widthPixels ?: 0
        maxHeight = (heightPixels - Utils.getHeight(context) - dp2px(40f)).toFloat()
        setContentView(ll_top, ViewGroup.LayoutParams(widthPixels, maxHeight.toInt()))
        ll_top?.setOnClickListener(defaultListener)
    }

    /**
     * when dailog attached to window,set dialog width and height and show anim
     * (当dailog依附在window上,设置对话框宽高以及显示动画)
     */
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setUiBeforShow()
        val width = if (widthScale == 0f) {
            ViewGroup.LayoutParams.WRAP_CONTENT
        } else {
            val widthPixels = dm?.widthPixels ?: 0
            (widthPixels * widthScale).toInt()
        }
        val height = if (heightScale == 0f) {
            ViewGroup.LayoutParams.WRAP_CONTENT
        } else if (heightScale == 1f) {
            ViewGroup.LayoutParams.MATCH_PARENT
        } else {
            (maxHeight * heightScale).toInt()
        }
        ll_control_height?.layoutParams = LinearLayout.LayoutParams(width, height)
        if (showAnim != null) {
            showAnim?.listener(showAnimSet)?.playOn(ll_control_height)
        } else {
            BaseAnimatorSet.reset(ll_control_height)
        }
    }

    override fun setCanceledOnTouchOutside(cancel: Boolean) {
        this.cancel = cancel
        super.setCanceledOnTouchOutside(cancel)
    }

    override fun dismiss() {
        if (context != null && context is Activity) {
            if ((context as Activity).isFinishing) return
        }
        if (dismissAnim != null && ll_control_height != null) {
            dismissAnim?.listener(dismissAnimSet)?.playOn(ll_control_height)
        } else {
            superDismiss()
        }
    }

    /**
     * dismiss without anim(无动画dismiss)
     */
    fun superDismiss() {
        super.dismiss()
    }

    /**
     * dialog anim by styles(动画弹出对话框,style动画资源)
     */
    fun show(animStyle: Int) {
        val window = window
        window?.setWindowAnimations(animStyle)
        show()
    }

    /**
     * set window dim or not(设置背景是否昏暗)
     */
    fun dimEnabled(isDimEnabled: Boolean): T {
        val window = window
        if (isDimEnabled && window != null) {
            window.addFlags(LayoutParams.FLAG_DIM_BEHIND)
        } else window?.clearFlags(LayoutParams.FLAG_DIM_BEHIND)
        return this as T
    }

    /**
     * set dialog width scale:0-1(设置对话框宽度,占屏幕宽的比例0-1)
     */
    fun widthScale(widthScale: Float): T {
        this.widthScale = widthScale
        return this as T
    }

    /**
     * set dialog height scale:0-1(设置对话框高度,占屏幕宽的比例0-1)
     */
    fun heightScale(heightScale: Float): T {
        this.heightScale = heightScale
        return this as T
    }

    /**
     * set show anim(设置显示的动画)
     */
    fun showAnim(showAnim: BaseAnimatorSet): T {
        this.showAnim = showAnim
        return this as T
    }

    /**
     * set dismiss anim(设置隐藏的动画)
     */
    fun dismissAnim(dismissAnim: BaseAnimatorSet): T {
        this.dismissAnim = dismissAnim
        return this as T
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        return if (isDismissAnim || isShowAnim) {
            true
        } else super.dispatchTouchEvent(ev)
    }

    override fun show() {
        if (context != null && context is Activity) {
            if ((context as Activity).isFinishing) return
        }
        super.show()
    }

    override fun onBackPressed() {
        if (isDismissAnim || isShowAnim) {
            return
        }
        super.onBackPressed()
    }

    /**
     * dp to px
     */
    fun dp2px(dp: Float): Int {
        val scale = context!!.resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }

}
