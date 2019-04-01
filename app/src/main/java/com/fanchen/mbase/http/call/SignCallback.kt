package com.fanchen.mbase.http.call

import com.fanchen.mbase.http.HttpQueue

/**
 * 单一 http 请求回调 ，显示等待进度
 * Created by fanchen on 2018/11/10.
 */
interface SignCallback : Callback {
    /**
     * 開始請求
     * 运行在主线程
     */
    fun onHttpStart(queue: HttpQueue)

    /**
     * 在發生錯誤，或者整個請求完全完成，后調用
     * 运行在主线程
     */
    fun onHttpFinish(queue: HttpQueue)

}