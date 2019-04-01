package com.fanchen.mbase.dialog

import android.view.View
import android.view.animation.Interpolator

import com.nineoldandroids.animation.Animator
import com.nineoldandroids.animation.AnimatorSet
import com.nineoldandroids.view.ViewHelper

/**
 * AnimatorSet
 * 动画
 */
abstract class BaseAnimatorSet {
    /**
     * 动画时长,系统默认250
     */
    protected var duration: Long = 500
    protected var animatorSet = AnimatorSet()
    private var interpolator: Interpolator? = null
    private var delay: Long = 0
    private var listener: AnimatorListener? = null

    private val defaultListener = object : Animator.AnimatorListener {

        override fun onAnimationStart(animator: Animator) {
            if (listener != null) listener!!.onAnimationStart(animator)
        }

        override fun onAnimationRepeat(animator: Animator) {
            if (listener != null) listener!!.onAnimationRepeat(animator)
        }

        override fun onAnimationEnd(animator: Animator) {
            if (listener != null) listener!!.onAnimationEnd(animator)
        }

        override fun onAnimationCancel(animator: Animator) {
            if (listener != null) listener!!.onAnimationCancel(animator)
        }
    }

    abstract fun setAnimation(view: View?)

    protected fun start(view: View?) {
        view ?: return
        /** 设置动画中心点:pivotX--->X轴方向动画中心点,pivotY--->Y轴方向动画中心点  */
        reset(view)
        setAnimation(view)
        animatorSet.duration = duration
        if (interpolator != null) {
            animatorSet.setInterpolator(interpolator)
        }
        if (delay > 0) {
            animatorSet.startDelay = delay
        }
        animatorSet.addListener(defaultListener)
        animatorSet.start()
    }

    /**
     * 设置动画时长
     */
    fun duration(duration: Long): BaseAnimatorSet {
        this.duration = duration
        return this
    }

    /**
     * 设置动画时长
     */
    fun delay(delay: Long): BaseAnimatorSet {
        this.delay = delay
        return this
    }

    /**
     * 设置动画插补器
     */
    fun interpolator(interpolator: Interpolator): BaseAnimatorSet {
        this.interpolator = interpolator
        return this
    }

    /**
     * 动画监听
     */
    fun listener(listener: AnimatorListener): BaseAnimatorSet {
        this.listener = listener
        return this
    }

    /**
     * 在View上执行动画
     */
    fun playOn(view: View?) {
        start(view)
    }

    interface AnimatorListener {
        fun onAnimationStart(animator: Animator)

        fun onAnimationRepeat(animator: Animator)

        fun onAnimationEnd(animator: Animator)

        fun onAnimationCancel(animator: Animator)
    }

    companion object {

        fun reset(view: View?) {
            view ?: return
            ViewHelper.setAlpha(view, 1f)
            ViewHelper.setScaleX(view, 1f)
            ViewHelper.setScaleY(view, 1f)
            ViewHelper.setTranslationX(view, 0f)
            ViewHelper.setTranslationY(view, 0f)
            ViewHelper.setRotation(view, 0f)
            ViewHelper.setRotationY(view, 0f)
            ViewHelper.setRotationX(view, 0f)
        }
    }
}
