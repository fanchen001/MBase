package com.fanchen.api

import com.fanchen.bean.ImageResponse
import com.fanchen.bean.Kuaidi
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface TestApi {

    @GET("query")
    fun queryGet(@QueryMap map:Map<String,String>): Call<Kuaidi>

    @POST("query")
    @FormUrlEncoded
    fun queryPost(@FieldMap map:Map<String,String>): Call<Kuaidi>

    /**
     * 图片上传接口
     * UploadImage
     */
    @POST("https://member.cxc555.com/cmsWeb/file/user/uploadimg")
    fun uploadimg(@Body body: RequestBody): Call<ImageResponse>
}