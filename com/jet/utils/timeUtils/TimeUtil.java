package com.jet.utils.timeUtils;

import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/11/10.
 */

public class TimeUtil {
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    public static String formateDate(Timestamp date) {
        return DATE_FORMAT.format(date);
    }

    public static String formateDateTime(Timestamp dateTime) {
        return DATETIME_FORMAT.format(dateTime);
    }

    public static Timestamp getStartTime(Timestamp timestamp) {
        Calendar todayStart = Calendar.getInstance();
        todayStart.setTime(timestamp);
        todayStart.set(Calendar.HOUR_OF_DAY, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);
        return new Timestamp(todayStart.getTime().getTime());
    }

    public static Timestamp getEndTime(Timestamp timestamp) {
        Calendar todayEnd = Calendar.getInstance();
        todayEnd.setTime(timestamp);
        todayEnd.set(Calendar.HOUR_OF_DAY, 23);
        todayEnd.set(Calendar.MINUTE, 59);
        todayEnd.set(Calendar.SECOND, 59);
        todayEnd.set(Calendar.MILLISECOND, 999);
        return new Timestamp(todayEnd.getTime().getTime());
    }


    public static Timestamp praseTimeStamp(String date) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        if (!StringUtils.hasText(date)) {
            return timestamp;
        } else {
            String dateMatch = "[0-9]{4}-[0-9]{2}-[0-9]{2}";
            String datetimeMatch = "[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}";
            boolean isDate = Pattern.compile(dateMatch).matcher(date).matches();
            boolean isDatetime = Pattern.compile(datetimeMatch).matcher(date).matches();
            try {
                if (isDate) {
                    timestamp = new Timestamp(DATE_FORMAT.parse(date).getTime());
                } else if (isDatetime) {
                    timestamp = new Timestamp(DATETIME_FORMAT.parse(date).getTime());
                } else {
                    throw new Exception();
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("Unparseable date: " + date);
            }
        }
        return timestamp;
    }


    public static List<TimeRange> genericTimeRangeList(TimeRange timeRange, DateType dateType) {
        List<TimeRange> timeRanges = new ArrayList();
        Timestamp startTime = getStartTime(timeRange.getStart());
        Timestamp endTime = getEndTime(timeRange.getEnd());
        System.out.println("genericTimeRangeList StartTime:" + formateDateTime(startTime) + ";;EndTime:" + formateDateTime(endTime));
        Timestamp endTime2 = endTime;
        Calendar dateStart = Calendar.getInstance();
        dateStart.setTime(startTime);
        int calendarType = Calendar.HOUR;
        int increaseType = Calendar.HOUR;
        switch (dateType) {
            case HOURS:
                increaseType = Calendar.HOUR;
                break;
            case DAY:
                increaseType = Calendar.DAY_OF_YEAR;
                break;
            case WEEK:
                calendarType = Calendar.DAY_OF_WEEK;
                increaseType = Calendar.WEEK_OF_YEAR;
                break;
            case MONTH:
                calendarType = Calendar.DAY_OF_MONTH;
                increaseType = Calendar.MONTH;
                Calendar last = Calendar.getInstance();
                last.setTime(endTime);
                last.set(calendarType, last.getActualMaximum(calendarType));
                endTime = new Timestamp(last.getTime().getTime());
                break;
            case YEAR:
                calendarType = Calendar.DAY_OF_YEAR;
                increaseType = Calendar.YEAR;
                break;
        }
        while (dateStart.getTime().before(endTime)) {
            Timestamp firstTime;
            Timestamp lastTime;
            Timestamp currentDate = null;
            if (dateType == DateType.HOURS || dateType == DateType.DAY) {
                firstTime = new Timestamp(dateStart.getTime().getTime());
                dateStart.add(increaseType, 1);
                lastTime = new Timestamp(dateStart.getTime().getTime() - 1);
            } else {
                Calendar first = Calendar.getInstance();
                first.setTime(dateStart.getTime());
                first.set(calendarType, first.getActualMinimum(calendarType));
                firstTime = new Timestamp(first.getTime().getTime());
                Calendar last = Calendar.getInstance();
                last.setTime(dateStart.getTime());
                last.set(calendarType, last.getActualMaximum(calendarType));
                last.add(Calendar.DAY_OF_YEAR, 1);
                lastTime = new Timestamp(last.getTime().getTime() - 1);
                dateStart.add(increaseType, 1);
            }
            firstTime = firstTime.before(startTime) ? startTime : firstTime;
            lastTime = lastTime.after(endTime2) ? endTime2 : lastTime;
            timeRanges.add(new TimeRange(firstTime, lastTime));
        }
        return timeRanges;
    }

    public static String praseCornExpress(Date date, String... ignores) {
        List<String> formatList = new ArrayList<>();
        String[] formatOrders = new String[]{
                "ss", "mm", "HH", "dd", "MM", "E", "yyyy"
        };
        List<String> ignoreList = Arrays.asList(ignores);
        for (String formatOrder : formatOrders) {
            if (ignoreList.contains(formatOrder)
                    && !formatOrder.equals("ss")
                    && !formatOrder.equals("mm")
                    && !formatOrder.equals("HH")) {
                formatList.add("?");
            } else {
                formatList.add(formatOrder);
            }
        }
        String formatSting = StringUtils.collectionToDelimitedString(formatList, " ");
        SimpleDateFormat cornExpress = new SimpleDateFormat(formatSting);
        return cornExpress.format(date);
    }

    public static String praseCornExpressByNumberOfDays(Date date,int days){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DAY_OF_MONTH, days);
        return  praseCornExpress(c.getTime(),"E");
    }


    //例：05/19/2017 - 06/19/2017转成2017-05-19和2017-06-19
    public static String[] convertDateFormat(String originString) {
        String[] originDate = originString.split(" - ");

        String[] startDateArray = originDate[0].split("/");
        String[] endDateArray = originDate[1].split("/");

        String startDate = startDateArray[2] + "-" + startDateArray[0] + "-" + startDateArray[1];
        String endDate = endDateArray[2] + "-" + endDateArray[0] + "-" + endDateArray[1];
        return new String[]{startDate, endDate};

    }

    // 获取指定月份的第一天和最后一天
    //例：2017-07 转成2017-07-01 00:00:00 和 2017-07-31 23:59:59
    public static String[] convertMonth2Date(String month) {
        String[] originDate = month.split("-");
        Calendar cc;

        // 获取当月第一天和最后一天
        String firstday, lastday;
        // 第一天
        cc = Calendar.getInstance();
        cc.set(Integer.parseInt(originDate[0]), Integer.parseInt(originDate[1])-1, 1);
        cc.set(Calendar.DAY_OF_MONTH, 1);
        firstday = DATE_FORMAT.format(cc.getTime()) + " 00:00:00";
        // 最后一天
        cc = Calendar.getInstance();
        cc.set(Integer.parseInt(originDate[0]), Integer.parseInt(originDate[1])-1, 1);
        cc.add(Calendar.MONTH, 1);
        cc.set(Calendar.DAY_OF_MONTH, 0);
        lastday = DATE_FORMAT.format(cc.getTime()) + " 23:59:59";

        return new String[]{firstday, lastday};

    }


}
