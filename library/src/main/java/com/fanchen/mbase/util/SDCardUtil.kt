package com.fanchen.mbase.util

import android.content.Context
import android.os.Environment
import android.os.StatFs
import android.text.format.Formatter
import java.io.File

/**
 * 内存卡相关
 * Created by fanchen on 2018/9/3.
 */
object SDCardUtil {
    /**
     * 获得文件路径的剩余大小
     *
     * @param context
     * 上下文
     * @param fileDir
     * 文件路径
     * @return 剩余内存大小
     */
    fun getAvailSizeFromDir(context: Context, fileDir: File): String {
        return Formatter.formatFileSize(context,getLongAvailSizeFromDir( fileDir))
    }

    fun getLongAvailSizeFromDir(fileDir: File): Long {
        val stat = StatFs(fileDir.getPath())
        val blockSize = stat.blockSize.toLong() // 获得一个扇区的大小
        val availableBlocks = stat.availableBlocks.toLong() // 获得可用的扇区数量
        return availableBlocks * blockSize
    }

    /**
     * 获得文件路径的全部大小
     *
     * @param context
     * 上下文
     * @param fileDir
     * 文件路径
     * @return 剩余内存大小
     */
    fun getTotelSizeFromDir(context: Context, fileDir: File): String {
        return Formatter.formatFileSize(context, getFileAllSize(fileDir.getPath()))
    }

    /**
     * 获取文件夹大小
     *
     * @author YOLANDA
     * @param path
     * @return
     */
    fun getFileAllSize(path: String): Long {
        val file = File(path)
        if (file.exists()) {
            if (file.isDirectory()) {
                val children = file.listFiles()
                var size: Long = 0
                for (f in children)
                    size += getFileAllSize(f.getPath())
                return size
            } else {
                return file.length()
            }
        } else {
            return 0
        }
    }

    /**
     * 判断SDCard是否可用
     *
     * @return
     */
    fun isSDCardEnable(): Boolean {
        return Environment.getExternalStorageState()?.equals(Environment.MEDIA_MOUNTED) ?: false
    }

    /**
     * 获取SD卡路径
     *
     * @return
     */
    fun getSDCardPath(): String {
        return Environment.getExternalStorageDirectory().absolutePath + File.separator
    }

    /**
     * 获取SD卡的剩余容量 单位byte
     *
     * @return
     */
    fun getSDCardAllSize(): Long {
        if (isSDCardEnable()) {
            val stat = StatFs(getSDCardPath())
            // 获取空闲的数据块的数量
            val availableBlocks = stat.availableBlocks.toLong()
            // 获取单个数据块的大小（byte）
            val blockSize = stat.blockSize.toLong()
            return blockSize * availableBlocks
        }
        return 0
    }
}