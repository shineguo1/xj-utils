package org.xj.commons.toolkit;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    public static final String DATE_PATTERN = "yyyyMMdd";
    public static final String DATE_FULL_PATTERN = "yyyyMMddHHmmss";
    public static final String DATE_FULL_PATTERN_SSS = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String DATE_TYPE = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_M_PATTERN = "yyyyMMddHHmm";

    public static final String MMddHHmmssSSS = "yyMMddHHmmssSSS";

    public DateUtil() {
    }

    public static Date parse(String date, String pattern) {
        try {
            return (new SimpleDateFormat(pattern)).parse(date);
        } catch (ParseException var3) {
            return null;
        }
    }

    public static String parseToString(Date date, String pattern) {
        return (new SimpleDateFormat(pattern)).format(date);
    }

    public static boolean isYear(String year) {
        if (StringUtils.isBlank(year)) {
            return false;
        } else if (!year.matches("^[0-9]+$")) {
            return false;
        } else {
            return year.length() <= 4;
        }
    }

    public static boolean isMonth(String month) {
        if (StringUtils.isBlank(month)) {
            return false;
        } else if (!month.matches("^[0-9]+$")) {
            return false;
        } else {
            return Integer.parseInt(month) <= 12;
        }
    }

    /**
     * 比较时间
     *
     * @param date1 时间1
     * @param date2 时间2
     * @return date1比data2晚 返回true
     */
    public static boolean compDate(String date1, String date2) {
        try {
            int res = date1.compareTo(date2);
            if (res >= 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 时间戳转换日期方法加8小时
     *
     * @param value
     * @return
     */
    public static String stampToDate(String value) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long lt = new Long(value);
        Date date = new Date(lt);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR, calendar.get(Calendar.HOUR) + 8);
        res = simpleDateFormat.format(calendar.getTime());
        return res;
    }

    public static Long getEpoch(LocalDateTime dateTime) {
        Date from = Date.from(dateTime.toInstant(ZoneOffset.of("+8")));
        return from.getTime();
    }

    public static Long getEpochByNow() {
        return Instant.now().toEpochMilli();
    }

}
