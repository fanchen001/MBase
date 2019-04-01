package com.fanchen.mbase.http.call

import com.fanchen.mbase.http.HttpItem
import com.fanchen.mbase.http.HttpQueue

/**
 * 单一 http 请求回调,不需要显示等待进度
 * Created by fanchen on 2018/11/10.
 */
interface Callback {
    /**
     * 發生錯誤
     * 运行在主线程
     */
    fun onHttpError(queue: HttpQueue, e: Throwable)

    /**
     * 整個請求完成
     * 运行在主线程
     */
    fun onHttpSuccess(queue: HttpQueue, responses: ArrayList<HttpItem>)

    /**
     * 判断Callback回调的View是否活动
     */
    fun isActive(queue: HttpQueue):Boolean
}