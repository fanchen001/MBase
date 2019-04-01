package com.fanchen.mbase.util

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.telephony.TelephonyManager
import java.io.File
import java.util.regex.Pattern
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.app.ActivityManager
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader
import android.media.AudioManager
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.annotation.TargetApi
import android.content.res.Configuration
import android.provider.Settings
import android.app.AppOpsManager
import android.os.Binder

/**
 * 设备相关信息
 * Created by fanchen on 2018/9/3.
 */
object DeviceUtil {

    /**
     * 获取手机状态信息
     *
     * 需添加权限 `<uses-permission android:name="android.permission.READ_PHONE_STATE"/>`
     *
     * @return DeviceId(IMEI) = 99000311726612<br></br>
     * DeviceSoftwareVersion = 00<br></br>
     * Line1Number =<br></br>
     * NetworkCountryIso = cn<br></br>
     * NetworkOperator = 46003<br></br>
     * NetworkOperatorName = 中国电信<br></br>
     * NetworkType = 6<br></br>
     * honeType = 2<br></br>
     * SimCountryIso = cn<br></br>
     * SimOperator = 46003<br></br>
     * SimOperatorName = 中国电信<br></br>
     * SimSerialNumber = 89860315045710604022<br></br>
     * SimState = 5<br></br>
     * SubscriberId(IMSI) = 460030419724900<br></br>
     * VoiceMailNumber = *86<br></br>
     */
    @SuppressLint("HardwareIds", "MissingPermission")
    fun getPhoneStatus(context: Context?): String {
        var str = ""
        try {
            val tm = context?.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
            str += "DeviceId(IMEI) = " + tm?.deviceId + "\n"
            str += "DeviceSoftwareVersion = " + tm?.deviceSoftwareVersion + "\n"
            str += "Line1Number = " + tm?.line1Number + "\n"
            str += "NetworkCountryIso = " + tm?.networkCountryIso + "\n"
            str += "NetworkOperator = " + tm?.networkOperator + "\n"
            str += "NetworkOperatorName = " + tm?.networkOperatorName + "\n"
            str += "NetworkType = " + tm?.networkType + "\n"
            str += "PhoneType = " + tm?.phoneType + "\n"
            str += "SimCountryIso = " + tm?.simCountryIso + "\n"
            str += "SimOperator = " + tm?.simOperator + "\n"
            str += "SimOperatorName = " + tm?.simOperatorName + "\n"
            str += "SimSerialNumber = " + tm?.simSerialNumber + "\n"
            str += "SimState = " + tm?.simState + "\n"
            str += "SubscriberId(IMSI) = " + tm?.subscriberId + "\n"
            str += "VoiceMailNumber = " + tm?.voiceMailNumber + "\n"
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return str
    }

    /**
     * 判断悬浮窗权限（目前主要用户魅族与小米的检测）。
     */
    fun isFloatWindowOpAllowed(context: Context): Boolean {
        val version = Build.VERSION.SDK_INT
        return if (version >= 19) {
            checkOp(context, 24)  // 24 是AppOpsManager.OP_SYSTEM_ALERT_WINDOW 的值，该值无法直接访问
        } else {
            try {
                context.applicationInfo.flags and (1 shl 27) == 1 shl 27
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    @TargetApi(19)
    private fun checkOp(context: Context, op: Int): Boolean {
        val version = Build.VERSION.SDK_INT
        if (version >= Build.VERSION_CODES.KITKAT) {
            val manager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            try {
                val method = manager.javaClass.getDeclaredMethod("checkOp", Int::class.javaPrimitiveType, Int::class.javaPrimitiveType, String::class.java)
                val property = method.invoke(manager, op, Binder.getCallingUid(), context.packageName) as Int
                return AppOpsManager.MODE_ALLOWED == property
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return false
    }

    /**
     * 获取屏幕休眠时间，需要WRITE_SETTINGS权限
     *
     * @param context
     * 上下文
     * @return 屏幕休眠时间，单位毫秒，默认30000
     */
    fun getScreenDormantTime(context: Context?): Int {
        try {
            return Settings.System.getInt(context?.contentResolver, Settings.System.SCREEN_OFF_TIMEOUT, 30000)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return -1
    }

    /**
     * 设置屏幕休眠时间，需要WRITE_SETTINGS权限
     *
     * @param context
     * 上下文
     * @param millis    时间
     * @return 设置是否成功
     */
    fun setScreenDormantTime(context: Context?, millis: Int): Boolean {
        try {
            return Settings.System.putInt(context?.contentResolver, Settings.System.SCREEN_OFF_TIMEOUT, millis)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    /**
     * 获取飞行模式的状态，需要WRITE_APN_SETTINGS权限
     *
     * @param context
     * 上下文
     * @return 1：打开；0：关闭；默认：关闭
     */
    fun getAirplaneModeState(context: Context?): Int {
        try {
            return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                Settings.System.getInt(context?.contentResolver, Settings.System.AIRPLANE_MODE_ON, 0)
            } else {
                Settings.Global.getInt(context?.contentResolver, Settings.Global.AIRPLANE_MODE_ON, 0)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return -1
    }

    /**
     * 判断飞行模式是否打开，需要WRITE_APN_SETTINGS权限
     *
     * @param context
     * 上下文
     * @return true：打开；false：关闭；默认关闭
     */
    fun isAirplaneModeOpen(context: Context?): Boolean {
        return if (getAirplaneModeState(context) == 1) true else false
    }

    /**
     * 判断设备是否是手机
     *
     * @return `true`: 是<br></br>`false`: 否
     */
    fun isPhone(context: Context?): Boolean {
        val tm = context?.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
        return tm != null && tm.phoneType != TelephonyManager.PHONE_TYPE_NONE
    }

    /**
     * 判断是否是平板
     *
     * @return `true`: 是<br></br>`false`: 否
     */
    fun isTablet(context: Context?): Boolean {
        return context?.getResources()?.getConfiguration()?.screenLayout ?: 0 and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE
    }

    /**
     * 设置飞行模式的状态，需要WRITE_APN_SETTINGS权限
     *
     * @param context
     * 上下文
     * @param enable
     * 飞行模式的状态
     * @return 设置是否成功
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun setAirplaneMode(context: Context?, enable: Boolean): Boolean {
        var result = true
        try {
            // 如果飞行模式当前的状态与要设置的状态不一样
            if (isAirplaneModeOpen(context) != enable) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    result = Settings.System.putInt(context?.contentResolver, Settings.System.AIRPLANE_MODE_ON, if (enable) 1 else 0)
                } else {
                    result = Settings.Global.putInt(context?.contentResolver, Settings.Global.AIRPLANE_MODE_ON, if (enable) 1 else 0)
                }
                // 发送飞行模式已经改变广播
                context?.sendBroadcast(Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    /**
     * 获取应用程序的IMEI号
     */
    @SuppressLint("MissingPermission")
    fun getIMEI(context: Context?): String {
        try {
            val telecomManager = context?.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
            return telecomManager?.deviceId ?: ""
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    /**
     * 获取设备的系统版本号
     */
    fun getDeviceSDK(): Int {
        return Build.VERSION.SDK_INT
    }

    /**
     * 获取设备的型号
     */
    fun getDeviceName(): String {
        return Build.MODEL
    }

    /**
     * 是否Dalvik模式
     *
     * @return 结果
     */
    fun isDalvik(): Boolean {
        return "Dalvik" == getCurrentRuntimeValue()
    }

    /**
     * 是否ART模式
     *
     * @return 结果
     */
    fun isART(): Boolean {
        val currentRuntime = getCurrentRuntimeValue()
        return "ART" == currentRuntime || "ART debug build" == currentRuntime
    }

    /**
     * 获取手机当前的Runtime
     *
     * @return 正常情况下可能取值Dalvik, ART, ART debug build;
     */
    fun getCurrentRuntimeValue(): String {
        try {
            val systemProperties = Class.forName("android.os.SystemProperties")
            val get = systemProperties.getMethod("get", String::class.java, String::class.java) ?: return "Exception"
            val value = get.invoke(systemProperties, "persist.sys.dalvik.vm.lib", "Dalvik") as String
            if ("libdvm.so" == value) {
                return "Dalvik"
            } else if ("libart.so" == value) {
                return "ART"
            } else if ("libartd.so" == value) {
                return "ART debug build"
            }
            return value
        } catch (e: Exception) {
            return "Exception"
        }
    }


    /**
     * 获取sim卡卡号
     *
     * @return
     */
    @SuppressLint("MissingPermission")
    fun getSimNum(context: Context?): String {
        try {
            val tm = context?.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
            var simState = true
            if (tm?.simState == TelephonyManager.SIM_STATE_ABSENT) { // 未发现sim卡
                simState = false
            }
            if (simState) {
                val simNum = tm?.simSerialNumber ?: ""
                if (simNum != null) {
                    return simNum
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    /**
     * 获取设备id
     *
     * @return
     */
    @SuppressLint("MissingPermission")
    fun getDeviceId(context: Context?): String {
        try {
            val tm = context?.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
            var simState = true
            if (tm?.simState == TelephonyManager.SIM_STATE_ABSENT) { // 未发现sim卡
                simState = false
            }
            if (simState) {
                val deviceId = tm?.deviceId ?: ""
                if (deviceId != null) {
                    return deviceId
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }


    /**
     * 获取手机 IMSI 码
     *
     * @return
     */
    @SuppressLint("MissingPermission")
    fun getIMSI(context: Context?): String {
        try {
            val tm = context?.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
            val imsi = tm?.subscriberId
            return imsi ?: ""
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    /**
     * 获取手机号码，绝大部分时候不灵
     *
     * @return
     */
    @SuppressLint("MissingPermission")
    fun getPhoneNO(context: Context?): String {
        try {
            val tm = context?.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
            val imsi = tm?.line1Number
            return imsi ?: ""
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    /** 返回手机服务商名字  */
    fun getProvidersName(context: Context): String {
        var ProvidersName: String? = null
        // 返回唯一的用户ID;就是这张卡的编号神马的
        val IMSI = getIMSI(context)
        // IMSI号前面3位460是国家，紧接着后面2位00 02是中国移动，01是中国联通，03是中国电信。
        if (IMSI.startsWith("46000") || IMSI.startsWith("46002")) {
            ProvidersName = "中国移动"
        } else if (IMSI.startsWith("46001")) {
            ProvidersName = "中国联通"
        } else if (IMSI.startsWith("46003")) {
            ProvidersName = "中国电信"
        } else {
            ProvidersName = "其他服务商:" + IMSI
        }
        return ProvidersName
    }

    /** 获取当前设备的SN  */
    @SuppressLint("MissingPermission")
    fun getSimSN(context: Context?): String {
        var simSN: String = ""
        try {
            val tm = context?.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
            simSN = tm?.simSerialNumber ?: ""
        } catch (e: Exception) {
        }
        return simSN
    }

    /**
     * 获取cpu核心数
     * filesystem at "/sys/devices/system/cpu"
     * @return The number of cores, or 1 if failed to get result
     */
    fun getCpuCores(): Int {
        try {
            val dir = File("/sys/devices/system/cpu/")
            val files = dir.listFiles { file ->
                if (Pattern.matches("cpu[0-9]", file.getName())) true else false
            }
            return files.size
        } catch (e: Exception) {
            e.printStackTrace()
            return 1
        }
    }

    /**
     * 获取系统可用内存空间大小
     *
     * @param context
     * @return long byte大小
     */
    fun getAvailMemSize(context: Context?): Long {
        try {
            val am = context?.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
            val outInfo = ActivityManager.MemoryInfo()
            am?.getMemoryInfo(outInfo)
            return outInfo.availMem
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0
    }

    /**
     * 获取系统全部的内存空间大小
     *
     * @param context
     * @return long byte大小
     */
    fun getTotalMemSize(context: Context): Long {
        try {
            val file = File("/proc/meminfo")
            val fis = FileInputStream(file)
            // MemTotal: 516452 kB
            val br = BufferedReader(InputStreamReader(fis))
            val line = br.readLine()
            val chars = line.toCharArray()
            val sb = StringBuilder()
            for (c in chars) {
                if (c >= '0' && c <= '9') {
                    sb.append(c)
                }
            }
            br.close()
            fis.close()
            return (Integer.parseInt(sb.toString()) * 1024).toLong()
        } catch (e: Exception) {
            e.printStackTrace()
            return 0
        }
    }

    /**
     * 获取蓝牙的状态
     *
     * @return 取值为BluetoothAdapter的四个静态字段：STATE_OFF, STATE_TURNING_OFF,
     * STATE_ON, STATE_TURNING_ON
     * @throws Exception
     * 没有找到蓝牙设备
     */
    @SuppressLint("MissingPermission")
    fun getBluetoothState(): Int {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        return bluetoothAdapter?.state ?: -1
    }

    /**
     * 判断蓝牙是否打开
     *
     * @return true：已经打开或者正在打开；false：已经关闭或者正在关闭
     * 没有找到蓝牙设备
     */
    fun isBluetoothOpen(): Boolean {
        return if (getBluetoothState() == BluetoothAdapter.STATE_ON || getBluetoothState() == BluetoothAdapter.STATE_TURNING_ON) true else false
    }

    /**
     * 设置蓝牙状态
     *
     * @param enable
     * 打开
     * 没有找到蓝牙设备
     */
    @SuppressLint("MissingPermission")
    fun setBluetooth(enable: Boolean) {
        // 如果当前蓝牙的状态与要设置的状态不一样
        if (isBluetoothOpen() != enable) {
            // 如果是要打开就打开，否则关闭
            if (enable) {
                BluetoothAdapter.getDefaultAdapter()?.enable()
            } else {
                BluetoothAdapter.getDefaultAdapter()?.disable()
            }
        }
    }


    /**
     * 获取铃声音量，需要WRITE_APN_SETTINGS权限
     *
     * @param context
     * 上下文
     * @return 铃声音量，取值范围为0-7；默认为0
     */
    fun getRingVolume(context: Context?): Int {
        try {
            val am = context?.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
            return am?.getStreamVolume(AudioManager.STREAM_RING) ?: 0
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0
    }

    /**
     * 获取媒体音量
     *
     * @param context
     * 上下文
     * @param ringVloume 音量
     */
    fun setRingVolume(context: Context?, ringVloume: Int = 0) {
        var vloume = ringVloume % 7
        if (ringVloume == 0) {
            vloume = 7
        }
        try {
            val am = context?.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
            am?.setStreamVolume(AudioManager.STREAM_RING, vloume, AudioManager.FLAG_PLAY_SOUND)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * 打開鍵盤
     */
    fun openKeyboard(editText: EditText?) {
        try {
            val imm = editText?.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.showSoftInput(editText, InputMethodManager.RESULT_SHOWN)
            imm?.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 關閉鍵盤
     */
    fun closeKeyboard(editText: EditText?) {
        try {
            val imm = editText?.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(editText?.windowToken, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}