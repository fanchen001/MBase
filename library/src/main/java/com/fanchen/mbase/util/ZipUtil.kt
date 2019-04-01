package com.fanchen.mbase.util

import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import java.util.zip.ZipFile


/**
 * ZipUtil
 * Created by fanchen on 2018/9/4.
 */
object ZipUtil {


    /**
     * 压缩文件
     *
     * @param resFilePath 待压缩文件路径         输入
     * @param zipFilePath 压缩文件路径           输出
     * @param comment     压缩文件的注释
     * @return `true`: 压缩成功<br></br>`false`: 压缩失败
     */
    fun zipFile(resFilePath: String, zipFilePath: String, comment: String? = null): Boolean {
        return zipFile(getFileByPath(resFilePath), getFileByPath(zipFilePath), comment)
    }

    /**
     * 压缩文件
     *
     * @param resFile 待压缩文件
     * @param zipFile 压缩文件
     * @param comment 压缩文件的注释
     * @return `true`: 压缩成功<br></br>`false`: 压缩失败
     * @throws IOException IO错误时抛出
     */
    fun zipFile(resFile: File?, zipFile: File?, comment: String? = null): Boolean {
        if (resFile == null || zipFile == null) return false
        ZipOutputStream(FileOutputStream(zipFile)).use {
            return zipFile(resFile, "", it, comment)
        }
    }

    /**
     * 压缩文件
     *
     * @param resFile  待压缩文件
     * @param rootPath 相对于压缩文件的路径
     * @param zos      压缩文件输出流
     * @param comment  压缩文件的注释
     * @return `true`: 压缩成功<br></br>`false`: 压缩失败
     */
    private fun zipFile(resFile: File, root: String, zos: ZipOutputStream, comment: String? = null): Boolean {
        val rootPath = root + (if (isSpace(root)) "" else File.separator) + resFile.getName()
        if (resFile.isDirectory()) {
            val fileList = resFile.listFiles()
            // 如果是空文件夹那么创建它，我把'/'换为File.separator测试就不成功，eggPain
            if (fileList?.size ?: -1 <= 0) {
                val entry = ZipEntry(rootPath + '/')
                if (!isSpace(comment))
                    entry.setComment(comment)
                zos.putNextEntry(entry)
                zos.closeEntry()
            } else {
                for (file in fileList) {
                    // 如果递归返回false则返回false
                    if (!zipFile(file, rootPath, zos, comment)) return false
                }
            }
        } else {
            BufferedInputStream(FileInputStream(resFile)).use {
                val entry = ZipEntry(rootPath)
                if (!isSpace(comment)) entry.setComment(comment)
                zos.putNextEntry(entry)
                zos.write(it.readBytes())
                zos.closeEntry()
            }
        }
        return true
    }

    /**
     * 解压带有关键字的文件
     *
     * @param zipFilePath 待解压文件路径
     * @param destDirPath 目标目录路径
     * @param keyword     关键字
     * @return 返回带有关键字的文件链表
     * @throws IOException IO错误时抛出
     */
    fun unzipFileByKeyword(zipFilePath: String, destDirPath: String, keyword: String? = null): List<File> {
        return unzipFileByKeyword(getFileByPath(zipFilePath), getFileByPath(destDirPath), keyword)
    }

    /**
     * 解压带有关键字的文件
     *
     * @param zipFile 待解压文件
     * @param destDir 目标目录
     * @param keyword 关键字
     * @return 返回带有关键字的文件链表
     * @throws IOException IO错误时抛出
     */
    fun unzipFileByKeyword(zipFile: File?, destDir: File?, keyword: String? = null): List<File> {
        val files = ArrayList<File>()
        if (zipFile == null || destDir == null) return files
        val zf = ZipFile(zipFile)
        if (isSpace(keyword)) {
            for (entry in zf.entries()) {
                val entryName = entry.name
                if (!unzipChildFile(destDir, files, zf, entry, entryName))
                    return files
            }
        } else {
            for (entry in zf.entries()) {
                val entryName = entry.name
                if (keyword != null && entryName.contains(keyword)) {
                    if (!unzipChildFile(destDir, files, zf, entry, entryName))
                        return files
                }
            }
        }
        return files
    }

    private fun unzipChildFile(destDir: File, files: MutableList<File>, zf: ZipFile, entry: ZipEntry, entryName: String): Boolean {
        val filePath = destDir.toString() + File.separator + entryName
        val file = File(filePath)
        files.add(file)
        if (entry.isDirectory) {
            if (!createOrExistsDir(file)) return false
        } else {
            if (!createOrExistsFile(file)) return false
            BufferedOutputStream(FileOutputStream(file)).use {
                it.write(BufferedInputStream(zf.getInputStream(entry)).readBytes())
            }
        }
        return true
    }

    private fun createOrExistsDir(file: File?): Boolean {
        return file != null && if (file.exists()) file.isDirectory else file.mkdirs()
    }

    private fun createOrExistsFile(file: File?): Boolean {
        if (file?.exists() ?: false)
            return file?.isFile ?: false
        if (!createOrExistsDir(file?.parentFile)) return false
        try {
            return file?.createNewFile() ?: false
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    private fun getFileByPath(filePath: String): File? {
        return if (isSpace(filePath)) null else File(filePath)
    }

    private fun isSpace(s: String?): Boolean {
        s?.forEach {
            if (!Character.isWhitespace(it)) return false
        }
        return true
    }
}