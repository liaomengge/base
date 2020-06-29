package cn.mwee.base_common.utils.date;

import lombok.experimental.UtilityClass;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

/**
 * Created by liaomengge on 2018/6/2.
 */
@UtilityClass
public class MwJdk8DateUtil {

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
        LocalDateTime localDateTime = LocalDateTime.now();
        return localDateTime.format(DateTimeFormatter.ofPattern(yyyy_MM_dd_HH_mm_ss));
    }

    /**
     * 转化当前时间为指定格式
     *
     * @param pattern
     * @return
     */
    public String getNowDate2String(String pattern) {
        LocalDateTime localDateTime = LocalDateTime.now();
        return localDateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 转化指定时间为默认格式,默认格式:yyyy-MM-dd HH:mm:ss
     *
     * @param localDateTime
     * @return
     */
    public String getDate2String(LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.ofPattern(yyyy_MM_dd_HH_mm_ss));
    }

    /**
     * 转化指定时间为指定格式
     * <p>
     * localDateTime is null, 则返回""
     *
     * @param localDateTime
     * @param pattern
     * @return
     */
    public String getDate2String(LocalDateTime localDateTime, String pattern) {
        if (localDateTime == null) return "";
        return localDateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 转化指定时间为指定格式
     * <p>
     * localDate is null, 则返回""
     *
     * @param localDate
     * @param pattern
     * @return
     */
    public String getDate2String(LocalDate localDate, String pattern) {
        if (localDate == null) return "";
        return localDate.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 转化指定时间为默认格式,默认格式:yyyy-MM-dd
     *
     * @param localDate
     * @return
     */
    public String getDate2String(LocalDate localDate) {
        return localDate.format(DateTimeFormatter.ofPattern(yyyy_MM_dd));
    }

    /**
     * 转化指定时间为指定格式
     * <p>
     * localTime is null, 则返回""
     *
     * @param localTime
     * @param pattern
     * @return
     */
    public String getDate2String(LocalTime localTime, String pattern) {
        if (localTime == null) return "";
        return localTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 转化指定时间为默认格式,默认格式:HH:mm:ss
     *
     * @param localTime
     * @return
     */
    public String getDate2String(LocalTime localTime) {
        return localTime.format(DateTimeFormatter.ofPattern(HH_mm_ss));
    }

    /********************************************************
     * String to Date
     *******************************************************/

    /**
     * 转化指定时间为默认格式,默认格式:yyyy-MM-dd HH:mm:ss
     * <p>
     * 如果解析失败,则返回null
     *
     * @param localDateTime
     * @return
     */
    public LocalDateTime getString2Date(String localDateTime) {
        return getString2Date(localDateTime, yyyy_MM_dd_HH_mm_ss);
    }

    /**
     * 转化指定时间为指定格式
     * <p>
     * 如果解析失败,则返回null
     *
     * @param localDateTime
     * @param pattern
     * @return
     */
    public LocalDateTime getString2Date(String localDateTime, String pattern) {
        try {
            return LocalDateTime.parse(localDateTime, DateTimeFormatter.ofPattern(pattern));
        } catch (RuntimeException e) {
            return null;
        }
    }

    /**
     * 转化指定时间为默认格式,默认格式:yyyy-MM-dd
     * <p>
     * 如果解析失败,则返回null
     *
     * @param localDate
     * @return
     */
    public LocalDate getString2LocalDate(String localDate) {
        return getString2LocalDate(localDate, yyyy_MM_dd);
    }

    /**
     * 转化指定时间为指定格式
     * <p>
     * 如果解析失败,则返回null
     *
     * @param localDate
     * @param pattern
     * @return
     */
    public LocalDate getString2LocalDate(String localDate, String pattern) {
        try {
            return LocalDate.parse(localDate, DateTimeFormatter.ofPattern(pattern));
        } catch (RuntimeException e) {
            return null;
        }
    }

    /**
     * 转化指定时间为默认格式,默认格式:HH:mm:ss
     * <p>
     * 如果解析失败,则返回null
     *
     * @param localTime
     * @return
     */
    public LocalTime getString2LocalTime(String localTime) {
        return getString2LocalTime(localTime, HH_mm_ss);
    }

    /**
     * 转化指定时间为指定格式
     * <p>
     * 如果解析失败,则返回null
     *
     * @param localTime
     * @param pattern
     * @return
     */
    public LocalTime getString2LocalTime(String localTime, String pattern) {
        try {
            return LocalTime.parse(localTime, DateTimeFormatter.ofPattern(pattern));
        } catch (RuntimeException e) {
            return null;
        }
    }

    /********************************************************
     * Date to Long
     *******************************************************/

    public long getDate2Long(LocalDateTime localDateTime) {
        if (localDateTime == null) return 0;
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public long getDate2Long(LocalDate localDate) {
        if (localDate == null) return 0;
        return localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /********************************************************
     * Long to Date
     *******************************************************/

    public LocalDateTime getLong2Date(long milliSeconds) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(milliSeconds), ZoneId.systemDefault());
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
        return Clock.systemDefaultZone().instant().getEpochSecond();
    }

    /**
     * 获取指定时间的秒数
     *
     * @param localDateTime
     * @return
     */
    public long getSecondTime(LocalDateTime localDateTime) {
        if (localDateTime == null) return 0;
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
    }

    /**
     * 获取当前时间毫秒数
     *
     * @return
     */
    public long getMilliSecondsTime() {
        return Clock.systemDefaultZone().millis();
    }

    /**
     * 获取指定时间毫秒数
     *
     * @return
     */
    public long getMilliSecondsTime(LocalDateTime localDateTime) {
        if (localDateTime == null) return 0;
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
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
        return getTodayBegin().format(DateTimeFormatter.ofPattern(yyyy_MM_dd_HH_mm_ss));
    }

    /**
     * 获取当前结束时间,默认格式:yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    public String getTodayEnd2String() {
        return getTodayEnd().format(DateTimeFormatter.ofPattern(yyyy_MM_dd_HH_mm_ss));
    }

    /**
     * 获取当前开始时间
     *
     * @return
     */
    public LocalDateTime getTodayBegin() {
        return getBegin4Date(LocalDateTime.now());
    }

    /**
     * 获取当前结束时间
     *
     * @return
     */
    public LocalDateTime getTodayEnd() {
        return getEnd4Date(LocalDateTime.now());
    }

    /**
     * 获取指定时间的开始时间
     *
     * @param localDateTime
     * @return
     */
    public String getBegin4Date2String(LocalDateTime localDateTime) {
        return getBegin4Date(localDateTime).format(DateTimeFormatter.ofPattern(yyyy_MM_dd_HH_mm_ss));
    }

    /**
     * 获取指定时间的结束时间
     *
     * @param localDateTime
     * @return
     */
    public String getEnd4Date2String(LocalDateTime localDateTime) {
        return getEnd4Date(localDateTime).format(DateTimeFormatter.ofPattern(yyyy_MM_dd_HH_mm_ss));
    }

    /**
     * 获取指定时间的开始时间
     *
     * @param localDateTime
     * @return
     */
    public LocalDateTime getBegin4Date(LocalDateTime localDateTime) {
        return LocalDateTime.of(localDateTime.toLocalDate(), LocalTime.MIN);
    }

    /**
     * 获取指定时间的结束时间
     *
     * @param localDateTime
     * @return
     */
    public LocalDateTime getEnd4Date(LocalDateTime localDateTime) {
        return LocalDateTime.of(localDateTime.toLocalDate(), LocalTime.MAX);
    }

    /**
     * 获取指定时间的开始时间
     *
     * @param localDate
     * @return
     */
    public String getBegin4Date2String(LocalDate localDate) {
        return getBegin4Date(localDate).format(DateTimeFormatter.ofPattern(yyyy_MM_dd_HH_mm_ss));
    }

    /**
     * 获取指定时间的结束时间
     *
     * @param localDate
     * @return
     */
    public String getEnd4Date2String(LocalDate localDate) {
        return getEnd4Date(localDate).format(DateTimeFormatter.ofPattern(yyyy_MM_dd_HH_mm_ss));
    }

    /**
     * 获取指定时间的开始时间
     *
     * @param localDate
     * @return
     */
    public LocalDateTime getBegin4Date(LocalDate localDate) {
        return LocalDateTime.of(localDate, LocalTime.MIN);
    }

    /**
     * 获取指定时间的结束时间
     *
     * @param localDate
     * @return
     */
    public LocalDateTime getEnd4Date(LocalDate localDate) {
        return LocalDateTime.of(localDate, LocalTime.MAX);
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
        return getMonthBegin().format(DateTimeFormatter.ofPattern(yyyy_MM_dd));
    }

    /**
     * 获取当前月末 - yyyy-MM-dd
     *
     * @return
     */
    public String getMonthEnd2String() {
        return getMonthEnd().format(DateTimeFormatter.ofPattern(yyyy_MM_dd));
    }

    /**
     * 获取当前月初 - yyyy-MM-dd
     *
     * @return
     */
    public LocalDate getMonthBegin() {
        return getBegin4Month(LocalDate.now());
    }

    /**
     * 获取当前月末 - yyyy-MM-dd
     *
     * @return
     */
    public LocalDate getMonthEnd() {
        return getEnd4Month(LocalDate.now());
    }

    /**
     * 获取指定日期月初 - yyyy-MM-dd
     *
     * @param localDate
     * @return
     */
    public String getBegin4Month2String(LocalDate localDate) {
        return getBegin4Month(localDate).format(DateTimeFormatter.ofPattern(yyyy_MM_dd));
    }

    /**
     * 获取指定日期月末 - yyyy-MM-dd
     *
     * @param localDate
     * @return
     */
    public String getEnd4Month2String(LocalDate localDate) {
        return getEnd4Month(localDate).format(DateTimeFormatter.ofPattern(yyyy_MM_dd));
    }

    /**
     * 获取当前月 - yyyy-MM
     *
     * @return
     */
    public String getTodayMonth2String() {
        return getMonth2String(LocalDate.now());
    }

    /**
     * 获取指定月 - yyyy-MM
     *
     * @param localDate
     * @return
     */
    public String getMonth2String(LocalDate localDate) {
        return localDate.format(DateTimeFormatter.ofPattern(yyyy_MM));
    }

    /**
     * 获取指定日期月初 - yyyy-MM-dd
     *
     * @param localDate
     * @return
     */
    public LocalDate getBegin4Month(LocalDate localDate) {
        return localDate.with(TemporalAdjusters.firstDayOfMonth());
    }

    /**
     * 获取指定日期月末 - yyyy-MM-dd
     *
     * @param localDate
     * @return
     */
    public LocalDate getEnd4Month(LocalDate localDate) {
        return localDate.with(TemporalAdjusters.lastDayOfMonth());
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
        return getYearBegin().format(DateTimeFormatter.ofPattern(yyyy_MM));
    }

    /**
     * 获取当前年末 - yyyy-MM
     *
     * @return
     */
    public String getYearEnd2String() {
        return getYearEnd().format(DateTimeFormatter.ofPattern(yyyy_MM));
    }

    /**
     * 获取当前年 - yyyy
     *
     * @return
     */
    public String getTodayYear2String() {
        return getYear2String(LocalDate.now());
    }

    /**
     * 获取指定年 - yyyy
     *
     * @param localDate
     * @return
     */
    public String getYear2String(LocalDate localDate) {
        return localDate.format(DateTimeFormatter.ofPattern(yyyy));
    }

    /**
     * 获取当前年初 - yyyy-MM
     *
     * @return
     */
    public LocalDate getYearBegin() {
        return getBegin4Year(LocalDate.now());
    }

    /**
     * 获取当前年末 - yyyy-MM
     *
     * @return
     */
    public LocalDate getYearEnd() {
        return getEnd4Year(LocalDate.now());
    }

    /**
     * 获取指定时间年初 - yyyy-MM
     *
     * @return
     */
    public String getBegin4Year2String(LocalDate localDate) {
        return getBegin4Year(localDate).format(DateTimeFormatter.ofPattern(yyyy_MM));
    }

    /**
     * 获取指定时间年末 - yyyy-MM
     *
     * @return
     */
    public String getEnd4Year2String(LocalDate localDate) {
        return getEnd4Year(localDate).format(DateTimeFormatter.ofPattern(yyyy_MM));
    }

    /**
     * 获取指定时间年初 - yyyy-MM
     *
     * @return
     */
    public LocalDate getBegin4Year(LocalDate localDate) {
        return localDate.with(TemporalAdjusters.firstDayOfYear());
    }

    /**
     * 获取指定时间年末 - yyyy-MM
     *
     * @return
     */
    public LocalDate getEnd4Year(LocalDate localDate) {
        return localDate.with(TemporalAdjusters.lastDayOfYear());
    }

    /********************************************************
     * 时间差
     *******************************************************/

    /**
     * 时间差,单位:天
     *
     * @param fromLocalDate
     * @param toLocalDate
     * @return
     */
    public int diffDay(LocalDate fromLocalDate, LocalDate toLocalDate) {
        return (int) Duration.between(fromLocalDate, toLocalDate).toDays();
    }

    /**
     * 时间差,单位:小时
     *
     * @param fromLocalTime
     * @param toLocalTime
     * @return
     */
    public int diffHour(LocalTime fromLocalTime, LocalTime toLocalTime) {
        return (int) Duration.between(fromLocalTime, toLocalTime).toHours();
    }

    /**
     * 时间差,单位:分钟
     *
     * @param fromLocalTime
     * @param toLocalTime
     * @return
     */
    public int diffMin(LocalTime fromLocalTime, LocalTime toLocalTime) {
        return (int) Duration.between(fromLocalTime, toLocalTime).toMinutes();
    }

    /**
     * 时间差,单位:秒
     *
     * @param fromLocalTime
     * @param toLocalTime
     * @return
     */
    public int diffSecond(LocalTime fromLocalTime, LocalTime toLocalTime) {
        return (int) ChronoUnit.SECONDS.between(fromLocalTime, toLocalTime);
    }

    /********************************************************
     * 其他使用
     *******************************************************/

    /**
     * 得到指定星期几(星期天返回7)
     *
     * @return
     */
    public int getDayOfWeekCN(LocalDate localDate) {
        return localDate.getDayOfWeek().getValue();
    }

    /**
     * 按中国习惯, 返回今天是星期几(星期天返回7)
     *
     * @return
     */
    public int getDayOfWeekCN() {
        return getDayOfWeekCN(LocalDate.now());
    }

    /**
     * 得到指定日期是几号
     *
     * @param localDate
     * @return
     */
    public int getDayOfMonth(LocalDate localDate) {
        return localDate.getDayOfMonth();
    }

    /**
     * 得到今天是几月
     *
     * @return
     */
    public int getDayOfMonth() {
        return getDayOfMonth(LocalDate.now());
    }

    /**
     * 得到指定日期的月份
     *
     * @param localDate
     * @return
     */
    public int getMonth(LocalDate localDate) {
        return localDate.getMonth().getValue();
    }

    /**
     * 得到今天是几月
     *
     * @return
     */
    public int getMonth() {
        return getMonth(LocalDate.now());
    }

    /**
     * 得到指定日期的小时
     *
     * @param localTime
     * @return
     */
    public int getHour(LocalTime localTime) {
        return localTime.getHour();
    }

    /**
     * 得到指定日期的分钟
     *
     * @param localTime
     * @return
     */
    public int getMinute(LocalTime localTime) {
        return localTime.getMinute();
    }

    /**
     * 获取指定时间的月份
     *
     * @param localDate
     * @return
     */
    public String getStringMonth(LocalDate localDate) {
        return localDate.format(DateTimeFormatter.ofPattern("MM"));
    }

    /**
     * 获取指定时间的天
     *
     * @param localDate
     * @return
     */
    public String getStringDate(LocalDate localDate) {
        return localDate.format(DateTimeFormatter.ofPattern("dd"));
    }

    /**
     * 获取指定时间的小时
     *
     * @param localTime
     * @return
     */
    public String getStringHour(LocalTime localTime) {
        return localTime.format(DateTimeFormatter.ofPattern("HH"));
    }

    /**
     * 获取指定时间的分钟
     *
     * @param localTime
     * @return
     */
    public String getStringMinute(LocalTime localTime) {
        return localTime.format(DateTimeFormatter.ofPattern("mm"));
    }

    /**
     * 获取指定时间的秒
     *
     * @param localTime
     * @return
     */
    public String getStringSecond(LocalTime localTime) {
        return localTime.format(DateTimeFormatter.ofPattern("ss"));
    }

    /********************************************************
     * 扩展
     *******************************************************/

    /**
     * 判断日期是否在范围内, 包含相等的日期
     */
    public boolean isBetween(LocalDateTime localDateTime, LocalDateTime startLocalDateTime,
                             LocalDateTime endLocalDateTime) {
        if (localDateTime == null || startLocalDateTime == null || endLocalDateTime == null || startLocalDateTime.isAfter(endLocalDateTime))
            return false;
        return !localDateTime.isBefore(startLocalDateTime) && !localDateTime.isAfter(endLocalDateTime);
    }

    /********************************************************
     * 扩展
     *******************************************************/

    /**
     * 将字符串转换为时间
     *
     * @param dateStr 时间字符串
     * @return 时间
     */
    public LocalDateTime parseDateTime(String dateStr) {
        return parseDateTime(dateStr, yyyy_MM_dd_HH_mm_ss);
    }

    /**
     * 将字符串转换为时间
     *
     * @param dateStr 时间字符串
     * @param pattern 表达式
     * @return 时间
     */
    public LocalDateTime parseDateTime(String dateStr, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return parseDateTime(dateStr, formatter);
    }

    /**
     * 将字符串转换为时间
     *
     * @param dateStr   时间字符串
     * @param formatter DateTimeFormatter
     * @return 时间
     */
    public LocalDateTime parseDateTime(String dateStr, DateTimeFormatter formatter) {
        return LocalDateTime.parse(dateStr, formatter);
    }

    /**
     * 将字符串转换为日期
     *
     * @param dateStr 时间字符串
     * @return 时间
     */
    public LocalDate parseDate(String dateStr) {
        return parseDate(dateStr, yyyy_MM_dd);
    }

    /**
     * 将字符串转换为时间
     *
     * @param dateStr 时间字符串
     * @param pattern 表达式
     * @return 时间
     */
    public LocalDate parseDate(String dateStr, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return parseDate(dateStr, formatter);
    }

    /**
     * 将字符串转换为时间
     *
     * @param dateStr   时间字符串
     * @param formatter DateTimeFormatter
     * @return 时间
     */
    public LocalDate parseDate(String dateStr, DateTimeFormatter formatter) {
        return LocalDate.parse(dateStr, formatter);
    }

    /**
     * 将字符串转换为时间
     *
     * @param dateStr 时间字符串
     * @return 时间
     */
    public LocalTime parseTime(String dateStr) {
        return parseTime(dateStr, HH_mm_ss);
    }

    /**
     * 将字符串转换为时间
     *
     * @param dateStr 时间字符串
     * @param pattern 时间正则
     * @return 时间
     */
    public LocalTime parseTime(String dateStr, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return parseTime(dateStr, formatter);
    }

    /**
     * 将字符串转换为时间
     *
     * @param dateStr   时间字符串
     * @param formatter DateTimeFormatter
     * @return 时间
     */
    public LocalTime parseTime(String dateStr, DateTimeFormatter formatter) {
        return LocalTime.parse(dateStr, formatter);
    }

    /**
     * java8 日期时间格式化
     *
     * @param temporal 时间
     * @return 格式化后的时间
     */
    public String formatDateTime(TemporalAccessor temporal) {
        return format(temporal, yyyy_MM_dd_HH_mm_ss);
    }

    /**
     * java8 日期时间格式化
     *
     * @param temporal 时间
     * @return 格式化后的时间
     */
    public String formatDate(TemporalAccessor temporal) {
        return format(temporal, yyyy_MM_dd);
    }

    /**
     * java8 时间格式化
     *
     * @param temporal 时间
     * @return 格式化后的时间
     */
    public String formatTime(TemporalAccessor temporal) {
        return format(temporal, HH_mm_ss);
    }

    /**
     * java8 日期格式化
     *
     * @param temporal 时间
     * @param pattern  表达式
     * @return 格式化后的时间
     */
    public String format(TemporalAccessor temporal, String pattern) {
        return DateTimeFormatter.ofPattern(pattern).format(temporal);
    }

    /********************************************************
     * 转化
     *******************************************************/
    public LocalDateTime date2LocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date2Instant(date), ZoneId.systemDefault());
    }

    public LocalDateTime instant2LocalDateTime(Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    public Date instant2Date(Instant instant) {
        return Date.from(instant);
    }

    public Date localDatseTime2Date(LocalDateTime localDateTime) {
        return instant2Date(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public Instant date2Instant(Date date) {
        return date.toInstant();
    }

    public Instant LocalDateTime2instant(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant();
    }
}
