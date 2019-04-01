package com.fanchen.mbase.dialog

import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener

/**
 * BottomTopBaseDialog
 *
 * @param <T>
</T> */
abstract class BottomTopBaseDialog<T : BottomTopBaseDialog<T>>(context: Context?, style: Int) : BaseDialog<T>(context, style) {

    private var innerAnimDuration: Long = 350
    private var isInnerShowAnim: Boolean = false
    private var isInnerDismissAnim: Boolean = false

    protected var left: Int = 0
    protected var top: Int = 0
    protected var right: Int = 0
    protected var bottom: Int = 0

    /**
     * 进入动画
     *
     * @return
     */
    protected abstract val windowInAs: BaseAnimatorSet?

    /**
     * 退出动画
     *
     * @return
     */
    protected abstract val windowOutAs: BaseAnimatorSet?

    /**
     * animateView 联动效果
     * 显示的时候
     *
     * @return
     */
    protected abstract val showAnimation: Animation?

    /**
     * animateView 联动效果
     * 关闭的时候
     *
     * @return
     */
    protected abstract val dismissAnimation: Animation?

    /**
     * animateView
     *
     * @return
     */
    protected abstract val animateView: View?

    private val showListener = object : AnimationListener {

        override fun onAnimationStart(animation: Animation) {
            isInnerShowAnim = true
        }

        override fun onAnimationRepeat(animation: Animation) {

        }

        override fun onAnimationEnd(animation: Animation) {
            isInnerShowAnim = false
        }

    }

    private val dismissListener = object : AnimationListener {

        override fun onAnimationStart(animation: Animation) {
            isInnerDismissAnim = true
        }

        override fun onAnimationRepeat(animation: Animation) {

        }

        override fun onAnimationEnd(animation: Animation) {
            isInnerDismissAnim = false
            superDismiss()
        }

    }

    /**
     * set duration for inner animation of animateView(设置animateView内置动画时长)
     */
    fun innerAnimDuration(innerAnimDuration: Long): T {
        this.innerAnimDuration = innerAnimDuration
        return this as T
    }

    fun padding(left: Int, top: Int, right: Int, bottom: Int): T {
        this.left = left
        this.top = top
        this.right = right
        this.bottom = bottom
        return this as T
    }

    /**
     * show dialog and animateView with inner show animation(设置dialog和animateView显示动画)
     */
    protected fun showWithAnim() {
        val animateView = animateView
        val animation = showAnimation
        if (animation != null) {
            animation.duration = innerAnimDuration
            animation.setAnimationListener(showListener)
            ll_control_height?.startAnimation(animation)
        }
        if (animateView != null && windowInAs != null) {
            windowInAs?.duration(innerAnimDuration)?.playOn(animateView)
        }
    }

    /**
     * dimiss dialog and animateView with inner dismiss animation(设置dialog和animateView消失动画)
     */
    protected fun dismissWithAnim() {
        val animation = dismissAnimation
        val animateView = animateView
        if (animation != null) {
            animation.duration = innerAnimDuration
            animation.setAnimationListener(dismissListener)
            ll_control_height?.startAnimation(animation)
        } else {
            superDismiss()
        }
        if (animateView != null && windowOutAs != null) {
            windowOutAs?.duration(innerAnimDuration)?.playOn(animateView)
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        return if (isInnerDismissAnim || isInnerShowAnim) {
            true
        } else super.dispatchTouchEvent(ev)
    }

    override fun onBackPressed() {
        if (isInnerDismissAnim || isInnerShowAnim) {
            return
        }
        super.onBackPressed()
    }
}
