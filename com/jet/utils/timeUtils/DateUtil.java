package com.jet.utils.timeUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
  
    // 获得当天0点时间  
    public static Date getTimesmorning() {  
        Calendar cal = Calendar.getInstance();  
        cal.set(Calendar.HOUR_OF_DAY, 0);  
        cal.set(Calendar.SECOND, 0);  
        cal.set(Calendar.MINUTE, 0);  
        cal.set(Calendar.MILLISECOND, 0);  
        return cal.getTime();   
    }  
  
    // 获得当天24点时间  
    public static Date getTimesnight() {  
        Calendar cal = Calendar.getInstance();  
        cal.set(Calendar.HOUR_OF_DAY, 24);  
        cal.set(Calendar.SECOND, 0);  
        cal.set(Calendar.MINUTE, 0);  
        cal.set(Calendar.MILLISECOND, 0);  
        return cal.getTime();  
    }  
  
    // 获得本周一0点时间  
    public static Date getTimesWeekmorning() {  
        Calendar cal = Calendar.getInstance();  
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);  
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);  
        return cal.getTime();  
    }  
  
    // 获得本周日24点时间  
    public static Date getTimesWeeknight() {  
        Calendar cal = Calendar.getInstance();  
        cal.setTime(getTimesWeekmorning());  
        cal.add(Calendar.DAY_OF_WEEK, 7);  
        return cal.getTime();  
    }  
  
    // 获得本月第一天0点时间  
    public static Date getTimesMonthmorning() {  
        Calendar cal = Calendar.getInstance();  
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);  
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));  
        return cal.getTime();  
    }  
  
    // 获得本月最后一天24点时间  
    public static Date getTimesMonthnight() {  
        Calendar cal = Calendar.getInstance();  
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);  
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));  
        cal.set(Calendar.HOUR_OF_DAY, 24);  
        return cal.getTime();  
    }
    
    public static int getCurrentYear() {  
    	Calendar c = Calendar.getInstance();  
    	return c.get(Calendar.YEAR);  
    }
    
    public static Date getCurrentQuarterStartTime() {  
        Calendar c = Calendar.getInstance();  
        int currentMonth = c.get(Calendar.MONTH) + 1;  
        SimpleDateFormat longSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
        SimpleDateFormat shortSdf = new SimpleDateFormat("yyyy-MM-dd");  
        Date now = null;  
        try {  
            if (currentMonth >= 1 && currentMonth <= 3)  
                c.set(Calendar.MONTH, 0);  
            else if (currentMonth >= 4 && currentMonth <= 6)  
                c.set(Calendar.MONTH, 3);  
            else if (currentMonth >= 7 && currentMonth <= 9)  
                c.set(Calendar.MONTH, 4);  
            else if (currentMonth >= 10 && currentMonth <= 12)  
                c.set(Calendar.MONTH, 9);  
            c.set(Calendar.DATE, 1);  
            now = longSdf.parse(shortSdf.format(c.getTime()) + " 00:00:00");  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return now;  
    }  
  
    /** 
     * 当前季度的结束时间，即2012-03-31 23:59:59 
     * 
     * @return 
     */  
    public static Date getCurrentQuarterEndTime() {  
        Calendar cal = Calendar.getInstance();  
        cal.setTime(getCurrentQuarterStartTime());  
        cal.add(Calendar.MONTH, 3);  
        return cal.getTime();  
    }  
  
  
    public static Date getCurrentYearStartTime() {  
        Calendar cal = Calendar.getInstance();  
        cal.set(cal.get(Calendar.YEAR), 0, 1, 0, 0, 0);  
        return cal.getTime();  
    }  
  
    public static Date getCurrentYearEndTime() {  
        Calendar cal = Calendar.getInstance();  
        cal.setTime(getCurrentYearStartTime());  
        cal.add(Calendar.YEAR, 1);  
        return cal.getTime();  
    }
    
    public static Date getYearStartTime(int year) {  
        Calendar cal = Calendar.getInstance();  
        cal.set(year, 0, 1, 0, 0, 0);  
        return cal.getTime();  
    }
    
    public static Date getYearEndTime(int year) {  
        Calendar cal = Calendar.getInstance();  
        cal.set(year+1, 0, 1, 0, 0, 0);  
        return cal.getTime();  
    }
    
    public static Date addOneDay(Date date) {  
        Calendar cal = Calendar.getInstance();  
        cal.setTime(date);  
        cal.add(Calendar.DATE, 1);  
        return cal.getTime();  
    }
    

    public static boolean inCurrentWeek(Date date) {
    	return date.before(getTimesWeeknight()) && date.after(getTimesWeekmorning());
    }
    
    public static boolean inCurrentMonth(Date date) {
    	return date.before(getTimesMonthnight()) && date.after(getTimesMonthmorning());
    }
    
    public static boolean inCurrentQuarter(Date date) {
    	return date.before(getCurrentQuarterEndTime()) && date.after(getCurrentQuarterStartTime());
    }
    
    public static boolean inCurrentYear(Date date) {
    	return date.before(getCurrentYearEndTime()) && date.after(getCurrentYearStartTime());
    }
    
    public static boolean inRangeTime(Date date,Date startDate,Date endDate) {
    	return date.before(endDate) && date.after(startDate);
    }
    
    public static Date getProjectRangeFrom(Date date) {
    	Date startDate = getCurrentYearStartTime();
    	Date startWeekDate = getTimesWeekmorning();
    	if (startDate.after(startWeekDate)) {
    		startDate = startWeekDate;
		}
    	if (date!=null && startDate.after(date)) {
    		startDate = date;
		}
    	return startDate;
    }
    
    public static Date getProjectRangeTo(Date date) {
    	Date endDate = getCurrentYearEndTime();
    	Date endWeekDate = getTimesWeeknight();
    	if (endDate.before(endWeekDate)) {
    		endDate = endWeekDate;
		}
    	if (date!=null && endDate.before(date)) {
    		endDate = date;
		}
    	return endDate;
    }
  
    public static int getCurrentMonth(Date date) {
    	 Calendar cal = Calendar.getInstance(); 
    	 cal.setTime(date);
    	 return cal.get(Calendar.MONTH) + 1;
    }
    
    public static int getCurrentMonth() {
   	 Calendar cal = Calendar.getInstance(); 
   	 return cal.get(Calendar.MONTH) + 1;
    }
    
    public static int getCurrentQuarter() {
    	int currentMonth = getCurrentMonth();      	 
      	if (currentMonth >= 1 && currentMonth <= 3)  
            return 1;  
        else if (currentMonth >= 4 && currentMonth <= 6)  
            return 2;  
        else if (currentMonth >= 7 && currentMonth <= 9)  
        	return 3;  
        else if (currentMonth >= 10 && currentMonth <= 12)  
        	return 4;
		return 0;  
    }
}
