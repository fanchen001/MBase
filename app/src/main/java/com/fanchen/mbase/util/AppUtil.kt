package com.fanchen.mbase.util

import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.annotation.SuppressLint
import android.content.pm.ResolveInfo
import android.content.Intent
import java.security.MessageDigest
import android.content.pm.ApplicationInfo
import android.app.ActivityManager.RunningAppProcessInfo
import java.io.File


/**
 * App相关信息
 * Created by fanchen on 2018/9/3.
 */
object AppUtil {

    /**
     * 包名判断是否为主进程
     *
     * @param context
     * @return
     */
    fun isMainProcess(context: Context?): Boolean {
        return context?.getPackageName()?.equals(getProcessName(context)) ?: false
    }

    /**
     * 获取进程名称
     *
     * @param context
     * @return
     */
    fun getProcessName(context: Context?): String {
        try {
            val am = context?.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
            val runningApps = am?.runningAppProcesses ?: return ""
            val filter = runningApps.filter { it.pid == android.os.Process.myPid() && it.processName != null }
            return if (filter.isNotEmpty()) filter.get(0).processName else ""
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    /**
     * 获取包信息.
     *
     * @param context the context
     */
    fun getPackageInfo(context: Context?): PackageInfo? {
        var info: PackageInfo? = null
        try {
            val packageName = context?.packageName
            info = context?.packageManager?.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return info
    }

    /**
     * 获得APP的label名字
     *
     * @param context
     * @return
     */
    fun getAppName(context: Context?): String {
        try {
            val packageManager = context?.packageManager
            val packageInfo = packageManager?.getPackageInfo(context.packageName, 0)
            val labelRes = packageInfo?.applicationInfo?.labelRes ?: return ""
            return context.resources?.getString(labelRes) ?: ""
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    /**
     * 获取应用程序版本名称信息
     */
    fun getVersionName(context: Context?): String {
        try {
            val packageManager = context?.packageManager
            val packageInfo = packageManager?.getPackageInfo(context.packageName, 0)
            return packageInfo?.versionName ?: ""
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    fun getVersionCode(context: Context?): Int {
        try {
            val packageManager = context?.packageManager
            val packageInfo = packageManager?.getPackageInfo(context.packageName, 0)
            return packageInfo?.versionCode ?: 0
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0
    }

    /**
     * 获取应用程序包名
     */
    fun getPackageName(context: Context?): String? {
        return context?.packageName
    }

    /**
     * 獲取app Launcher activity
     */
    fun getLauncherClassName(context: Context?): String {
        try {
            val packageManager = context?.packageManager
            val intent = Intent(Intent.ACTION_MAIN)
            intent.`package` = context?.packageName
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            val info: ResolveInfo? = packageManager?.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) ?: packageManager?.resolveActivity(intent, 0)
            return info?.activityInfo?.name ?: ""
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    /**
     * 获取系统中所有的应用
     *
     * @param context 上下文
     * @param system 是否包含系统应用
     * @return 应用信息List
     */
    fun getAllApps(context: Context, system: Boolean = false): List<PackageInfo> {
        val apps = ArrayList<PackageInfo>()
        val pManager = context.packageManager
        val paklist = pManager.getInstalledPackages(0)
        for (pak in paklist) {
            if (system) {
                apps.add(pak)
            } else if (pak.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM <= 0) {
                apps.add(pak)
            }
        }
        return apps
    }


    /**
     * 获取应用签名
     *
     * @param context 上下文
     * @param pkgName 包名
     * @return 返回应用的签名
     */
    fun getSign(context: Context?, pkgName: String): String? {
        try {
            val pis = context?.packageManager?.getPackageInfo(pkgName, PackageManager.GET_SIGNATURES)
            return hexdigest(pis?.signatures?.get(0)?.toByteArray())
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }


    /**
     * 将签名字符串转换成需要的32位签名
     *
     * @param paramArrayOfByte 签名byte数组
     * @return 32位签名字符串
     */
    private fun hexdigest(paramArrayOfByte: ByteArray?): String {
        val hexDigits = charArrayOf(48.toChar(), 49.toChar(), 50.toChar(), 51.toChar(), 52.toChar(), 53.toChar(), 54.toChar(), 55.toChar(), 56.toChar(), 57.toChar(), 97.toChar(), 98.toChar(), 99.toChar(), 100.toChar(), 101.toChar(), 102.toChar())
        try {
            val localMessageDigest = MessageDigest.getInstance("MD5")
            localMessageDigest.update(paramArrayOfByte)
            val arrayOfByte = localMessageDigest.digest()
            val arrayOfChar = CharArray(32)
            var i = 0
            var j = 0
            while (true) {
                if (i >= 16) {
                    return String(arrayOfChar)
                }
                val k = arrayOfByte[i].toInt()
                arrayOfChar[j] = hexDigits[0xF and k.ushr(4)]
                arrayOfChar[++j] = hexDigits[k and 0xF]
                i++
                j++
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }


    /**
     * 判断某个服务是否正在运行的方法
     *
     * @param mContext
     * @param serviceName 是包名+服务的类名（例如：net.loonggg.testbackstage.TestService）
     * @return true代表正在运行，false代表服务没有正在运行
     */
    fun isServiceWork(mContext: Context?, serviceName: String): Boolean {
        try {
            val myAM = mContext?.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
            val myList = myAM?.getRunningServices(50)
            myList?.forEach { if (it.service.className.toString().equals(serviceName)) return true }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }


    /**
     * 清理后台进程与服务
     *
     * @param context 应用上下文对象context
     * @return 被清理的数量
     */
    @SuppressLint("MissingPermission")
    fun gc(context: Context?): Int {
        var count = 0 // 清理掉的进程数
        try {
            val am = context?.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
            val serviceList = am?.getRunningServices(100)// 获取正在运行的service列表
            serviceList?.forEach {
                if (!it.pid.equals(android.os.Process.myPid())) {
                    android.os.Process.killProcess(it.pid)
                    count++
                }
            }
            val processList = am?.runningAppProcesses // 获取正在运行的进程列表
            processList?.forEach {
                if (it.importance > RunningAppProcessInfo.IMPORTANCE_VISIBLE) {
                    // pkgList 得到该进程下运行的包名
                    it.pkgList?.forEach {
                        am.killBackgroundProcesses(it)
                        count++
                    }
                }
            }
        } catch (e: Exception) {
            e.stackTrace
        }
        return count
    }

    /**
     * 获取manifest有注册的权限
     *
     * @return
     */
    fun getRegPermission(context: Context?, name: String): Array<String>? {
        val perls = ArrayList<String>()
        try {
            val appInfo = context?.getPackageManager()?.getPackageInfo(name, PackageManager.GET_PERMISSIONS)
            appInfo?.requestedPermissions?.forEach {  perls.add(it) }
            return perls.toTypedArray()
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * 清除本应用内部缓存(/data/data/com.xxx.xxx/cache)
     */
    fun cleanInternalCache(context: Context?) {
        deleteFilesByDirectory(context?.getCacheDir())
    }

    /**
     * 清除本应用所有数据库(/data/data/com.xxx.xxx/databases)
     */
    fun cleanDatabases(context: Context?) {
        val dir = context?.getFilesDir()?.getPath() ?: ""
        val file = context?.getPackageName() + "/databases"
        deleteFilesByDirectory(File(dir + file))
    }

    /**
     * 清除本应用SharedPreference(/data/data/com.xxx.xxx/shared_prefs)
     */
    fun cleanSharedPreference(context: Context?) {
        val dir = context?.getFilesDir()?.getPath() ?: ""
        val file = context?.getPackageName() + "/shared_prefs"
        deleteFilesByDirectory(File(dir + file))
    }

    /**
     * 按名字清除本应用数据库
     *
     * @param dbName 数据库名称
     */
    fun cleanDatabaseByName(context: Context?, dbName: String) {
        context?.deleteDatabase(dbName)
    }

    /**
     * 清除/data/data/com.xxx.xxx/files下的内容
     */
    fun cleanFiles(context: Context?) {
        deleteFilesByDirectory(context?.getFilesDir())
    }

    /**
     * 清除外部cache下的内容(/mnt/sdcard/android/data/com.xxx.xxx/cache)
     */
    fun cleanExternalCache(context: Context?) {
        if (SDCardUtil.isSDCardEnable()) {
            deleteFilesByDirectory(context?.getExternalCacheDir())
        }
    }

    /**
     * 清除自定义路径下的文件，使用需小心，请不要误删。而且只支持目录下的文件删除
     *
     * @param filePath 文件路径
     */
    fun cleanCustomCache(filePath: String) {
        deleteFilesByDirectory(File(filePath))
    }

    /**
     * 清除本应用所有的数据
     *
     * @param filePath 文件路径
     */
    @JvmStatic
    fun cleanApplicationData(context: Context?, filePath: Array<String>) {
        cleanInternalCache(context)
        cleanExternalCache(context)
        cleanDatabases(context)
        cleanSharedPreference(context)
        cleanFiles(context)
        for (fp in filePath) {
            cleanCustomCache(fp)
        }
    }

    /**
     * 删除方法 这里只会删除某个文件夹下的文件。 此操作较危险，请慎用；
     *
     * @param directory 文件夹File对象
     */
    private fun deleteFilesByDirectory(directory: File?) {
        FileUtil.deleteDirectory(directory?.getAbsolutePath() ?: "")
    }

}