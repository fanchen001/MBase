package com.fanchen.mbase.http.call

import com.fanchen.mbase.http.HttpItem
import com.fanchen.mbase.http.HttpQueue

/**
 * http 请求队列
 * Created by fanchen on 2018/11/10.
 */
interface QueueCallback : SignCallback {

    /**
     * 下一個請求
     * 运行在子线程
     */
    fun onNextRequest(queue: HttpQueue, current: HttpItem, next: HttpItem): Boolean

}