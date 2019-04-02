package com.fanchen.mbase.http.call

import android.app.Activity
import android.support.v4.app.Fragment
import com.fanchen.mbase.http.HttpQueue
//import com.fanchen.mbase.util.HUDUtil

/**
 * SignCallback 默认实现
 * Created by fanchen on 2018/11/10.
 */
open class DefaultSignCallback(fragment: Fragment? = null, activity: Activity? = null) : DefaultCallback(fragment, activity), SignCallback {

    constructor(fragment: Fragment?) : this(fragment, null)

    constructor(activity: Activity?) : this(null, activity)

    override fun onHttpStart(queue: HttpQueue) {
        val activity = getActivity() ?: return
//        HUDUtil.showHUD(activity,"请稍后...")
    }

    override fun onHttpFinish(queue: HttpQueue) {
//        HUDUtil.closeHUD()
    }

}