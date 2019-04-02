package com.fanchen.mbase.util

import android.content.Context
import android.graphics.*
import android.util.Base64
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import java.io.File
import java.io.InputStream
import java.net.URL
import android.graphics.PorterDuffXfermode
import android.graphics.Shader.TileMode
import android.graphics.LinearGradient
import android.graphics.Bitmap
import android.graphics.RectF
import android.support.annotation.ColorInt
import android.support.annotation.FloatRange
import android.graphics.Shader
import android.graphics.BitmapShader
import android.media.ExifInterface
import android.view.View
import android.provider.MediaStore
import android.provider.DocumentsContract
import android.content.ContentUris
import android.os.Environment.getExternalStorageDirectory
import android.annotation.TargetApi
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.support.annotation.RequiresApi


/**
 * 图片相关
 * Created by fanchen on 2018/9/4.
 */
object ImageUtil {

    /**
     * 图片最大宽度.
     */
    val MAX_WIDTH = 4096 / 2

    /**
     * 图片最大高度.
     */
    val MAX_HEIGHT = 4096 / 2

    fun createBitmap3(v: View, width: Int, height: Int): Bitmap {
        //测量使得view指定大小
        val measuredWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY)
        val measuredHeight = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)
        v.measure(measuredWidth, measuredHeight)
        //调用layout方法布局后，可以得到view的尺寸大小
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight())
        val bmp = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888)
        val c = Canvas(bmp)
        c.drawColor(Color.WHITE)
        v.draw(c)
        return bmp
    }

    /**
     * 将Bitmap转换成Base64字符串
     *
     * @param bit 图片
     * @return base64 编码的图片
     */
    @RequiresApi(Build.VERSION_CODES.FROYO)
    fun bitmap_Base64(bit: Bitmap?): String? {
        val bytes = StreamUtil.bitmap2Byte(bit)
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }

    /**
     * byteArr转bitmap
     *
     * @param bytes 字节数组
     * @return bitmap
     */
    fun bytes2Bitmap(bytes: ByteArray?): Bitmap? {
        return if (bytes == null || bytes.size == 0) null else BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    /**
     * Base64字符串转bitmap
     *
     * @param base64
     * @return
     */
    @RequiresApi(Build.VERSION_CODES.FROYO)
    fun base64_Bitmap(base64: String?): Bitmap? {
        val base64ToByte = EncryptUtil.base64ToByte(base64)
        return bytes2Bitmap(base64ToByte)
    }

    /**
     * byteArr转drawable
     *
     * @param bytes 字节数组
     * @return drawable
     */
    @RequiresApi(Build.VERSION_CODES.DONUT)
    fun bytes2Drawable(context: Context?, bytes: ByteArray?): Drawable? {
        return bitmap2Drawable(context, bytes2Bitmap(bytes))
    }

    /**
     * 获取图片类型
     *
     * @param bytes bitmap的前8字节
     * @return 图片类型
     */
    fun getImageType(bytes: ByteArray): String? {
        if (isJPEG(bytes)) return "JPEG"
        if (isGIF(bytes)) return "GIF"
        if (isPNG(bytes)) return "PNG"
        return if (isBMP(bytes)) "BMP" else null
    }

    /**
     * 转为灰度图片
     *
     * @param src     源图片
     * @param recycle 是否回收
     * @return 灰度图
     */
    fun getGrayBitmap(src: Bitmap, recycle: Boolean = false): Bitmap? {
        if (isEmptyBitmap(src)) return null
        val ret = Bitmap.createBitmap(src.width, src.height, src.config)
        val canvas = Canvas(ret)
        val paint = Paint()
        val colorMatrix = ColorMatrix()
        colorMatrix.setSaturation(0f)
        val colorMatrixColorFilter = ColorMatrixColorFilter(colorMatrix)
        paint.colorFilter = colorMatrixColorFilter
        canvas.drawBitmap(src, 0f, 0f, paint)
        if (recycle && !src.isRecycled) src.recycle()
        return ret
    }

    @RequiresApi(Build.VERSION_CODES.ECLAIR)
            /**
             * 获取图片旋转角度
             *
             * @param filePath 文件路径
             * @return 旋转角度
             */
    fun getRotateDegree(filePath: String): Int {
        try {
            val exifInterface = ExifInterface(filePath)
            val orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            return when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90
                ExifInterface.ORIENTATION_ROTATE_180 -> 180
                ExifInterface.ORIENTATION_ROTATE_270 -> 270
                else -> 90
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0
    }

    /**
     * 图片锐化（拉普拉斯变换）
     *
     * @param bmp
     * @return
     */
    fun sharpenImageAmeliorate(bmp: Bitmap): Bitmap {
        // 拉普拉斯矩阵
        val laplacian = intArrayOf(-1, -1, -1, -1, 9, -1, -1, -1, -1)
        val width = bmp.width
        val height = bmp.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        var pixR = 0
        var pixG = 0
        var pixB = 0
        var pixColor = 0
        var newR = 0
        var newG = 0
        var newB = 0
        var idx = 0
        val alpha = 0.3f
        val pixels = IntArray(width * height)
        bmp.getPixels(pixels, 0, width, 0, 0, width, height)
        var i = 1
        val length = height - 1
        while (i < length) {
            var k = 1
            val len = width - 1
            while (k < len) {
                idx = 0
                for (m in -1..1) {
                    for (n in -1..1) {
                        pixColor = pixels[(i + n) * width + k + m]
                        pixR = Color.red(pixColor)
                        pixG = Color.green(pixColor)
                        pixB = Color.blue(pixColor)
                        newR = newR + (pixR.toFloat() * laplacian[idx].toFloat() * alpha).toInt()
                        newG = newG + (pixG.toFloat() * laplacian[idx].toFloat() * alpha).toInt()
                        newB = newB + (pixB.toFloat() * laplacian[idx].toFloat() * alpha).toInt()
                        idx++
                    }
                }
                newR = Math.min(255, Math.max(0, newR))
                newG = Math.min(255, Math.max(0, newG))
                newB = Math.min(255, Math.max(0, newB))
                pixels[i * width + k] = Color.argb(255, newR, newG, newB)
                newR = 0
                newG = 0
                newB = 0
                k++
            }
            i++
        }
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return bitmap
    }

    /**
     *  判断是否为jpg
     */
    private fun isJPEG(b: ByteArray): Boolean {
        return (b.size >= 2 && b[0] == 0xFF.toByte() && b[1] == 0xD8.toByte())
    }

    /**
     *  判断是否为GIF
     */
    private fun isGIF(b: ByteArray): Boolean {
        return (b.size >= 6 && b[0].toChar() == 'G' && b[1].toChar() == 'I' && b[2].toChar() == 'F' && b[3].toChar() == '8' && (b[4].toChar() == '7' || b[4].toChar() == '9') && b[5].toChar() == 'a')
    }

    /**
     *  判断是否为png
     */
    private fun isPNG(b: ByteArray): Boolean {
        return b.size >= 8 && (b[0] == 137.toByte() && b[1] == 80.toByte() && b[2] == 78.toByte() && b[3] == 71.toByte() && b[4] == 13.toByte() && b[5] == 10.toByte() && b[6] == 26.toByte() && b[7] == 10.toByte())
    }

    /**
     * 判断是否为bitmap
     */
    private fun isBMP(b: ByteArray): Boolean {
        return (b.size >= 2 && b[0].toInt() == 0x42 && b[1].toInt() == 0x4d)
    }

    /**
     * 判断bitmap对象是否为空
     *
     * @param src 源图片
     * @return `true`: 是<br></br>`false`: 否
     */
    fun isEmptyBitmap(src: Bitmap?): Boolean {
        return src == null || src.width == 0 || src.height == 0
    }

    /**
     * bitmap转drawable
     *
     * @param bitmap bitmap对象
     * @return drawable
     */
    @RequiresApi(Build.VERSION_CODES.DONUT)
    fun bitmap2Drawable(context: Context?, bitmap: Bitmap?): Drawable? {
        return if (bitmap == null) null else BitmapDrawable(context?.getResources(), bitmap)
    }

    /**
     * drawable转bitmap
     *
     * @param drawable drawable对象
     * @return bitmap
     */
    fun drawable2Bitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable) {
            if (drawable.bitmap != null) {
                return drawable.bitmap
            }
        }
        val bitmap: Bitmap
        if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, if (drawable.opacity != PixelFormat.OPAQUE) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565)
        } else {
            bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, if (drawable.opacity != PixelFormat.OPAQUE) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565)
        }
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight())
        drawable.draw(canvas)
        return bitmap
    }

    /**
     * 根据文件名判断文件是否为图片
     *
     * @param file 　文件
     * @return `true`: 是<br></br>`false`: 否
     */
    fun isImage(file: File?): Boolean {
        return file != null && isImage(file!!.getPath())
    }

    /**
     * 根据文件名判断文件是否为图片
     *
     * @param filePath 　文件路径
     * @return `true`: 是<br></br>`false`: 否
     */
    fun isImage(filePath: String): Boolean {
        val path = filePath.toUpperCase()
        return (path.endsWith(".PNG") || path.endsWith(".JPG") || path.endsWith(".JPEG") || path.endsWith(".BMP") || path.endsWith(".GIF"))
    }

    /**
     * 获取图片类型
     *
     * @param filePath 文件路径
     * @return 图片类型
     */
    fun getImageType(filePath: String): String? {
        return getImageType(File(filePath))
    }

    /**
     * 获取图片类型
     *
     * @param file 文件
     * @return 图片类型
     */
    fun getImageType(file: File?): String? {
        try {
            return getImageType(StreamUtil.file2Byte(file!!)!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * 图片二值化 黑白
     * @param File
     */
    fun binaryzation(file: String): Bitmap? {
        return binaryzation(bytes2Bitmap(StreamUtil.file2Byte(file)))
    }

    /**
     * 图片二值化 黑白
     * @param File
     */
    fun binaryzation(file: File): Bitmap? {
        return binaryzation(bytes2Bitmap(StreamUtil.file2Byte(file)))
    }

    /**
     * 图片二值化 黑白
     *
     * @param bitmap
     * @return
     */
    fun binaryzation(bitmap: Bitmap?): Bitmap? {
        if (bitmap == null) return null
        //得到图形的宽度和长度
        val width = bitmap.width
        val height = bitmap.height
        //创建二值化图像
        var binarymap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        //依次循环，对图像的像素进行处理
        for (i in 0 until width) {
            for (j in 0 until height) {
                val col = binarymap!!.getPixel(i, j)
                val alpha = col and -0x1000000
                //得到图像的像素RGB
                val red = col and 0x00FF0000 shr 16
                val green = col and 0x0000FF00 shr 8
                val blue = col and 0x000000FF
                // 用公式X = 0.3×R+0.59×G+0.11×B计算出X代替原来的RGB
                var gray = (red.toFloat() * 0.3 + green.toFloat() * 0.59 + blue.toFloat() * 0.11).toInt()
                //对图像进行二值化处理
                if (gray <= 110) {
                    gray = 0
                } else {
                    gray = 255
                }
                // 新的ARGB
                val newColor = alpha or (gray shl 16) or (gray shl 8) or gray
                //设置新图像的当前像素值
                binarymap.setPixel(i, j, newColor)
            }
        }
        return binarymap
    }

    /**
     * 以最省内存的方式读取本地资源的图片
     *
     * @param context
     * @param resId
     * @return
     */
    fun readBitMap(context: Context?, resId: Int): Bitmap {
        val opt = BitmapFactory.Options()
        opt.inPreferredConfig = Bitmap.Config.RGB_565
        opt.inPurgeable = true
        opt.inInputShareable = true
        // 获取资源图片
        return BitmapFactory.decodeStream(context?.resources?.openRawResource(resId), null, opt)
    }

    /**
     * 以最省内存的方式读取图片
     *
     * @return
     */
    fun readBitMap(file: String): Bitmap {
        val opt = BitmapFactory.Options()
        opt.inPreferredConfig = Bitmap.Config.RGB_565
        opt.inPurgeable = true
        opt.inInputShareable = true
        // 获取资源图片
        return BitmapFactory.decodeFile(file, opt)
    }

    fun readBitMap(file: File?): Bitmap? {
        file ?: return null
        return readBitMap(file.absoluteFile)
    }

    /**
     * 描述：获取原图.
     *
     * @param file File对象
     * @return Bitmap 图片
     */
    fun getBitmap(file: File): Bitmap? {
        var resizeBmp: Bitmap? = null
        try {
            resizeBmp = BitmapFactory.decodeFile(file.path)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return resizeBmp
    }

    /**
     * 从互联网上获取指定大小的图片.
     *
     * @param url           要下载文件的网络地址
     * @param desiredWidth  新图片的宽
     * @param desiredHeight 新图片的高
     * @return Bitmap 新图片
     */
    fun getBitmap(url: URL, desiredWidth: Int = 0, desiredHeight: Int = 0): Bitmap? {
        try {
            var bitmap = getBitmap(StreamUtil.url2Byte(url), desiredWidth, desiredHeight)
            // 超出的裁掉
            if (bitmap != null && desiredWidth > 0 && desiredHeight > 0 && (bitmap.width > desiredWidth || bitmap.height > desiredHeight)) {
                bitmap = getCutBitmap(bitmap, desiredWidth, desiredHeight)
            }
            return bitmap
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * 从流中获取指定大小的图片.
     *
     * @param inputStream
     * @param desiredWidth
     * @param desiredHeight
     * @return
     */
    fun getBitmap(inputStream: InputStream?, desiredWidth: Int, desiredHeight: Int): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            val data = StreamUtil.stream2bytes(inputStream)
            bitmap = getBitmap(data, desiredWidth, desiredHeight)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                inputStream?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return bitmap
    }

    /**
     * 从流中获取指定大小的图片.
     *
     * @param data
     * @param desiredWidth
     * @param desiredHeight
     * @return
     */
    fun getBitmap(data: ByteArray?, desiredWidth: Int, desiredHeight: Int): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            val opts = BitmapFactory.Options()
            // 设置为true,decodeFile先不创建内存 只获取一些解码边界信息即图片大小信息
            opts.inJustDecodeBounds = true
            BitmapFactory.decodeByteArray(data, 0, data!!.size, opts)
            // 获取图片的原始宽度高度
            val srcWidth = opts.outWidth
            val srcHeight = opts.outHeight
            val size = resizeToMaxSize(srcWidth, srcHeight, desiredWidth, desiredHeight)
            var width = size[0]
            var height = size[1]
            // 缩放的比例
            val scale = getMinScale(srcWidth, srcHeight, width, height)
            var destWidth = srcWidth
            var destHeight = srcHeight
            if (scale != 0f) {
                destWidth = (srcWidth * scale).toInt()
                destHeight = (srcHeight * scale).toInt()
            }
            // 默认为ARGB_8888.
            opts.inPreferredConfig = Bitmap.Config.RGB_565
            // 以下两个字段需一起使用：
            // 产生的位图将得到像素空间，如果系统gc，那么将被清空。当像素再次被访问，如果Bitmap已经decode，那么将被自动重新解码
            opts.inPurgeable = true
            // 位图可以共享一个参考输入数据(inputstream、阵列等)
            opts.inInputShareable = true
            // 缩放的比例，缩放是很难按准备的比例进行缩放的，通过inSampleSize来进行缩放，其值表明缩放的倍数，SDK中建议其值是2的指数值
            val sampleSize = findBestSampleSize(srcWidth, srcHeight, destWidth, destHeight)
            opts.inSampleSize = sampleSize
            // 设置大小
            opts.outWidth = destWidth
            opts.outHeight = destHeight
            // 创建内存
            opts.inJustDecodeBounds = false
            // 使图片不抖动
            opts.inDither = false
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.size, opts)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return bitmap
    }

    /**
     * 找到最合适的SampleSize
     *
     * @param actualWidth
     * @param actualHeight
     * @param desiredWidth
     * @param desiredHeight
     * @return
     */
    private fun findBestSampleSize(actualWidth: Int, actualHeight: Int, desiredWidth: Int, desiredHeight: Int): Int {
        val wr = actualWidth.toDouble() / desiredWidth
        val hr = actualHeight.toDouble() / desiredHeight
        val ratio = Math.min(wr, hr)
        var n = 1.0f
        while (n * 2 <= ratio) {
            n *= 2f
        }
        return n.toInt()
    }


    /**
     * 倾斜图片
     *
     * @param src     源图片
     * @param kx      倾斜因子x
     * @param ky      倾斜因子y
     * @param px      平移因子x
     * @param py      平移因子y
     * @param recycle 是否回收
     * @return 倾斜后的图片
     */
    fun skewBitmap(src: Bitmap, kx: Float, ky: Float, px: Float = 0f, py: Float = 0f, recycle: Boolean = false): Bitmap? {
        if (isEmptyBitmap(src)) return null
        val matrix = Matrix()
        matrix.setSkew(kx, ky, px, py)
        val ret = Bitmap.createBitmap(src, 0, 0, src.width, src.height, matrix, true)
        if (recycle && !src.isRecycled) src.recycle()
        return ret
    }

    /**
     * 描述：缩放图片.
     *
     * @param file          File对象
     * @param desiredWidth  新图片的宽
     * @param desiredHeight 新图片的高
     * @return Bitmap 新图片
     */
    fun getScaleBitmap(file: File, desiredWidth: Int, desiredHeight: Int): Bitmap? {
        var resizeBmp: Bitmap? = null
        val opts = BitmapFactory.Options()
        // 设置为true,decodeFile先不创建内存 只获取一些解码边界信息即图片大小信息
        opts.inJustDecodeBounds = true
        BitmapFactory.decodeFile(file.path, opts)
        // 获取图片的原始宽度高度
        val srcWidth = opts.outWidth
        val srcHeight = opts.outHeight
        val size = resizeToMaxSize(srcWidth, srcHeight, desiredWidth, desiredHeight)
        var newWidth = size[0]
        var newHeight = size[1]
        // 缩放的比例
        val scale = getMinScale(srcWidth, srcHeight, newWidth, newHeight)
        var destWidth = srcWidth
        var destHeight = srcHeight
        if (scale != 0f) {
            destWidth = (srcWidth * scale).toInt()
            destHeight = (srcHeight * scale).toInt()
        }
        // 默认为ARGB_8888.
        opts.inPreferredConfig = Bitmap.Config.RGB_565
        // 以下两个字段需一起使用：
        // 产生的位图将得到像素空间，如果系统gc，那么将被清空。当像素再次被访问，如果Bitmap已经decode，那么将被自动重新解码
        opts.inPurgeable = true
        // 位图可以共享一个参考输入数据(inputstream、阵列等)
        opts.inInputShareable = true
        // 缩放的比例，缩放是很难按准备的比例进行缩放的，通过inSampleSize来进行缩放，其值表明缩放的倍数，SDK中建议其值是2的指数值
        val sampleSize = findBestSampleSize(srcWidth, srcHeight, destWidth, destHeight)
        opts.inSampleSize = sampleSize
        // 设置大小
        opts.outWidth = destWidth
        opts.outHeight = destHeight
        // 创建内存
        opts.inJustDecodeBounds = false
        // 使图片不抖动
        opts.inDither = false
        resizeBmp = BitmapFactory.decodeFile(file.path, opts)
        // 缩小或者放大
        resizeBmp = scaleBitmap(resizeBmp, scale)
        // 超出的裁掉
        if (resizeBmp!!.width > newHeight || resizeBmp.height > newHeight) {
            resizeBmp = getCutBitmap(resizeBmp, newHeight, newHeight)
        }
        return resizeBmp
    }

    /**
     * 描述：根据等比例缩放图片.
     *
     * @param bitmap the bitmap
     * @param scale  比例
     * @return Bitmap 新图片
     */
    fun scaleBitmap(bitmap: Bitmap?, scale: Float): Bitmap? {
        var resizeBmp: Bitmap? = null
        try {
            if (!checkBitmapNull(bitmap) || scale == 1f) {
                return bitmap
            }
            // 获取Bitmap资源的宽和高
            val bmpW = bitmap?.width ?: 0
            val bmpH = bitmap?.height ?: 0
            // 注意这个Matirx是android.graphics底下的那个
            val matrix = Matrix()
            // 设置缩放系数，分别为原来的0.8和0.8
            matrix.postScale(scale, scale)
            resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bmpW, bmpH, matrix, true)
        } catch (e: Throwable) {
            e.printStackTrace()
        } finally {
            if (resizeBmp != bitmap) {
                bitmap?.recycle()
            }
        }
        return resizeBmp
    }

    /**
     * 描述：裁剪图片.
     *
     * @param file          File对象
     * @param desiredWidth  新图片的宽
     * @param desiredHeight 新图片的高
     * @return Bitmap 新图片
     */
    fun getCutBitmap(file: File, desiredWidth: Int, desiredHeight: Int): Bitmap? {
        var desiredWidth = desiredWidth
        var desiredHeight = desiredHeight
        var resizeBmp: Bitmap? = null
        val opts = BitmapFactory.Options()
        // 设置为true,decodeFile先不创建内存 只获取一些解码边界信息即图片大小信息
        opts.inJustDecodeBounds = true
        BitmapFactory.decodeFile(file.path, opts)
        // 获取图片的原始宽度
        val srcWidth = opts.outWidth
        // 获取图片原始高度
        val srcHeight = opts.outHeight
        val size = resizeToMaxSize(srcWidth, srcHeight, desiredWidth, desiredHeight)
        desiredWidth = size[0]
        desiredHeight = size[1]
        // 缩放的比例
        val scale = getMinScale(srcWidth, srcHeight, desiredWidth, desiredHeight)
        var destWidth = srcWidth
        var destHeight = srcHeight
        // 只缩小，不放大
        if (scale > 1) {
            destWidth = (srcWidth * scale).toInt()
            destHeight = (srcHeight * scale).toInt()
        }
        // 默认为ARGB_8888.
        opts.inPreferredConfig = Bitmap.Config.RGB_565
        // 以下两个字段需一起使用：
        // 产生的位图将得到像素空间，如果系统gc，那么将被清空。当像素再次被访问，如果Bitmap已经decode，那么将被自动重新解码
        opts.inPurgeable = true
        // 位图可以共享一个参考输入数据(inputstream、阵列等)
        opts.inInputShareable = true
        // 缩放的比例，缩放是很难按准备的比例进行缩放的，通过inSampleSize来进行缩放，其值表明缩放的倍数，SDK中建议其值是2的指数值
        val sampleSize = findBestSampleSize(srcWidth, srcHeight, destWidth, destHeight)
        opts.inSampleSize = sampleSize
        // 设置大小
        opts.outHeight = destHeight
        opts.outWidth = destWidth
        // 创建内存
        opts.inJustDecodeBounds = false
        // 使图片不抖动
        opts.inDither = false
        val bitmap = BitmapFactory.decodeFile(file.path, opts)
        if (bitmap != null) {
            resizeBmp = getCutBitmap(bitmap, desiredWidth, desiredHeight)
        }
        return resizeBmp
    }


    /**
     * 描述：裁剪图片.
     *
     * @param bitmap        the bitmap
     * @param desiredWidth  新图片的宽
     * @param desiredHeight 新图片的高
     * @return Bitmap 新图片
     */
    fun getCutBitmap(bitmap: Bitmap, desiredWidth: Int, desiredHeight: Int): Bitmap? {
        if (!checkSize(desiredWidth, desiredHeight) || checkBitmapNull(bitmap)) {
            return null
        }
        var resizeBmp: Bitmap? = null
        try {
            val width = bitmap.width
            val height = bitmap.height
            var offsetX = 0
            var offsetY = 0
            val newWidth = if (width > desiredWidth) {
                offsetX = (width - desiredWidth) / 2
                desiredWidth
            } else {
                width
            }
            val newHeight = if (height > desiredHeight) {
                offsetY = (height - desiredHeight) / 2
                desiredHeight
            } else {
                height
            }
            resizeBmp = Bitmap.createBitmap(bitmap, offsetX, offsetY, newWidth, newHeight)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (resizeBmp != bitmap) {
                bitmap.recycle()
            }
        }
        return resizeBmp
    }

    /**
     * 描述：获取图片尺寸
     *
     * @param file File对象
     * @return Bitmap 新图片
     */
    fun getBitmapSize(file: File): IntArray {
        val size = IntArray(2)
        val opts = BitmapFactory.Options()
        // 设置为true,decodeFile先不创建内存 只获取一些解码边界信息即图片大小信息
        opts.inJustDecodeBounds = true
        BitmapFactory.decodeFile(file.path, opts)
        // 获取图片的原始宽度高度
        size[0] = opts.outWidth
        size[1] = opts.outHeight
        return size
    }

    /**
     * 获取缩小的比例.
     *
     * @param srcWidth
     * @param srcHeight
     * @param desiredWidth
     * @param desiredHeight
     * @return
     */
    private fun getMinScale(srcWidth: Int, srcHeight: Int, desiredWidth: Int, desiredHeight: Int): Float {
        // 缩放的比例
        // 计算缩放比例，宽高的最小比例
        val scaleWidth = desiredWidth.toFloat() / srcWidth
        val scaleHeight = desiredHeight.toFloat() / srcHeight
        return if (scaleWidth > scaleHeight) {
            scaleWidth
        } else {
            scaleHeight
        }
    }

    /**
     *
     */
    private fun resizeToMaxSize(srcWidth: Int, srcHeight: Int, desiredWidth: Int, desiredHeight: Int): IntArray {
        var desiredWidth = desiredWidth
        var desiredHeight = desiredHeight
        val size = IntArray(2)
        if (desiredWidth <= 0) {
            desiredWidth = srcWidth
        }
        if (desiredHeight <= 0) {
            desiredHeight = srcHeight
        }
        if (desiredWidth > MAX_WIDTH) {
            // 重新计算大小
            desiredWidth = MAX_WIDTH
            val scaleWidth = desiredWidth.toFloat() / srcWidth
            desiredHeight = (desiredHeight * scaleWidth).toInt()
        }

        if (desiredHeight > MAX_HEIGHT) {
            // 重新计算大小
            desiredHeight = MAX_HEIGHT
            val scaleHeight = desiredHeight.toFloat() / srcHeight
            desiredWidth = (desiredWidth * scaleHeight).toInt()
        }
        size[0] = desiredWidth
        size[1] = desiredHeight
        return size
    }

    /**
     * 描述：旋转Bitmap为一定的角度.
     *
     * @param bitmap  the bitmap
     * @param degrees the degrees
     * @return the bitmap
     */
    fun rotateBitmap(bitmap: Bitmap, degrees: Float = 0f): Bitmap? {
        try {
            val m = Matrix()
            m.setRotate(degrees % 360)
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, m, false)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * 描述：旋转Bitmap为一定的角度并四周暗化处理.
     *
     * @param bitmap  the bitmap
     * @param degrees the degrees
     * @return the bitmap
     */
    fun rotateBitmapTranslate(bitmap: Bitmap, degrees: Float = 0f): Bitmap? {
        val mBitmap: Bitmap? = null
        val width: Float
        val height: Float
        try {
            val matrix = Matrix()
            if (degrees / 90 % 2 != 0f) {
                width = bitmap.width.toFloat()
                height = bitmap.height.toFloat()
            } else {
                width = bitmap.height.toFloat()
                height = bitmap.width.toFloat()
            }
            val cx = width / 2
            val cy = height / 2
            matrix.preTranslate(-cx, -cy)
            matrix.postRotate(degrees)
            matrix.postTranslate(cx, cy)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return mBitmap
    }

    /**
     * 转为圆角图片
     *
     * @param src         源图片
     * @param radius      圆角的度数
     * @param borderSize  边框尺寸
     * @param borderColor 边框颜色
     * @param recycle     是否回收
     * @return 圆角图片
     */
    fun getRoundCornerBitmap(src: Bitmap?, radius: Float, borderSize: Int = 1, @ColorInt borderColor: Int = Color.WHITE, recycle: Boolean = false): Bitmap? {
        if (isEmptyBitmap(src)) return null
        val width = src?.width ?: 0
        val height = src?.height ?: 0
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val ret = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val shader = BitmapShader(src, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        paint.shader = shader
        val canvas = Canvas(ret)
        val rectF = RectF(0f, 0f, width.toFloat(), height.toFloat())
        val halfBorderSize = borderSize / 2f
        rectF.inset(halfBorderSize, halfBorderSize)
        canvas.drawRoundRect(rectF, radius, radius, paint)
        if (borderSize > 0) {
            paint.shader = null
            paint.color = borderColor
            paint.style = Paint.Style.STROKE
            paint.setStrokeWidth(borderSize.toFloat())
            paint.strokeCap = Paint.Cap.ROUND
            canvas.drawRoundRect(rectF, radius, radius, paint)
        }
        if (recycle && !(src?.isRecycled ?: false)) src?.recycle()
        return ret
    }


    /**
     * 转换图片转换成圆形.
     *
     * @param bitmap 传入Bitmap对象
     * @return the bitmap
     */
    fun getRoundBitmap(bitmap: Bitmap?, recycle: Boolean = false): Bitmap? {
        var width = bitmap?.width ?: 0
        var height = bitmap?.height ?: 0
        val roundPx: Float
        val left: Float
        val top: Float
        val right: Float
        val bottom: Float
        val dst_left: Float
        val dst_top: Float
        val dst_right: Float
        val dst_bottom: Float
        if (width <= height) {
            roundPx = (width / 2).toFloat()
            top = 0f
            bottom = width.toFloat()
            left = 0f
            right = width.toFloat()
            height = width
            dst_left = 0f
            dst_top = 0f
            dst_right = width.toFloat()
            dst_bottom = width.toFloat()
        } else {
            roundPx = (height / 2).toFloat()
            val clip = ((width - height) / 2).toFloat()
            left = clip
            right = width - clip
            top = 0f
            bottom = height.toFloat()
            width = height
            dst_left = 0f
            dst_top = 0f
            dst_right = height.toFloat()
            dst_bottom = height.toFloat()
        }
        val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val color = -0xbdbdbe
        val paint = Paint()
        val src = Rect(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
        val dst = Rect(dst_left.toInt(), dst_top.toInt(), dst_right.toInt(), dst_bottom.toInt())
        val rectF = RectF(dst)
        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = color
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, src, dst, paint)
        if (recycle && !(bitmap?.isRecycled ?: false)) bitmap?.recycle()
        return output
    }

    /**
     * 转换图片转换成镜面效果的图片.
     *
     * @param bitmap 传入Bitmap对象
     * @return the bitmap
     */
    fun getReflectionBitmap(bitmap: Bitmap?): Bitmap? {
        var bitmap: Bitmap? = bitmap ?: return null
        try {
            val reflectionGap = 1
            val width = bitmap!!.width
            val height = bitmap.height
            val matrix = Matrix()
            matrix.preScale(1f, -1f)
            val reflectionImage = Bitmap.createBitmap(bitmap, 0, height / 2, width, height / 2, matrix, false)
            val bitmapWithReflection = Bitmap.createBitmap(width, height + height / 2, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmapWithReflection)
            canvas.drawBitmap(bitmap, 0f, 0f, null)
            val deafaultPaint = Paint()
            canvas.drawRect(0f, height.toFloat(), width.toFloat(), (height + reflectionGap).toFloat(), deafaultPaint)
            canvas.drawBitmap(reflectionImage, 0f, (height + reflectionGap).toFloat(), null)
            val paint = Paint()
            val shader = LinearGradient(0f, bitmap.height.toFloat(), 0f, (bitmapWithReflection.height + reflectionGap).toFloat(), 0x70ffffff, 0x00ffffff, TileMode.CLAMP)
            paint.shader = shader
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
            canvas.drawRect(0f, height.toFloat(), width.toFloat(), (bitmapWithReflection.height + reflectionGap).toFloat(), paint)
            bitmap = bitmapWithReflection
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return bitmap
    }

    /**
     * 添加圆角边框
     *
     * @param src          源图片
     * @param borderSize   边框尺寸
     * @param color        边框颜色
     * @param cornerRadius 圆角半径
     * @param recycle      是否回收
     * @return 圆角边框图
     */
    fun addCornerBorder(src: Bitmap, borderSize: Int, @ColorInt color: Int = Color.WHITE, @FloatRange(from = 0.0) cornerRadius: Float = 3f, recycle: Boolean = false): Bitmap? {
        return addBorder(src, borderSize, color, false, cornerRadius, recycle)
    }

    /**
     * 添加圆形边框
     *
     * @param src        源图片
     * @param borderSize 边框尺寸
     * @param color      边框颜色
     * @param recycle    是否回收
     * @return 圆形边框图
     */
    fun addCircleBorder(src: Bitmap, borderSize: Int = 1, @ColorInt color: Int = Color.WHITE, recycle: Boolean = false): Bitmap? {
        return addBorder(src, borderSize, color, true, 0f, recycle)
    }

    /**
     * 添加边框
     *
     * @param src          源图片
     * @param borderSize   边框尺寸
     * @param color        边框颜色
     * @param isCircle     是否画圆
     * @param cornerRadius 圆角半径
     * @param recycle      是否回收
     * @return 边框图
     */
    private fun addBorder(src: Bitmap, borderSize: Int, @ColorInt color: Int, isCircle: Boolean, cornerRadius: Float, recycle: Boolean = false): Bitmap? {
        if (isEmptyBitmap(src)) return null
        val ret = if (recycle) src else src.copy(src.config, true)
        val width = ret.width
        val height = ret.height
        val canvas = Canvas(ret)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = color
        paint.style = Paint.Style.STROKE
        paint.setStrokeWidth(borderSize.toFloat())
        if (isCircle) {
            val radius = Math.min(width, height) / 2f - borderSize / 2f
            canvas.drawCircle(width / 2f, height / 2f, radius, paint)
        } else {
            val halfBorderSize = borderSize shr 1
            val rectF = RectF(halfBorderSize.toFloat(), halfBorderSize.toFloat(), (width - halfBorderSize).toFloat(), (height - halfBorderSize).toFloat())
            canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, paint)
        }
        return ret
    }

    /**
     * 检查Bitmap为空
     */
    private fun checkBitmapNull(bitmap: Bitmap?): Boolean {
        if (bitmap == null) {
            return false
        }
        if (bitmap.width <= 0 || bitmap.height <= 0) {
            return false
        }
        return true
    }

    /**
     * 检查宽高
     */
    private fun checkSize(desiredWidth: Int, desiredHeight: Int): Boolean {
        if (desiredWidth <= 0 || desiredHeight <= 0) {
            return false
        }
        return true
    }

    /**
     * 根据Uri获取图片绝对路径，解决Android4.4以上版本Uri转换
     *
     * @param context
     * @param imageUri
     */
    @TargetApi(19)
    fun getAbsolutePath(context: Context?, imageUri: Uri?): String? {
        if (context == null || imageUri == null) return null
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, imageUri)) {
            if (isExternalStorageDocument(imageUri)) {
                val docId = DocumentsContract.getDocumentId(imageUri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().absolutePath + "/" + split[1]
                }
            } else if (isDownloadsDocument(imageUri)) {
                val id = DocumentsContract.getDocumentId(imageUri)
                val contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)!!)
                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(imageUri)) {
                val docId = DocumentsContract.getDocumentId(imageUri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = MediaStore.Images.Media._ID + "=?"
                val selectionArgs = arrayOf(split[1])
                return getDataColumn(context, contentUri, selection, selectionArgs)
            }
        } else if ("content".equals(imageUri.scheme, ignoreCase = true)) {
            return if (isGooglePhotosUri(imageUri)) imageUri.lastPathSegment else getDataColumn(context, imageUri, null, null)
        } else if ("file".equals(imageUri.scheme, ignoreCase = true)) {
            return imageUri.path
        }
        return null
    }

    fun getDataColumn(context: Context, uri: Uri?, selection: String?, selectionArgs: Array<String>?): String? {
        var cursor: Cursor? = null
        val column = MediaStore.Images.Media.DATA
        val projection = arrayOf(column)
        try {
            cursor = context.contentResolver.query(uri, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }
}