package com.fanchen.mbase.http

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import com.fanchen.mbase.http.call.Callback
import com.fanchen.mbase.http.call.SignCallback
import com.fanchen.mbase.http.converter.GsonConverterFactory
import com.fanchen.mbase.http.interceptor.CxcInterceptor
import com.fanchen.mbase.http.interceptor.HttpLogInterceptor.Logger.FileLogger
import com.fanchen.mbase.util.DateUtil
import com.fanchen.mbase.util.NetworkUtil
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.HashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.io.File

class OkHttpUtil {
    @Transient
    val executor: ExecutorService = Executors.newFixedThreadPool(3)
    @Transient
    private val map: HashMap<Class<*>, Retrofit> = HashMap()
    @Transient
    private val serviceMap: HashMap<Class<*>, Any> = HashMap()
    @Transient
    private val isProxy = true
    @Transient
    private var appContext: Context? = null
    @Transient
    private val handler: Handler = Handler(Looper.getMainLooper())

    private constructor(context: Context?) {
        appContext = context?.applicationContext
    }

    private fun isFiddler(): Boolean {
        val proHost = android.net.Proxy.getDefaultHost()
        val proPort = android.net.Proxy.getDefaultPort()
        //简单的防Fiddler抓包检测
        return (!isProxy && !TextUtils.isEmpty(proHost) && (proHost.startsWith("192")
                || proHost.startsWith("127")) && proPort != -1)
    }

    private fun checkExecute(httpQueue: HttpQueue, callback: Callback): Boolean {
        return if (NetworkUtil.isNetWorkAvailable(appContext)) {
            if (isFiddler()) {
                callback.onHttpError(httpQueue, Throwable("Throwable:请勿使用抓包工具~"))
                (callback as? SignCallback)?.onHttpFinish(httpQueue)
                false
            } else {
                true
            }
        } else {
            callback.onHttpError(httpQueue, Throwable("Throwable:当前网络不可用~"))
            (callback as? SignCallback)?.onHttpFinish(httpQueue)
            false
        }
    }

    fun initHttpUtil(clazz: Class<*>, url: String,isLog:Boolean = false) {
        val builder = OkHttpClient.Builder().connectTimeout(60, TimeUnit.SECONDS).readTimeout(60, TimeUnit.SECONDS).writeTimeout(60, TimeUnit.SECONDS)
        if(isLog){
            val fileName = String.format("%s.log", DateUtil.getCurrentDate("yyyy-MM-dd"))
            val logFile = File(appContext?.externalCacheDir, fileName)
            val interceptor = CxcInterceptor(url, FileLogger(logFile))
            builder.addInterceptor(interceptor)
        }
        val create = GsonConverterFactory.create()
        map[clazz] = Retrofit.Builder().baseUrl(url).addConverterFactory(create).client(builder.build()).build()
    }

    /**
     * @param context
     * @return
     */
    companion object {
        @SuppressLint("StaticFieldLeak")
        private var okHttpUtil: OkHttpUtil? = null

        fun with(context: Context?): OkHttpUtil {
            if (okHttpUtil == null) {
                synchronized(OkHttpUtil::class.java) {
                    if (okHttpUtil == null) {
                        okHttpUtil = OkHttpUtil(context)
                    }
                }
            }
            return okHttpUtil!!
        }
    }

    fun getService(service: String): Any {
        val forName = Class.forName(service)
        var any = serviceMap[forName]
        if (any == null) {
            any = map[forName]?.create(forName)
        }
        return any ?: Any()
    }

    fun execute(https: ArrayList<HttpItem>, callback: Callback) {
        val httpQueue = HttpQueue(this)
        if (checkExecute(httpQueue, callback)) {
            httpQueue.execute(handler, https, callback)
        }
    }

    fun execute(http: HttpItem, callback: Callback) {
        val httpQueue = HttpQueue(this)
        if (checkExecute(httpQueue, callback)) {
            httpQueue.addHttpItem(http)
            httpQueue.execute(handler, callback = callback)
        }
    }

    fun execute(http: HttpItem): HttpItem? {
        val httpQueue = HttpQueue(this)
        httpQueue.addHttpItem(http)
        val execute = httpQueue.execute()
        return if (execute?.isNotEmpty() == true) execute[0] else null
    }

    fun execute(https: ArrayList<HttpItem>): List<HttpItem>? {
        val httpQueue = HttpQueue(this)
        return httpQueue.execute(https)
    }
}