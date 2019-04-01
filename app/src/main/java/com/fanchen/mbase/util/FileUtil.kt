package com.fanchen.mbase.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.text.TextUtils
import android.util.Base64
import java.io.*


/**
 * 文件操作相关
 * Created by fanchen on 2018/9/3.
 */
object FileUtil {
    /**
     *
     */
    fun file2Base64(file: File?): String {
        return Base64.encodeToString(StreamUtil.file2Byte(file), Base64.DEFAULT)
    }

    /**
     *
     */
    fun base642File(base64 : String,file: File?){
        byte2File(Base64.decode(base64,Base64.DEFAULT),file)
    }

    /**
     * 将byte保存为文件
     */
    fun byte2File(byte: ByteArray?, file: File?): Boolean {
        try {
            LogUtil.e("byte2File", file?.absolutePath ?: "")
            if (file != null) {
                val parent = file.parentFile
                if (parent != null && !parent.exists())
                    parent.mkdir()
                if (!file.exists())
                    file.createNewFile()
            }
            FileOutputStream(file).use {
                it.write(byte)
                it.flush()
            }
            return true
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return false
    }

    /**
     * 将byte保存为文件
     */
    fun byte2File(byte: ByteArray?, file: String): Boolean {
        return byte2File(byte, File(file))
    }

    /**
     * bitmap保存为图片
     */
    fun bitmap2File(bmp: Bitmap?, file: File?): Boolean {
        val byte = StreamUtil.bitmap2Byte(bmp)
        return byte2File(byte, file)
    }

    /**
     * bitmap保存为图片
     */
    fun bitmap2File(bmp: Bitmap, path: String): Boolean {
        return bitmap2File(bmp, File(path))
    }

    /**
     * 获取assets里文件的byte
     */
    fun getByteFromAssets(context: Context?, filename: String): ByteArray? {
        val fis = context?.assets?.open(filename)
        return StreamUtil.stream2bytes(fis)
    }

    /**
     * 拷贝assets里面的文件
     */
    fun copyFile(context: Context?, filename: String, newFile: File): Boolean {
        return byte2File(getByteFromAssets(context, filename), newFile)
    }


    /**
     * 拷贝assets里面的文件
     */
    @SuppressLint("SdCardPath")
    fun copyFile(context: Context?, filename: String, newName: String): Boolean {
        val packageName = context?.packageName ?: ""
        val dbPath = ("/data/data/$packageName/databases/$newName")
        val file = File(dbPath)
        if (file.exists()) return true
        return copyFile(context, filename, file)
    }

    fun copyFile(context: Context?, filename: String): Boolean {
        return copyFile(context, filename, filename)
    }

    fun copyFileAsync(context: Context?, filename: String) {
        Thread { copyFile(context, filename) }.start()
    }

    /**
     * 从InputStream里面拷贝文件
     */
    fun copyFile(fis: InputStream?, newFile: File): Boolean {
        return byte2File(StreamUtil.stream2bytes(fis), newFile)
    }

    /**
     * 从InputStream里面拷贝文件
     */
    fun copyFile(fis: InputStream?, newFile: String): Boolean {
        return copyFile(fis, File(newFile))
    }

    /**
     * 拷贝文件
     */
    fun copyFile(file: String, newFile: String): Boolean {
        return copyFile(File(file), File(newFile))
    }

    /**
     * 拷贝文件
     */
    fun copyFile(file: File, newFile: File): Boolean {
        try {
            val byte = StreamUtil.file2Byte(file)
            FileOutputStream(newFile).use {
                it.write(byte)
                it.flush()
            }
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    /**
     * 删除目录（文件夹）以及目录下的文件
     *
     * @param sPath
     * @param boolean 是否删除根目录
     * 被删除目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    fun deleteDirectory(sPath: String, boolean: Boolean = true): Boolean {
        if (TextUtils.isEmpty(sPath)) return false
        val dirFile = File(if (!sPath.endsWith(File.separator)) sPath + File.separator else sPath)
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if (!dirFile.exists() || !dirFile.isDirectory) {
            return false
        }
        // 删除文件夹下的所有文件(包括子目录)
        val files = dirFile.listFiles()
        var flag = false
        for (newFile in files) {
            // 删除子文件
            if (newFile.isFile) {
                flag = deleteFile(newFile.absolutePath)
                if (!flag) break
            } else { // 删除子目录
                flag = deleteDirectory(newFile.absolutePath)
                if (!flag) break
            }
        }
        return if (!flag) false else if (boolean) dirFile.delete() else true
        // 删除当前目录
    }

    /**
     * 删除单个文件
     *
     * @param sPath
     * 被删除文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    fun deleteFile(sPath: String): Boolean {
        var flag = false
        val file = File(sPath)
        // 路径为文件且不为空则进行删除
        if (file.isFile && file.exists()) {
            file.delete()
            flag = true
        }
        return flag
    }

    /**
     * 文件或者目录重命名
     * @param oldFilePath 旧文件路径
     * @param newName 新的文件名,可以是单个文件名和绝对路径
     * @return
     */
    fun renameTo(oldFilePath: String, newName: String): Boolean {
        var newNames = newName
        try {
            val oldFile = File(oldFilePath)
            //若文件存在
            if (!oldFile.exists()) return false
            //判断是全路径还是文件名
            if (newNames.indexOf("/") < 0 && newNames.indexOf("\\") < 0) {
                //单文件名，判断是windows还是Linux系统
                val absolutePath = oldFile.absolutePath
                if (newNames.indexOf("/") > 0) { //Linux系统
                    newNames = absolutePath.substring(0, absolutePath.lastIndexOf("/") + 1) + newNames
                } else {
                    newNames = absolutePath.substring(0, absolutePath.lastIndexOf("\\") + 1) + newNames
                }
            }
            val file = File(newNames)
            //判断重命名后的文件是否存在
            if (!file.exists()) {
                //不存在，重命名
                return oldFile.renameTo(file)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

}