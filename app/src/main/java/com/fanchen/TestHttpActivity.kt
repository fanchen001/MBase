package com.fanchen

import android.app.Activity
import android.content.Intent
import android.view.View
import com.fanchen.api.TestApi
import com.fanchen.mbase.R
import com.fanchen.mbase.http.HttpItem
import com.fanchen.mbase.http.HttpQueue
import com.fanchen.mbase.http.call.DefaultQueueCallback
import com.fanchen.mbase.http.call.DefaultSignCallback
import com.fanchen.mbase.ui.BaseActivity
import com.fanchen.mbase.util.BoxingUtil
import com.fanchen.mbase.warp.showToast
import kotlinx.android.synthetic.main.activity_http.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody

class TestHttpActivity : BaseActivity() {

    override fun getLayout(): Int {
        return R.layout.activity_http
    }

    fun onClick(v: View) {
        when (v) {
            button1 -> {
                val map = mapOf("type" to "yuantong", "postid" to "11111111111")
                val httpItem = HttpItem("queryGet", map, TestApi::class.java.name)
                mHttpUtil.execute(httpItem,object : DefaultSignCallback(this){

                    override fun onHttpSuccess(queue: HttpQueue, responses: ArrayList<HttpItem>) {
                        showToast(responses[0].response?.toString() ?: "---")
                    }

                })

            }
            button2 -> {
                val map = mapOf("type" to "yuantong", "postid" to "11111111111")
                val httpItem = HttpItem("queryPost", map, TestApi::class.java.name)
                mHttpUtil.execute(httpItem,object : DefaultSignCallback(this){

                    override fun onHttpSuccess(queue: HttpQueue, responses: ArrayList<HttpItem>) {
                        showToast(responses[0].response?.toString() ?: "---")
                    }

                })
            }
            button3 -> {
                val map = mapOf("type" to "yuantong", "postid" to "11111111111")
                val arrayList = ArrayList<HttpItem>()
                arrayList.add(HttpItem("queryGet", map, TestApi::class.java.name))
                arrayList.add(HttpItem("queryPost", map, TestApi::class.java.name))

                mHttpUtil.execute(arrayList,object : DefaultQueueCallback(this){

                    override fun onNextRequest(queue: HttpQueue, current: HttpItem, next: HttpItem): Boolean {
                        return true
                    }

                    override fun onHttpSuccess(queue: HttpQueue, responses: ArrayList<HttpItem>) {

                        showToast(responses[0].response?.toString() ?: "---")
                        showToast(responses[1].response?.toString() ?: "---")
                    }

                })
            }
            button4 -> {
                BoxingUtil.singleStart(this)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == Activity.RESULT_FIRST_USER && data != null){
            val file = BoxingUtil.getFile(this, data) ?: return
            val token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE1NTk3MjI0ODI4NjMsInBheWxvYWQiOiJ7XCJ1c2VySWRcIjpcIjEzOTc1ODcwNzYxXCIsXCJ1c2VyVW5pY29kZVwiOm51bGwsXCJ1c2VyTmFtZVwiOm51bGwsXCJ1c2VyUGhvbmVcIjpudWxsLFwidXNlckVtYWlsXCI6bnVsbCxcInBhcmVudElkXCI6bnVsbCxcInJhbmtJZFwiOm51bGwsXCJ1c2VyUHdkXCI6bnVsbCxcImNyZWF0ZVRpbWVcIjpudWxsLFwibG9naW5UaW1lXCI6bnVsbCxcImxvZ2luVGltZXNcIjpudWxsLFwiaXNEZWxldGVcIjpudWxsLFwidmVyc2lvblwiOjQsXCJhY2NvdW50U3RhdHVzXCI6bnVsbCxcInRoaXJkUGFydHlcIjpudWxsLFwidHBPcGVuaWRcIjpudWxsLFwidHBNYWluYWNjb3VudFwiOm51bGwsXCJ0cFVuaW9uaWRcIjpudWxsfSJ9.UWKza0HPxReSVFmWuFqoAeemc4j0laRdjYlFiLV4bHY"
            val httpItem = HttpItem("uploadimg",serviceName = TestApi::class.java.name)
            val fileRQ = RequestBody.create(MediaType.parse("image/jpeg"), file)
            val parse = MediaType.parse("multipart/form-data")
            httpItem.parameters = MultipartBody.Builder().setType(parse!!).addFormDataPart("token", token)
                    .addFormDataPart("ptcTag", "memberPhoto.mobile").addFormDataPart("uploadUserBgPic", file.name, fileRQ).build()

            mHttpUtil.execute(httpItem,object : DefaultSignCallback(this){

                override fun onHttpSuccess(queue: HttpQueue, responses: ArrayList<HttpItem>) {
                    showToast(responses[0].response?.toString() ?: "---")
                }

            })
        }
    }

}