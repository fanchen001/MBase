package com.fanchen.mbase.warp

import android.content.Context
import android.graphics.*
import android.text.TextUtils
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.fanchen.mbase.util.ImageUtil

/**
 * ImageView扩展
 * Created by Administrator on 2018/9/4.
 */
/**
 * 加载网络图片
 */
fun ImageView.setImageUrl(url: String?, preload: Int = 0, errorload: Int = 0) {
    if(TextUtils.isEmpty(url))return
    Glide.with(context.applicationContext).load(url).asBitmap().placeholder(preload).error(errorload).into(this)
}

/**
 * 加載base64圖片
 */
fun ImageView.setImageBase64(base64: String?) {
    if(TextUtils.isEmpty(base64))return
    val bitmap = ImageUtil.base64_Bitmap(base64)
    setImageBitmap(bitmap)
}

/**
 * 加载网络图片，圆角
 */
fun ImageView.setRoundCornerUrl(url: String?, preload: Int = 0, errorload: Int = 0) {
    if(TextUtils.isEmpty(url))return
    Glide.with(context.applicationContext).load(url).transform(GlideRoundTransform(context)).into(this)
}

/**
 * 加载网络图片，圆形
 */
fun ImageView.setRoundUrl(url: String?, preload: Int = 0, errorload: Int = 0) {
    if(TextUtils.isEmpty(url))return
    Glide.with(context.applicationContext).load(url).transform(GlideCircleTransform(context)).into(this)
}

class GlideCircleTransform(var context: Context) : BitmapTransformation(context) {

    override fun getId(): String {
        return this.javaClass.name
    }

    override fun transform(pool: BitmapPool?, toTransform: Bitmap?, outWidth: Int, outHeight: Int): Bitmap {
        return ImageUtil.getRoundBitmap(toTransform,recycle = true) ?: Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_4444)
    }

}

class GlideRoundTransform(var context: Context) : BitmapTransformation(context) {

    override fun getId(): String {
        return this.javaClass.name
    }

    override fun transform(pool: BitmapPool?, toTransform: Bitmap?, outWidth: Int, outHeight: Int): Bitmap {
        return ImageUtil.getRoundCornerBitmap(toTransform, 15f,recycle = true) ?: Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_4444)
    }

}
