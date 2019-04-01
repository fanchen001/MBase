package com.fanchen.mbase.http

import android.os.Handler
import com.fanchen.mbase.http.call.Callback
import com.fanchen.mbase.http.call.QueueCallback
import com.fanchen.mbase.http.call.SignCallback
import okhttp3.RequestBody
import retrofit2.Call
import java.lang.ref.SoftReference
import java.lang.reflect.Type
import java.net.SocketException
import java.net.SocketTimeoutException
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Http请求队列
 */
class HttpQueue(var util: OkHttpUtil) {

    val queue: CopyOnWriteArrayList<HttpItem> = CopyOnWriteArrayList()

    private fun object2Map(obj: Any?): Map<String, String> {
        val reMap = HashMap<String, String>()
        val aClass = obj?.javaClass ?: return reMap
        val fields = aClass.declaredFields
        for (i in fields.indices) {
            val subField = aClass.getDeclaredField(fields[i].name)
            subField.isAccessible = true
            val o = subField.get(obj)
            reMap[fields[i].name] = o.toString()
        }
        return reMap
    }

    private fun isFieldMap(types: Array<Type>): Boolean {
        return types.size == 1 && types[0].toString().contains("java.util.Map")
    }

    private fun isFieldArray(types: Array<Type>, any: Any?): Boolean {
        return types.size == (any as Array<*>).size
    }

    @Synchronized
    private fun execute(item: HttpItem): Any? {
        try {
            val service = util.getService(item.serviceName)
            val declaredMethods = service.javaClass.declaredMethods
            val filter = declaredMethods.filter { it.name == item.method }
            if (filter.isNotEmpty()) {
                filter[0].isAccessible = true
                val genericParameterTypes = filter[0].genericParameterTypes
                if (item.parameters == null) {
                    val invoke = filter[0].invoke(service) as? Call<*>
                    item.response = invoke?.execute()?.body()
                } else if (item.parameters is RequestBody) {
                    val invoke = filter[0].invoke(service, item.parameters) as? Call<*>
                    item.response = invoke?.execute()?.body()
                } else if (item.parameters is String) {
                    val invoke = filter[0].invoke(service, item.parameters.toString()) as? Call<*>
                    item.response = invoke?.execute()?.body()
                } else if ((item.parameters is Map<*, *>) && isFieldMap(genericParameterTypes)) {
                    val invoke = filter[0].invoke(service, item.parameters) as? Call<*>
                    item.response = invoke?.execute()?.body()
                } else if ((item.parameters is Array<*>) && isFieldArray(genericParameterTypes, item.parameters)) {
                    val invoke = filter[0].invoke(service, *(item.parameters as Array<*>)) as? Call<*>
                    item.response = invoke?.execute()?.body()
                } else if (isFieldMap(genericParameterTypes)) {
                    val obj = object2Map(item.parameters)
                    val invoke = filter[0].invoke(service, obj) as? Call<*>
                    item.response = invoke?.execute()?.body()
                } else {
                    throw Throwable("Throwable:API接口不存在~")
                }
                return item.response
            } else {
                throw Throwable("Throwable:API接口不存在~")
            }
        } catch (e: Throwable) {
            e.printStackTrace()
            when (e) {
                is SocketTimeoutException -> item.error = Throwable("Throwable:连接服务器超时~")
                is SocketException -> item.error = Throwable("Throwable:网络飞走啦~")
                else -> item.error = e
            }
        }
        return null
    }

    /**
     *
     */
    @Synchronized
    fun addHttpItem(item: HttpItem) {
        if (!queue.contains(item)) queue.add(item)
    }

    @Synchronized
    fun removeHttpItem(item: HttpItem) {
        if (queue.contains(item)) queue.remove(item)
    }

    @Synchronized
    fun clear() {
        if (queue.isNotEmpty()) queue.clear()
    }

    @Synchronized
    fun setParameters(method: String, parameters: Any) {
        queue.forEach {
            if (method == it.method) {
                it.parameters = parameters
                return@forEach
            }
        }
    }

    @Synchronized
    fun getParameters(method: String) : Any? {
        queue.forEach {
            if (method == it.method) {
                return it.parameters
            }
        }
        return null
    }

    fun execute(newQueue: ArrayList<HttpItem>? = null): List<HttpItem>? {
        if (newQueue != null && newQueue != queue) queue.addAll(newQueue)
        queue.forEachIndexed { index, httpItem ->
            val execute = execute(httpItem)
            if (execute == null) {
                return null
            } else if (index < 0 || index >= queue.size - 1) {
                return queue
            }
        }
        return null
    }

    @Synchronized
    fun execute(handler: Handler, newQueue: List<HttpItem>? = null, callback: Callback) {
        val signCallbackSoft = if (callback is SignCallback) SoftReference(callback) else null
        val queueCallbackSoft = if (callback is QueueCallback) SoftReference(callback) else null
        execute(handler, newQueue, SoftReference(callback), signCallbackSoft, queueCallbackSoft)
    }

    @Synchronized
    fun execute(handler: Handler, newQueue: List<HttpItem>? = null, callbackSoft: SoftReference<Callback>? = null,
                signCallbackSoft: SoftReference<SignCallback>? = null, queueCallbackSoft: SoftReference<QueueCallback>? = null) {
        if (newQueue != null && newQueue != queue) queue.addAll(newQueue)
        util.executor.execute {
            try {
                if (callbackSoft?.get()?.isActive(this) == true && signCallbackSoft != null) {
                    handler.post { signCallbackSoft.get()?.onHttpStart(this) }
                }
                run forHttp@{
                    queue.forEachIndexed { index, httpItem ->
                        val execute = execute(httpItem)
                        if (execute == null) {
                            if (callbackSoft?.get()?.isActive(this) == true) {
                                handler.post { callbackSoft.get()?.onHttpError(this, httpItem.error) }
                            }
                            return@forHttp
                        }else if (index >= 0 && index < queue.size - 1) {
                            if (callbackSoft?.get()?.isActive(this) == true && queueCallbackSoft != null) {
                                if (queueCallbackSoft.get()?.onNextRequest(this, httpItem, queue[index + 1]) != true) {
                                    handler.post { queueCallbackSoft.get()?.onHttpError(this, httpItem.error) }
                                    return@forHttp
                                }
                            } else {
                                return@forHttp
                            }
                        } else {
                            if (callbackSoft?.get()?.isActive(this) == true) {
                                handler.post { callbackSoft.get()?.onHttpSuccess(this, ArrayList(queue)) }
                            } else {
                                return@forHttp
                            }
                        }
                    }
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            } finally {
                if (callbackSoft?.get()?.isActive(this) == true && signCallbackSoft != null) {
                    handler.post { signCallbackSoft.get()?.onHttpFinish(this) }
                }
            }
        }
    }

}