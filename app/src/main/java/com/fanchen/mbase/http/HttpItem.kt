package com.fanchen.mbase.http

import android.text.TextUtils

/**
 * HTTP请求Item
 */
class HttpItem {

    var method = "" //HTTP 方法名
    var serviceName: String = "" //api service class
    var error: Throwable = Throwable("链接服务器失败") // 错误信息
    var parameters: Any? = null //请求参数
    var response: Any? = null //返回

    constructor(method: String = "", parameters: Any? = null, serviceName: String = "") {
        if (!TextUtils.isEmpty(method)) this.method = method
        if (!TextUtils.isEmpty(serviceName)) this.serviceName = serviceName
        if (parameters != null) this.parameters = parameters
    }

}