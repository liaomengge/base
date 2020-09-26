package cn.ly.base_common.utils.date;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import lombok.experimental.UtilityClass;

/**
 * Created by liaomengge on 17/11/8.
 */
@UtilityClass
public class LyDateUtil {

    public final long INTERVAL_UNIT = 1000L;

    public final long MILLISECONDS_SECOND = 1000L;
    public final long MILLISECONDS_MINUTE = 60000L;
    public final long MILLISECONDS_HOUR = 3600000L;
    public final long MILLISECONDS_DAY = 86400000L;

    public final String yyyy_MM_dd_HH_mm_ss_SSS = "yyyy-MM-dd HH:mm:ss.SSS";
    public final String yyyy_MM_dd_HH_mm_ss = "yyyy-MM-dd HH:mm:ss";
    public final String yyyy_MM_dd = "yyyy-MM-dd";
    public final String yyyyMMdd = "yyyyMMdd";
    public final String yyyy_MM = "yyyy-MM";
    public final String yyyyMM = "yyyyMM";
    public final String yyyy = "yyyy";
    public final String HHmmssSSS = "HHmmssSSS";
    public final String HHmmss = "HHmmss";
    public final String HH_mm_ss = "HH:mm:ss";

    /********************************************************
     * Date to String
     *******************************************************/

    /**
     * 转化当前时间为默认格式,默认格式:yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    public String getNowDate2String() {
        return DateFormatUtils.format(new Date(), yyyy_MM_dd_HH_mm_ss);
    }

    /**
     * 转化当前时间为指定格式
     *
     * @param pattern
     * @return
     */
    public String getNowDate2String(String pattern) {
        return DateFormatUtils.format(new Date(), pattern);
    }

    /**
     * 转化指定时间为默认格式,默认格式:yyyy-MM-dd HH:mm:ss
     *
     * @param date
     * @return
     */
    public String getDate2String(Date date) {
        return getDate2String(date, yyyy_MM_dd_HH_mm_ss);
    }

    /**
     * 转化指定时间为指定格式
     * <p>
     * 如果date is null, 则返回""
     *
     * @param date
     * @param pattern
     * @return
     */
    public String getDate2String(Date date, String pattern) {
        if (date == null) {
            return "";
        }
        return DateFormatUtils.format(date, pattern);
    }

    /********************************************************
     * String to Date
     *******************************************************/

    /**
     * 转化指定时间为默认格式,默认格式:yyyy-MM-dd HH:mm:ss
     * <p>
     * 如果解析失败,则返回null
     *
     * @param dateStr
     * @return
     */
    public Date getString2Date(String dateStr) {
        return getString2Date(dateStr, yyyy_MM_dd_HH_mm_ss);
    }

    /**
     * 转化指定时间为指定格式
     * <p>
     * 如果解析失败,则返回null
     *
     * @param dateStr
     * @param pattern
     * @return
     */
    public Date getString2Date(String dateStr, String pattern) {
        try {
            return DateUtils.parseDate(dateStr, pattern);
        } catch (ParseException e) {
            return null;
        }
    }

    /********************************************************
     * Date to Long
     *******************************************************/

    public long getDate2Long(Date date) {
        if (date == null) {
            return 0;
        }
        return date.getTime();
    }

    /********************************************************
     * Long to Date
     *******************************************************/

    public Date getLong2Date(long milliSeconds) {
        return new Date(milliSeconds);
    }

    /********************************************************
     * Long to String
     *******************************************************/

    public String getLong2String(long milliSeconds) {
        return getDate2String(getLong2Date(milliSeconds));
    }

    public String getLong2String(long milliSeconds, String pattern) {
        return getDate2String(getLong2Date(milliSeconds), pattern);
    }

    /********************************************************
     * 获取当前的时间值
     *******************************************************/

    /**
     * 获取当前秒数
     *
     * @return
     */
    public long getSecondTime() {
        return System.currentTimeMillis() / INTERVAL_UNIT;
    }

    /**
     * 获取指定时间的秒数
     *
     * @param date
     * @return
     */
    public long getSecondTime(Date date) {
        if (date == null) {
            return 0;
        }
        return date.getTime() / INTERVAL_UNIT;
    }

    /**
     * 获取当前时间毫秒数
     *
     * @return
     */
    public long getMilliSecondsTime() {
        return System.currentTimeMillis();
    }

    /**
     * 获取指定时间毫秒数
     *
     * @return
     */
    public long getMilliSecondsTime(Date date) {
        if (date == null) {
            return 0;
        }
        return date.getTime();
    }

    /********************************************************
     * 获取开始结束的起始时间 - 天
     *******************************************************/

    /**
     * 获取当前开始时间,默认格式:yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    public String getTodayBegin2String() {
        return DateFormatUtils.format(getTodayBegin(), yyyy_MM_dd_HH_mm_ss);
    }

    /**
     * 获取当前结束时间,默认格式:yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    public String getTodayEnd2String() {
        return DateFormatUtils.format(getTodayEnd(), yyyy_MM_dd_HH_mm_ss);
    }

    /**
     * 获取当前开始时间
     *
     * @return
     */
    public Date getTodayBegin() {
        return getBegin4Date(new Date());
    }

    /**
     * 获取当前结束时间
     *
     * @return
     */
    public Date getTodayEnd() {
        return getEnd4Date(new Date());
    }

    /**
     * 获取指定时间的开始时间
     *
     * @param date
     * @return
     */
    public String getBegin4Date2String(Date date) {
        return DateFormatUtils.format(getBegin4Date(date), yyyy_MM_dd_HH_mm_ss);
    }

    /**
     * 获取指定时间的结束时间
     *
     * @param date
     * @return
     */
    public String getEnd4Date2String(Date date) {
        return DateFormatUtils.format(getEnd4Date(date), yyyy_MM_dd_HH_mm_ss);
    }

    /**
     * 获取指定时间的开始时间
     *
     * @param date
     * @return
     */
    public Date getBegin4Date(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                0, 0, 0);
        return calendar.getTime();
    }

    /**
     * 获取指定时间的结束时间
     *
     * @param date
     * @return
     */
    public Date getEnd4Date(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                23, 59, 59);
        return calendar.getTime();
    }

    /********************************************************
     * 获取开始结束的起始时间 - 月
     *******************************************************/

    /**
     * 获取当前月初 - yyyy-MM-dd
     *
     * @return
     */
    public String getMonthBegin2String() {
        return DateFormatUtils.format(getMonthBegin(), yyyy_MM_dd);
    }

    /**
     * 获取当前月末 - yyyy-MM-dd
     *
     * @return
     */
    public String getMonthEnd2String() {
        return DateFormatUtils.format(getMonthEnd(), yyyy_MM_dd);
    }

    /**
     * 获取当前月初 - yyyy-MM-dd
     *
     * @return
     */
    public Date getMonthBegin() {
        return getBegin4Month(new Date());
    }

    /**
     * 获取当前月末 - yyyy-MM-dd
     *
     * @return
     */
    public Date getMonthEnd() {
        return getEnd4Month(new Date());
    }

    /**
     * 获取指定日期月初 - yyyy-MM-dd
     *
     * @param date
     * @return
     */
    public String getBegin4Month2String(Date date) {
        return DateFormatUtils.format(getBegin4Month(date), yyyy_MM_dd);
    }

    /**
     * 获取指定日期月末 - yyyy-MM-dd
     *
     * @param date
     * @return
     */
    public String getEnd4Month2String(Date date) {
        return DateFormatUtils.format(getEnd4Month(date), yyyy_MM_dd);
    }

    /**
     * 获取当前月 - yyyy-MM
     *
     * @return
     */
    public String getTodayMonth2String() {
        return getMonth2String(new Date());
    }

    /**
     * 获取指定月 - yyyy-MM
     *
     * @param date
     * @return
     */
    public String getMonth2String(Date date) {
        return DateFormatUtils.format(date, yyyy_MM);
    }

    /**
     * 获取指定日期月初 - yyyy-MM-dd
     *
     * @param date
     * @return
     */
    public Date getBegin4Month(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    /**
     * 获取指定日期月末 - yyyy-MM-dd
     *
     * @param date
     * @return
     */
    public Date getEnd4Month(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 0);
        return calendar.getTime();
    }

    /********************************************************
     * 获取开始结束的起始时间 - 年
     *******************************************************/

    /**
     * 获取当前年初 - yyyy-MM
     *
     * @return
     */
    public String getYearBegin2String() {
        return DateFormatUtils.format(getYearBegin(), yyyy_MM);
    }

    /**
     * 获取当前年末 - yyyy-MM
     *
     * @return
     */
    public String getYearEnd2String() {
        return DateFormatUtils.format(getYearEnd(), yyyy_MM);
    }

    /**
     * 获取当前年初 - yyyy-MM
     *
     * @return
     */
    public Date getYearBegin() {
        return getBegin4Year(new Date());
    }

    /**
     * 获取当前年末 - yyyy-MM
     *
     * @return
     */
    public Date getYearEnd() {
        return getEnd4Year(new Date());
    }

    /**
     * 获取指定时间年初 - yyyy-MM
     *
     * @return
     */
    public String getBegin4Year2String(Date date) {
        return DateFormatUtils.format(getBegin4Year(date), yyyy_MM);
    }

    /**
     * 获取指定时间年末 - yyyy-MM
     *
     * @return
     */
    public String getEnd4Year2String(Date date) {
        return DateFormatUtils.format(getEnd4Year(date), yyyy_MM);
    }

    /**
     * 获取当前年 - yyyy
     *
     * @return
     */
    public String getTodayYear2String() {
        return getYear2String(new Date());
    }

    /**
     * 获取指定年 - yyyy
     *
     * @param date
     * @return
     */
    public String getYear2String(Date date) {
        return DateFormatUtils.format(date, yyyy);
    }

    /**
     * 获取指定时间年初 - yyyy-MM
     *
     * @return
     */
    public Date getBegin4Year(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DATE, 1);
        return calendar.getTime();
    }

    /**
     * 获取指定时间年末 - yyyy-MM
     *
     * @return
     */
    public Date getEnd4Year(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MONTH, Calendar.DECEMBER);
        calendar.set(Calendar.DATE, 31);
        return calendar.getTime();
    }

    /********************************************************
     * 时间差
     *******************************************************/

    /**
     * 时间差,单位:天
     *
     * @param fromDate
     * @param toDate
     * @return
     */
    public int diffDay(Date fromDate, Date toDate) {
        return (int) ((toDate.getTime() - fromDate.getTime()) / (1000 * 60 * 60 * 24));
    }

    /**
     * 时间差,单位:小时
     *
     * @param fromDate
     * @param toDate
     * @return
     */
    public int diffHour(Date fromDate, Date toDate) {
        return (int) ((toDate.getTime() - fromDate.getTime()) / (1000 * 60 * 60));
    }

    /**
     * 时间差,单位:分钟
     *
     * @param fromDate
     * @param toDate
     * @return
     */
    public int diffMin(Date fromDate, Date toDate) {
        return (int) ((toDate.getTime() - fromDate.getTime()) / (1000 * 60));
    }

    /**
     * 时间差,单位:秒
     *
     * @param fromDate
     * @param toDate
     * @return
     */
    public int diffSecond(Date fromDate, Date toDate) {
        return (int) ((toDate.getTime() - fromDate.getTime()) / 1000);
    }

    /********************************************************
     * 其他使用
     *******************************************************/

    /**
     * 返回今天是星期几(按西方习惯, 星期天返回0)
     *
     * @return
     */
    public int getDayOfWeek() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        calendar.setTime(new Date());
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        return day - 1;
    }

    /**
     * 按中国习惯, 返回今天是星期几(星期天返回7)
     *
     * @return
     */
    public int getDayOfWeekCN() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        calendar.setTime(new Date());
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        //外国, 星期天为每个星期的第1天
        if (day == 1) {
            return 7;
        }
        return day - 1;
    }

    /**
     * 得到指定日期是几号
     *
     * @param date
     * @return
     */
    public int getDayOfMonth(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 得到今天是几号
     *
     * @return
     */
    public int getDayOfMonth() {
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 得到指定日期的月份
     *
     * @param date
     * @return
     */
    public int getMonth(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.MONTH) + 1;
    }

    /**
     * 得到指定日期的小时
     *
     * @param date
     * @return
     */
    public int getHour(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 得到指定日期的分钟
     *
     * @param date
     * @return
     */
    public int getMinute(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.MINUTE);
    }

    /**
     * 获取指定时间的月份
     *
     * @param date
     * @return
     */
    public String getStringMonth(Date date) {
        return DateFormatUtils.format(date, "MM");
    }

    /**
     * 获取指定时间的天
     *
     * @param date
     * @return
     */
    public String getStringDate(Date date) {
        return DateFormatUtils.format(date, "dd");
    }

    /**
     * 获取指定时间的小时
     *
     * @param date
     * @return
     */
    public String getStringHour(Date date) {
        return DateFormatUtils.format(date, "HH");
    }

    /**
     * 获取指定时间的分钟
     *
     * @param date
     * @return
     */
    public String getStringMinute(Date date) {
        return DateFormatUtils.format(date, "mm");
    }

    /********************************************************
     * 扩展
     *******************************************************/

    public Date parse(String dateStr) {
        return parse(dateStr, yyyy_MM_dd_HH_mm_ss);
    }

    public Date parse(String dateStr, String pattern) {
        try {
            return DateUtils.parseDate(dateStr, pattern);
        } catch (ParseException e) {
            return null;
        }
    }

    public String formatDateTime(Date date) {
        return format(date, yyyy_MM_dd_HH_mm_ss);
    }

    public String formatDate(Date date) {
        return format(date, yyyy_MM_dd);
    }

    public String formatTime(Date date) {
        return format(date, HH_mm_ss);
    }

    public String format(Date date, String pattern) {
        if (date == null) {
            return "";
        }
        return DateFormatUtils.format(date, pattern);
    }

    /**
     * 判断日期是否在范围内, 包含相等的日期
     */
    public boolean isBetween(Date date, Date start, Date end) {
        if (date == null || start == null || end == null || start.after(end)) {
            return false;
        }
        return !date.before(start) && !date.after(end);
    }
}
