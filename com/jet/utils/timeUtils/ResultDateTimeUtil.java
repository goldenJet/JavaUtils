package com.jet.utils.timeUtils;


import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Created by Jet.chen on 2017/7/10.
 * 获取工作日相关的工具类（支持法定节假日）
 */
public class ResultDateTimeUtil {


    private static JSONObject JSONOBJECT; // 读取的holiday文件
    private static List<Date> HOLIDAY_LIST; // 节假日
    private static String HOLIDAY_STRING; // 节假日
    private static List<Date> WORKINGDAY_LIST; // 调休日
    private static String WORKINGDAY_STRING; // 调休日
    static {
        try {
            // 初始化节假日呼入调休日
            InputStream is = ResultDateTimeUtil.class.getClassLoader().getResourceAsStream("static/Holiday/HolidayJson");
            String jsonString = IOUtils.toString(is);
            JSONOBJECT = new JSONObject(jsonString);
            //System.out.println("静态代码块被执行");
            initHoliday();
            initWorkingDay();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 不忽略法定节假日计算某日期指定天数之后的日期
     * @param startDate
     * @param days （days可以为负数，即往前几天）
     * @return
     */
    public static long getDateAfterDaysNotSkipHolidays(Long startDate, int days){
        return getDateAfterDays(startDate, days, false);
    }
    /**
     * 忽略法定节假日计算某日期指定天数之后的日期
     * @param startDate
     * @param days （days可以为负数，即往前几天）
     * @return
     */
    public static long getDateAfterDaysSkipHolidays(Long startDate, int days){
        return getDateAfterDays(startDate, days, true);
    }

    /**
     * 根据起始日期，获取指定天数之后的日期（可选跳过节假日）
     * @param startDate
     * @param days （days可以为负数，即往前几天）
     * @param skipHolidays 是否跳过法定节假日
     * @return
     */
    private static long getDateAfterDays(Long startDate, int days, boolean skipHolidays){
        if (StringUtils.isEmpty(startDate)) return 0;
        // 创建一个日历
        Calendar cc = Calendar.getInstance();
        cc.setTime(new Date(startDate));

        // TODO 开始日期如果是休息日，考虑是否要进行修正

        return recursionGetDateAfterDays(cc, days, skipHolidays);
    }

    /**
     * 递归 获取指定天数后的日期
     * @param calendar 指定开始日期
     * @param days 往后的天数
     * @return
     */
    private static long recursionGetDateAfterDays(Calendar calendar, int days, boolean skipHolidays){
        if (days == 0){
            // 递归的结束条件
            return calendar.getTimeInMillis();
        } else {
            if (isHoliday(calendar.getTimeInMillis(), skipHolidays)){
                // 该天是节假日，days 不变
                if (days > 0){
                    // 往后跳
                    calendar.add(Calendar.DATE, 1);
                } else {
                    // 往前跳
                    calendar.add(Calendar.DATE, -1);
                }
                return recursionGetDateAfterDays(calendar, days, skipHolidays);
            } else {
                // 改天不是节假日，恭喜，days终于可以少一天了
                if (days > 0){
                    // 往后跳
                    calendar.add(Calendar.DATE, 1);
                    days = days - 1;
                } else {
                    // 往前跳
                    calendar.add(Calendar.DATE, -1);
                    days = days + 1;
                }
                return recursionGetDateAfterDays(calendar, days, skipHolidays);
            }
        }
    }

    /**
     * 判断该天是否是节假日
     * @param date
     * @param skipHolidays true则只判定双休日，忽略法定节假日
     * @return true代表是节假日
     */
    public static boolean isHoliday(Long date, boolean skipHolidays) {
        boolean isHoliday = false;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date parseDate = new Date(date);
        String formatDate = sdf.format(parseDate);
        Calendar cc = Calendar.getInstance();
        cc.setTime(new Date(date));

        int i = cc.get(Calendar.DAY_OF_WEEK);
        if (skipHolidays){
            // 忽略法定假日的话直接判断是否是双休日即可
            if (Calendar.SATURDAY == i || Calendar.SUNDAY == i) isHoliday = true;
        } else {
            if (Calendar.SATURDAY == i || Calendar.SUNDAY == i){
                // 如果是双休日的话还要判断是都是法定调休日
                if (!WORKINGDAY_STRING.contains(formatDate)) isHoliday = true;
            } else {
                // 不是双休日则只要判断是否是法定节假日即可
                if (HOLIDAY_STRING.contains(formatDate)) isHoliday = true;
            }
        }

        return isHoliday;
    }

    /**
     * 手动维护2017年假期
     */
    private static void initHoliday(){
        List<Date> holidayList = new ArrayList<>();
        List<String> holidayStringList = new ArrayList<>();
        try {
            JSONArray holidays = JSONOBJECT.getJSONArray("holiday");
            Iterator<Object> iterator = holidays.iterator();
            while (iterator.hasNext()){
                String holidayString = iterator.next().toString();
                holidayStringList.add(holidayString);
                holidayList.add(new SimpleDateFormat("yyyy-MM-dd").parse(holidayString));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        HOLIDAY_LIST = holidayList;
        HOLIDAY_STRING = holidayStringList.toString();
    }
    /**
     * 手动维护2017年调休日
     */
    private static void initWorkingDay(){
        List<Date> workingDayList = new ArrayList<>();
        List<String> workingDayStringList = new ArrayList<>();
        try {
            JSONArray workings = JSONOBJECT.getJSONArray("working");
            Iterator<Object> iterator = workings.iterator();
            while (iterator.hasNext()){
                String workingDayString = iterator.next().toString();
                workingDayStringList.add(workingDayString);
                workingDayList.add(new SimpleDateFormat("yyyy-MM-dd").parse(workingDayString));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        WORKINGDAY_LIST = workingDayList;
        WORKINGDAY_STRING = workingDayStringList.toString();
    }

}