package com.fanchen.mbase.dialog

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.FrameLayout

import com.nineoldandroids.animation.ObjectAnimator

import android.view.ViewGroup.LayoutParams.MATCH_PARENT

/**
 * BottomBaseDialog
 *
 * @param <T>
</T> */
abstract class BottomBaseDialog<T : BottomBaseDialog<T>>(context: Context?, override val animateView: View? = null, style: Int) : BottomTopBaseDialog<T>(context, style) {

    private val showAnim = TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f)
    private val dismissAnim = TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f)

    protected override val showAnimation: Animation?
        get() = showAnim

    protected override val dismissAnimation: Animation?
        get() = dismissAnim

    protected override val windowInAs: BaseAnimatorSet? = object : BaseAnimatorSet() {

        override fun setAnimation(view: View?) {
            val v = view ?: return
            val oa1 = ObjectAnimator.ofFloat(v, "scaleX", 1f, 0.9f)
            val oa2 = ObjectAnimator.ofFloat(v, "scaleY", 1f, 0.9f)
            animatorSet.playTogether(oa1, oa2)
        }

    }

    protected override val windowOutAs: BaseAnimatorSet? = object : BaseAnimatorSet() {

        override fun setAnimation(view: View?) {
            val v = view ?: return
            val oa1 = ObjectAnimator.ofFloat(v, "scaleX", 0.9f, 1f)
            val oa2 = ObjectAnimator.ofFloat(v, "scaleY", 0.9f, 1f)
            animatorSet.playTogether(oa1, oa2)
        }

    }

    override fun onStart() {
        super.onStart()
        ll_top?.layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        ll_top?.gravity = Gravity.BOTTOM
        window?.setGravity(Gravity.BOTTOM)
        ll_top?.setPadding(left, top, right, bottom)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        showWithAnim()
    }

    override fun dismiss() {
        dismissWithAnim()
    }

}
