package com.fanchen.mbase.http.call

import android.app.Activity
import android.support.v4.app.Fragment
import com.fanchen.mbase.http.HttpItem
import com.fanchen.mbase.http.HttpQueue
import com.fanchen.mbase.warp.showToast

/**
 * Callback 默认实现
 * Created by fanchen on 2018/11/10.
 */
open class DefaultCallback(private val fragment: Fragment? = null, private val activity: Activity? = null) : Callback {

    constructor(fragment: Fragment) : this(fragment, null)

    constructor(activity: Activity) : this(null, activity)

    override fun onHttpError(queue: HttpQueue, e: Throwable) {
        val activity = getActivity() ?: return
        val split = e.toString().split(":")
        activity.showToast(split[split.size - 1])
    }

    override fun onHttpSuccess(queue: HttpQueue, responses: ArrayList<HttpItem>) {
    }

    override fun isActive(queue: HttpQueue): Boolean {
        if (fragment != null) {
            return fragment.isAdded && !fragment.isDetached
        } else if (activity != null) {
            return !activity.isFinishing
        }
        return true
    }

    fun getActivity(): Activity? {
        return activity ?: fragment?.activity
    }

}