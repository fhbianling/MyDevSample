package com.bian.util.core;

import android.support.annotation.IntDef;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.format.DateFormat;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 日期工具类
 * Created by BianLing on 2016/9/5.
 * <p>
 * <p>
 * -------------
 * 愿麦子和麦子长在一起，愿河流和河流流归一出。
 */
public final class TimeUtil {
    /**
     * eg.2016-09-14
     **/
    public final static String YMD = "yyyy-MM-dd";
    /**
     * eg.2016-12-01 00:00:00
     **/
    public final static String YMDHMS = "yyyy-MM-dd HH:mm:ss";
    /**
     * eg.2016-12-01 00:00
     **/
    public final static String YMDHM = "yyyy-MM-dd HH:mm";
    /**
     * eg.2016年12月1日
     */
    public final static String YMD_CHINESE = "yyyy年MM月dd日";
    public final static int DAY = 0x11;
    public final static int MONTH = 0x12;
    public final static int YEAR = 0x13;
    private final static String[] MONTH_IN_HANZI = new String[12];
    private final static long TIME_IN_MILLS_OF_ONE_DAY = 24 * 60 * 60 * 1000;

    static {
        MONTH_IN_HANZI[0] = "一";
        MONTH_IN_HANZI[1] = "二";
        MONTH_IN_HANZI[2] = "三";
        MONTH_IN_HANZI[3] = "四";
        MONTH_IN_HANZI[4] = "五";
        MONTH_IN_HANZI[5] = "六";
        MONTH_IN_HANZI[6] = "七";
        MONTH_IN_HANZI[7] = "八";
        MONTH_IN_HANZI[8] = "九";
        MONTH_IN_HANZI[9] = "十";
        MONTH_IN_HANZI[10] = "十一";
        MONTH_IN_HANZI[11] = "十二";
    }

    private TimeUtil() {
        throw new UnsupportedOperationException();
    }

    /**
     * 得到这个月有多少天
     *
     * @param year  年
     * @param month 月
     * @return 当月天数
     */
    public static int getMonthLastDay(int year, int month) {
        if (month == 0) {
            return 0;
        }
        Calendar a = Calendar.getInstance();
        a.set(Calendar.YEAR, year);
        a.set(Calendar.MONTH, month - 1);
        a.set(Calendar.DATE, 1);// 把日期设置为当月第一天
        a.roll(Calendar.DATE, -1);// 日期回滚一天，也就是最后一天
        return a.get(Calendar.DATE);
    }

    /**
     * 得到当前年份
     *
     * @return 当前年
     */
    public static int getCurrentYear() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.YEAR);
    }

    /**
     * 得到当前月份
     *
     * @return 当前年的第几月
     */
    public static int getCurrentMonth() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.MONTH) + 1;
    }

    /**
     * 获得当天的日期
     *
     * @return 当前月的第几天
     */
    public static String getCurrDay() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.DAY_OF_MONTH) + "";
    }

    /**
     * 返回一个被指定格式格式化后的日期字符串
     *
     * @param format 格式
     * @param time   日期
     * @return 被格式化后的日期字符串
     */
    public static CharSequence formatTime(String format, long time) {
        SimpleDateFormat ss = new SimpleDateFormat(format, Locale.CHINA);
        return ss.format(time);
    }

    /**
     * 返回一个被指定格式格式化后的日期字符串
     *
     * @param format 格式
     * @param time   日期
     * @return 被格式化后的日期字符串
     */
    public static CharSequence formatTime(String format, Calendar time) {
        return DateFormat.format(format, time);
    }

    public static CharSequence formatTime(String format, Date date) {
        return DateFormat.format(format, date);
    }

    /**
     * 返回一个更改了日期格式的日期字符串
     *
     * @param oldFormat  旧的格式
     * @param newFormat  新的格式
     * @param oldDateStr 被更改的字符串
     * @return 更改后的字符串
     * @throws ParseException parseException
     */
    public static CharSequence transform(String oldFormat, String newFormat, String oldDateStr) throws ParseException {
        Calendar calendar = parseDateString(oldDateStr, oldFormat);
        return formatTime(newFormat, calendar);
    }

    /**
     * @return 按照该类默认格式格式化后的日期字符串
     */
    public static CharSequence formatCurrentTimeDefaultFormat() {
        return DateFormat.format(YMD, System.currentTimeMillis());
    }

    /**
     * 返回日期所对应的long型时间
     *
     * @param year  年
     * @param month 月
     * @param day   日
     * @return 日期所对应的long型时间
     */
    public static long calculateTimeInMills(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.set(year, month - 1, day);
        return calendar.getTimeInMillis();
    }

    /**
     * 解析字符串并返回Calendar实例
     *
     * @param dateStr 被解析的字符串
     * @param pattern 该字符串对应的格式
     * @return Calendar实例
     * @throws ParseException parseException
     */
    public static
    @Nullable
    Calendar parseDateString(String dateStr, String pattern) throws ParseException {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.CHINA);
        Date parse = format.parse(dateStr);
        calendar.setTime(parse);
        return calendar;
    }

    /**
     * 获取指定年，月，对应当月天数的连续集合
     *
     * @param year  年
     * @param month 月
     * @return 集合
     */
    public static List getDayData(int year, int month) {
        List<String> dayData = new ArrayList<>();
        for (int i = 1; i <= getMonthLastDay(year, month); i++) {
            dayData.add(i < 10 ? "0" + i : i + "");
        }
        return dayData;
    }

    /**
     * 返回两个Calendar实例的相差时间
     *
     * @param newDay 需要比较的时间 不能为空(null),需要正确的日期格式
     * @param oldDay 被比较的时间  为空(null)则为当前时间
     * @param type   返回值类型{@link CompareType}
     * @return 根据传入的type返回相差的年份，月份或天数
     */
    public static int compare(@NonNull Calendar newDay, @Nullable Calendar oldDay, @CompareType int type) {
        int n = 0;
        if (oldDay == null) {
            oldDay = Calendar.getInstance();
        }
        while (!oldDay.after(newDay)) {
            n++;
            if (type == MONTH) {
                oldDay.add(Calendar.MONTH, 1);
            } else if (type == DAY) {
                oldDay.add(Calendar.DATE, 1);
            }
        }
        n = n - 1;
        if (type == YEAR) {
            n = n / 365;
        }
        return n;
    }

    /**
     * 返回两个Calendar实例的相差时间,{@link #compare(Calendar, Calendar, int)} 的重载方法
     */
    public static int compare(long newDayTime, long oldDayTime, @CompareType int type) {
        Calendar oldDay = Calendar.getInstance();
        oldDay.setTimeInMillis(oldDayTime);
        Calendar newDay = Calendar.getInstance();
        newDay.setTimeInMillis(newDayTime);
        return compare(newDay, oldDay, type);
    }

    /**
     * 返回除开周末的两个日期时间差天数，只返回天数。
     *
     * @param newDayTime 较新的日期
     * @param oldDayTime 较旧的日期
     * @return 相差天数
     */
    public static int compareDateExceptWeekend(long newDayTime, long oldDayTime) {
        if (newDayTime < oldDayTime) {
            throw new UnsupportedOperationException("Parameter newDayTime should larger than oldDayTime.");
        }
        Calendar oldDay = Calendar.getInstance();
        oldDay.setTimeInMillis(oldDayTime);
        Calendar newDay = Calendar.getInstance();
        newDay.setTimeInMillis(newDayTime);
        int n = 0;
        while (!oldDay.after(newDay)) {
            int j = oldDay.get(Calendar.DAY_OF_WEEK);
            if (!(j == Calendar.SATURDAY || j == Calendar.SUNDAY)) {
                n++;
            }
            oldDay.add(Calendar.DATE, 1);
        }
        n = n - 1;
        return n;
    }

    /**
     * 返回一个离当天最近的且还未到达的指定星期时间
     * 例如ordinalOfWeek为3，则期望获得最近的还未到达的周二
     * 当天为2017年4月17(周一)，则应返回2017年4月18(当周二)
     * 当天为2017年4月18(周二)，则应返回2017年4月18(当周二)
     * 当天为2017年4月19(周三)，则应返回2017年4月25(下周二)
     *
     * @param ordinalOfWeek 星期几的序数，从1到7，对应星期天到星期六
     * @return 最近的一个指定序数的星期时间的Calendar实例
     */
    public static Calendar getRecentUnreachedDayOfWeek(@IntRange(from = 1, to = 7) int ordinalOfWeek) {
        Calendar instance = Calendar.getInstance();
        int i = instance.get(Calendar.DAY_OF_WEEK);
        long targetTimeInMills =
                instance.getTimeInMillis() -
                        (i - ordinalOfWeek - (ordinalOfWeek >= i ? 0 : 7)) * TIME_IN_MILLS_OF_ONE_DAY;
        instance.setTimeInMillis(targetTimeInMills);
        return new GregorianCalendar(
                instance.get(Calendar.YEAR),
                instance.get(Calendar.MONTH),
                instance.get(Calendar.DAY_OF_MONTH));
    }

    public static String hourMinute(long timeStamp) {
        long l = (System.currentTimeMillis() / TIME_IN_MILLS_OF_ONE_DAY) * TIME_IN_MILLS_OF_ONE_DAY;

        if (timeStamp > l && timeStamp - l < TIME_IN_MILLS_OF_ONE_DAY) {
            return formatTime("HH:mm", timeStamp).toString();
        } else if (timeStamp < l && l - timeStamp < TIME_IN_MILLS_OF_ONE_DAY) {
            return formatTime("昨天 HH:mm", timeStamp).toString();
        } else if (timeStamp < l && l - timeStamp > TIME_IN_MILLS_OF_ONE_DAY && l - timeStamp < TIME_IN_MILLS_OF_ONE_DAY * 365) {
            return formatTime("MM月dd日", timeStamp).toString();
        } else {
            return formatTime("yyyy年MM月", timeStamp).toString();
        }
    }

    public static boolean isSameDate(Date date1, Date date2) {
        return isSameDate(date1.getTime(), date2.getTime());
    }

    public static boolean isSameDate(long time1, long time2) {
        return time1 / TIME_IN_MILLS_OF_ONE_DAY == time2 / TIME_IN_MILLS_OF_ONE_DAY;
    }

    public static String getMonthInHanzi(@IntRange(from = 1, to = 12) int month) {
        return MONTH_IN_HANZI[month - 1];
    }

    public static boolean firstIsOld(String first, String second, String format) {
        try {
            Calendar firstCalendar = parseDateString(first, format);
            Calendar secondCalendar = parseDateString(second, format);
            if (firstCalendar != null && secondCalendar != null) {
                return firstCalendar.getTimeInMillis() < secondCalendar.getTimeInMillis();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean secondIsOld(String first, String second, String format) {
        try {
            Calendar firstCalendar = parseDateString(first, format);
            Calendar secondCalendar = parseDateString(second, format);
            if (firstCalendar != null && secondCalendar != null) {
                return firstCalendar.getTimeInMillis() > secondCalendar.getTimeInMillis();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获得当天凌晨(00:00)的timeInMills
     */
    public static long timeInMillsOfTodayWeeHours() {
        long current = System.currentTimeMillis();
        return current/(TIME_IN_MILLS_OF_ONE_DAY)*(TIME_IN_MILLS_OF_ONE_DAY) - TimeZone.getDefault().getRawOffset();
    }

    @IntDef({DAY, MONTH, YEAR})
    @Retention(RetentionPolicy.SOURCE)
    public @interface CompareType {
    }

}
