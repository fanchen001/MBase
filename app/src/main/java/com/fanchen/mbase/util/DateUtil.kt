package com.fanchen.mbase.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * 日期及格式化相关
 * Created by fanchen on 2018/9/3.
 */
object DateUtil {
    /** 时间日期格式化到年月日时分秒.  */
    val DATEFORMATYMDHMS = "yyyy-MM-dd HH:mm:ss"
    /** 时间日期格式化到年月日.  */
    val DATEFORMATYMD = "yyyy-MM-dd"
    /** 时间日期格式化到年月.  */
    val DATEFORMATYM = "yyyy-MM"
    /** 时间日期格式化到年月日时分.  */
    val DATEFORMATYMDHM = "yyyy-MM-dd HH:mm"
    /** 时间日期格式化到月日.  */
    val DATEFORMATMD = "MM/dd"
    /** 时分秒.  */
    val DATEFORMATHMS = "HH:mm:ss"
    /** 时分.  */
    val DATEFORMATHM = "HH:mm"
    /** 上午.  */
    val AM = "AM"
    /** 下午.  */
    val PM = "PM"

    /**
     * @param datdString Thu May 18 2017 00:00:00 GMT+0800 (中国标准时间)
     * @return 年月日;
     */
    fun parseTime(datdString: String): String {
        val datd = datdString.replace("GMT", "").replace("\\(.*\\)".toRegex(), "")
        //将字符串转化为date类型，格式2016-10-12
        val format = SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss z", Locale.ENGLISH)
        try {
            return SimpleDateFormat(DATEFORMATYMD).format(format.parse(datd)).replace("-", "/")
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return ""
    }

    /**
     * 获取当前时间戳
     * @return 时间戳
     */
    fun getCurrentTime(): Long {
        return Calendar.getInstance().getTimeInMillis()
    }

    /**
     *
     * @return
     */
    fun getTimeByCalendar(): String {
        val cal = Calendar.getInstance()
        val month = cal.get(Calendar.MONTH)//获取月份
        val day = cal.get(Calendar.DATE)//获取日
        return month + 1 + "月" + day + "日"
    }

    fun unitFormat(i: Int): String {
        return if (i in 0..9) "0" + Integer.toString(i) else "" + i
    }

    /**
     * 格式化12小时制<br></br>
     * 格式：yyyy-MM-dd hh-MM-ss
     * @param time 时间
     * @return
     */
    fun format12Time(time: Long): String {
        return format(time, DATEFORMATYMDHMS)
    }

    /**
     * 格式化24小时制<br></br>
     * 格式：yyyy-MM-dd HH-MM-ss
     * @param time 时间
     * @return
     */
    fun format24Time(time: Long): String {
        return format(time, DATEFORMATYMDHMS)
    }

    /**
     * 格式化时间,自定义标签
     * @param time 时间
     * @param pattern 格式化时间用的标签
     * @return
     */
    fun format(time: Long, pattern: String): String {
        return SimpleDateFormat(pattern).format(Date(time))
    }

    /**
     * long转时间格式
     *
     * @param time
     * 时间
     * @return
     */
    fun transitionTime(time: Long): String {
        val temp1 = time / 1000
        val temp2 = temp1 / 60
        val temp3 = temp2 / 60
        val h = (temp3 % 60).toString()
        var m = (temp2 % 60).toString()
        var s = (temp1 % 60).toString()
        if (m.length < 2) {
            m = "0" + m
        }
        if (s.length < 2) {
            s = "0" + s
        }
        return "$h:$m:$s"
    }

    /**
     * 将时间转换为中文
     * @param datetime
     * @return
     */
    fun dateToChineseString(datetime: Date): String {
        val today = Date()
        val seconds = (today.getTime() - datetime.getTime()) / 1000
        val year = seconds / (24 * 60 * 60 * 30 * 12)// 相差年数
        val month = seconds / (24 * 60 * 60 * 30)//相差月数
        val date = seconds / (24 * 60 * 60)     //相差的天数
        val hour = (seconds - date * 24 * 60 * 60) / (60 * 60)//相差的小时数
        val minute = (seconds - date * 24 * 60 * 60 - hour * 60 * 60) / 60//相差的分钟数
        val second = seconds - date * 24 * 60 * 60 - hour * 60 * 60 - minute * 60//相差的秒数
        if (year > 0) {
            return year + "年前"
        }
        if (month > 0) {
            return month + "月前"
        }
        if (date > 0) {
            return date + "天前"
        }
        if (hour > 0) {
            return hour + "小时前"
        }
        if (minute > 0) {
            return minute + "分钟前"
        }
        return if (second > 0) {
            second + "秒前"
        } else "未知时间"
    }


    /**
     * 取指定日期为星期几.
     *
     * @param strDate 指定日期
     * @param inFormat 指定日期格式
     * @return String   星期几
     */
    fun getWeekNumber(strDate: String, inFormat: String): String {
        val calendar = GregorianCalendar()
        try {
            calendar.setTime(SimpleDateFormat(inFormat).parse(strDate))
            val intTemp = calendar.get(Calendar.DAY_OF_WEEK) - 1
            return when (intTemp) {
                1 -> "星期一"
                2 -> "星期二"
                3 -> "星期三"
                4 -> "星期四"
                5 -> "星期五"
                6 -> "星期六"
                else -> "星期日"
            }
        } catch (e: Exception) {
            return "错误"
        }
    }

    fun getWeekNumberFromString(date: String): Int {
        return when (date) {
            "周一" -> 1
            "周二" -> 2
            "周三" -> 3
            "周四" -> 4
            "周五" -> 5
            "周六" -> 6
            else -> 7
        }
    }

    fun getWeekNumberFromInt(week: Int): String {
        return when (week) {
            1 -> "周一"
            2 -> "周二"
            3 -> "周三"
            4 -> "周四"
            5 -> "周五"
            6 -> "周六"
            else -> "周日"
        }
    }

    fun getWeekNumber(): String? {
        val currentDate = getCurrentDate(DATEFORMATYMD)
        val calendar = GregorianCalendar()
        val df = SimpleDateFormat(DATEFORMATYMD)
        try {
            calendar.setTime(df.parse(currentDate))
        } catch (e: Exception) {
            return "周日"
        }
        val intTemp = calendar.get(Calendar.DAY_OF_WEEK) - 1
        var week: String? = null
        when (intTemp) {
            0 -> week = "周日"
            1 -> week = "周一"
            2 -> week = "周二"
            3 -> week = "周三"
            4 -> week = "周四"
            5 -> week = "周五"
            6 -> week = "周六"
        }
        return week
    }

    /**
     * 描述：获取本周的某一天.
     *
     * @param format the format
     * @param calendarField the calendar field
     * @return String String类型日期时间
     */
    private fun getDayOfWeek(format: String, calendarField: Int): String {
        try {
            val c = GregorianCalendar()
            val mSimpleDateFormat = SimpleDateFormat(format)
            val week = c.get(Calendar.DAY_OF_WEEK)
            if (week == calendarField) {
                return mSimpleDateFormat.format(c.getTime())
            } else {
                var offectDay = calendarField - week
                if (calendarField == Calendar.SUNDAY) {
                    offectDay = 7 - Math.abs(offectDay)
                }
                c.add(Calendar.DATE, offectDay)
               return mSimpleDateFormat.format(c.getTime())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    /**
     * 描述：获取本月第一天,最后一天
     * -1 代表最后一天，1代表第一天
     * @param format the format
     * @return String String类型日期时间
     */
    fun getDayOfMonth(format: String,off : Boolean = true): String {
        try {
            val c = GregorianCalendar()
            val mSimpleDateFormat = SimpleDateFormat(format)
            c.set(GregorianCalendar.DAY_OF_MONTH, 1)//当前月的第一天
            if(!off) c.roll(Calendar.DATE, -1)
            return mSimpleDateFormat.format(c.getTime())
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    /**
     * 描述：获取表示当前日期的0点时间毫秒数.
     *
     * @return the first time of day
     */
    fun getFirstTimeOfDay(): Long {
        try {
            val currentDate = getCurrentDate(DATEFORMATYMD)
            return getDateByFormat(currentDate + " 00:00:00", DATEFORMATYMDHMS).getTime()
        } catch (e: Exception) {
        }
        return -1
    }

    /**
     * 描述：获取milliseconds表示的日期时间的字符串.
     *
     * @param milliseconds the milliseconds
     * @param format  格式化字符串，如："yyyy-MM-dd HH:mm:ss"
     * @return String 日期时间字符串
     */
    fun getStringByFormat(milliseconds: Long, format: String = DATEFORMATYMDHMS): String {
        try {
            return SimpleDateFormat(format).format(milliseconds)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    /**
     * 描述：获取表示当前日期时间的字符串.
     *
     * @param format  格式化字符串，如："yyyy-MM-dd HH:mm:ss"
     * @return String String类型的当前日期时间
     */
    fun getCurrentDate(format: String = DATEFORMATYMDHMS): String{
        try {
            val c = GregorianCalendar()
            return SimpleDateFormat(format).format(c.getTime())
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    /**
     * 描述：获取表示当前日期24点时间毫秒数.
     *
     * @return the last time of day
     */
    fun getLastTimeOfDay(): Long {
        try {
            val currentDate = getCurrentDate(DATEFORMATYMD)
            return getDateByFormat(currentDate + " 24:00:00", DATEFORMATYMDHMS).getTime()
        } catch (e: Exception) {
        }
        return -1
    }

    /**
     * 描述：判断是否是闰年()
     *
     * (year能被4整除 并且 不能被100整除) 或者 year能被400整除,则该年为闰年.
     *
     * @param year 年代（如2012）
     * @return boolean 是否为闰年
     */
    fun isLeapYear(year: Int): Boolean {
        return if (year % 4 == 0 && year % 400 != 0 || year % 400 == 0) true else false
    }

    /**
     * 描述：计算两个日期所差的天数.
     *
     * @param milliseconds1 the milliseconds1
     * @param milliseconds2 the milliseconds2
     * @return int 所差的天数
     */
    fun getOffectDay(milliseconds1: Long, milliseconds2: Long): Int {
        val calendar1 = Calendar.getInstance()
        calendar1.setTimeInMillis(milliseconds1)
        val calendar2 = Calendar.getInstance()
        calendar2.setTimeInMillis(milliseconds2)
        //先判断是否同年
        val y1 = calendar1.get(Calendar.YEAR)
        val y2 = calendar2.get(Calendar.YEAR)
        val d1 = calendar1.get(Calendar.DAY_OF_YEAR)
        val d2 = calendar2.get(Calendar.DAY_OF_YEAR)
        var maxDays = 0
        var day = 0
        if (y1 - y2 > 0) {
            maxDays = calendar2.getActualMaximum(Calendar.DAY_OF_YEAR)
            day = d1 - d2 + maxDays
        } else if (y1 - y2 < 0) {
            maxDays = calendar1.getActualMaximum(Calendar.DAY_OF_YEAR)
            day = d1 - d2 - maxDays
        } else {
            day = d1 - d2
        }
        return day
    }

    /**
     * 描述：获取本周一.
     *
     * @param format the format
     * @return String String类型日期时间
     */
    fun getFirstDayOfWeek(format: String): String? {
        return getDayOfWeek(format, Calendar.MONDAY)
    }

    /**
     * 描述：获取本周日.
     *
     * @param format the format
     * @return String String类型日期时间
     */
    fun getLastDayOfWeek(format: String): String? {
        return getDayOfWeek(format, Calendar.SUNDAY)
    }

    /**
     * 描述：String类型的日期时间转化为Date类型.
     *
     * @param strDate String形式的日期时间
     * @param format 格式化字符串，如："yyyy-MM-dd HH:mm:ss"
     * @return Date Date类型日期时间
     */
    fun getDateByFormat(strDate: String, format: String): Date {
        try {
            return SimpleDateFormat(format).parse(strDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return Date()
    }

    /**
     * 描述：Date类型转化为String类型.
     *
     * @param date the date
     * @param format the format
     * @return String String类型日期时间
     */
    fun getStringByFormat(date: Date, format: String): String {
        try {
            return SimpleDateFormat(format).format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    operator fun Long.plus(other: String): String {
        return this.toString() + other
    }

    operator fun Int.plus(other: String): String {
        return this.toString() + other
    }
}