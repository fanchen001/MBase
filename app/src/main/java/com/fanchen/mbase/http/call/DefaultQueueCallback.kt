package com.fanchen.mbase.http.call

import android.app.Activity
import android.support.v4.app.Fragment
import com.fanchen.mbase.http.HttpItem
import com.fanchen.mbase.http.HttpQueue

/**
 * QueueCallback 默认实现
 * Created by fanchen on 2018/11/10.
 */
open class DefaultQueueCallback(fragment: Fragment? = null, activity: Activity? = null) : DefaultSignCallback(fragment,activity), QueueCallback {

    constructor(fragment: Fragment?) : this(fragment, null)

    constructor(activity: Activity?) : this(null, activity)

    override fun onNextRequest(queue: HttpQueue, current: HttpItem, next: HttpItem): Boolean {
        return true
    }

}