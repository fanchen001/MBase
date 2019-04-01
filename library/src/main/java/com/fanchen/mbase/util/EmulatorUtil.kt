package com.fanchen.mbase.util

import android.annotation.SuppressLint
import android.content.Context
import android.telephony.TelephonyManager
import java.io.File


/**
 * 模拟器检查
 * Created by fanchen on 2018/9/4.
 */
object EmulatorUtil {
    private val known_pipes = arrayOf("/dev/socket/qemud", "/dev/qemu_pipe")

    private val known_qemu_drivers = arrayOf("goldfish")

    private val known_files = arrayOf("/system/lib/libc_malloc_debug_qemu.so", "/sys/qemu_trace", "/system/bin/qemu-props")

    private val known_numbers = arrayOf("15555215554", "15555215556", "15555215558", "15555215560", "15555215562", "15555215564", "15555215566", "15555215568", "15555215570", "15555215572", "15555215574", "15555215576", "15555215578", "15555215580", "15555215582", "15555215584")

    private val known_device_ids = arrayOf("000000000000000")// 默认ID

    private val known_imsi_ids = arrayOf("310260000000000")// 默认的 imsi id

    /**
     * 检测“/dev/socket/qemud”，“/dev/qemu_pipe”这两个通道
     * 读取文件内容，然后检查已知QEmu的驱动程序的列表
     * @param context The context of the application package.
     * @return launcher activity name of this application. From the
     * "android:name" attribute.
     */
    fun checkPipes(): Boolean {
        for (i in known_pipes) {
            if (File(i).exists()) {
                return true
            }
        }
        return false
    }


    /**
     * 检测驱动文件内容
     * 读取文件内容，然后检查已知QEmu的驱动程序的列表
     * @param context The context of the application package.
     * @return launcher activity name of this application. From the
     * "android:name" attribute.
     */
    fun checkQEmuDriverFile(): Boolean {
        val driver_file = File("/proc/tty/drivers")
        if (driver_file.exists() && driver_file.canRead()) {
            val driver_data = String(StreamUtil.file2Byte(driver_file) ?: kotlin.ByteArray(0))
            known_qemu_drivers.forEach {
                if (driver_data.indexOf(it) != -1) return true
            }
        }
        return false
    }


    /**
     * 检测模拟器上特有的几个文件
     *
     * @param context The context of the application package.
     * @return launcher activity name of this application. From the
     * "android:name" attribute.
     */
    fun checkEmulatorFiles(): Boolean {
        for (i in known_files) {
            if (File(i).exists()) {
                return true
            }
        }
        return false
    }


    /**
     * 检测模拟器默认的电话号码
     *
     * @param context The context of the application package.
     * @return launcher activity name of this application. From the
     * "android:name" attribute.
     */
    @SuppressLint("MissingPermission")
    fun checkPhoneNumber(context: Context?): Boolean {
        try {
            val telephonyManager = context?.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
            val phonenumber = telephonyManager?.line1Number
            known_numbers.forEach {
                if (it.equals(phonenumber, ignoreCase = true)) return true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }


    /**
     * 检测imsi id是不是“310260000000000”
     *
     * @param context The context of the application package.
     * @return launcher activity name of this application. From the
     * "android:name" attribute.
     */
    @SuppressLint("MissingPermission")
    fun checkImsiIDS(context: Context?): Boolean {
        try {
            val telephonyManager = context?.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
            val imsi_ids = telephonyManager?.subscriberId
            known_imsi_ids.forEach {
                if (it.equals(imsi_ids, ignoreCase = true)) return true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }


    /**
     * 检测手机上的一些硬件信息
     *
     * @param context The context of the application package.
     * @return launcher activity name of this application. From the
     * "android:name" attribute.
     */
    fun checkEmulatorBuild(context: Context): Boolean {
        val BOARD = android.os.Build.BOARD
        val BOOTLOADER = android.os.Build.BOOTLOADER
        val BRAND = android.os.Build.BRAND
        val DEVICE = android.os.Build.DEVICE
        val HARDWARE = android.os.Build.HARDWARE
        val MODEL = android.os.Build.MODEL
        val PRODUCT = android.os.Build.PRODUCT
        if (BOARD === "unknown" || BOOTLOADER === "unknown" || BRAND === "generic" || DEVICE === "generic" || MODEL === "sdk" || PRODUCT === "sdk" || HARDWARE === "goldfish") {
            return true
        }
        return false
    }
}