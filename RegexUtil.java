package com.wailian.util;

import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Created by Jet.chen on 2017/6/8.
 */
@Log4j
public class RegexUtil {

    public static boolean checkIsComplexPassword(String password) {
        boolean isComplex;
        log.info("checkIsComplexPassword:" + password);
        isComplex = password.matches(".*\\d+.*") && password.matches(".*[A-Z]+.*") && password.matches(".*[a-z]+.*") && password.matches(".*[~!@#$%^&*()_+|<>,.?/:;'\\[\\]{}\"]+.*");
        return isComplex;
    }


    /**
     * 大陆手机号码11位数，匹配格式：前三位固定格式+后8位任意数
     * 此方法中前三位格式有：
     * 13+任意数
     * 15+除4的任意数
     * 18+除1和4的任意数
     * 17+除9的任意数
     * 147
     */
    public static boolean isChinaPhoneLegal(String str) throws PatternSyntaxException {
        String regExp = "^((13[0-9])|(15[^4])|(18[0,2,3,5-9])|(17[0-8])|(147))\\d{8}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(str);
        return m.matches();
    }

    /**
     * 固话校验（带区号或者不带区号）
     */
    public static boolean isPhone(String str) throws PatternSyntaxException {
        str = str.trim();
        if (StringUtils.isBlank(str)) return false;
        Pattern p1 = null, p2 = null;
        Matcher m = null;
        boolean b = false;
        p1 = Pattern.compile("^[0][1-9]{2,3}-[0-9]{5,10}$");  // 验证带区号的
        p2 = Pattern.compile("^[1-9]{1}[0-9]{5,8}$");         // 验证没有区号的
        if (str.length() > 9) {
            m = p1.matcher(str);
            b = m.matches();
        } else {
            m = p2.matcher(str);
            b = m.matches();
        }
        return b;
    }

    /**
     * 邮箱校验
     */
    public static boolean isEmailLegal(String str) throws PatternSyntaxException {
        String regExp = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(str);
        return m.matches();
    }

    /**
     * 身份证校验
     * 仅仅校验了长度（18），最后一位，以及生日
     * 没有进行省市区的判断
     */
    public static boolean isChinaIdNum(String str) throws PatternSyntaxException {
        String regExp = "\\d{17}[0-9a-zA-Z]";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(str);
        if (!m.matches()) {
            return false;
        } else {
            int year = 0;
            int month = 0;
            int day = 0;

            // 提取身份证上的前6位以及出生年月日
            Pattern birthDatePattern = Pattern.compile("\\d{6}(\\d{4})(\\d{2})(\\d{2}).*");

            Matcher birthDateMather = birthDatePattern.matcher(str);

            if (birthDateMather.find()) {

                year = Integer.valueOf(birthDateMather.group(1));
                month = Integer.valueOf(birthDateMather.group(2));
                day = Integer.valueOf(birthDateMather.group(3));
            }

            // 年份判断，100年前至今
            Calendar cal = Calendar.getInstance();
            // 当前年份
            int currentYear = cal.get(Calendar.YEAR);
            if (year <= currentYear - 100 || year > currentYear) return false;
            // 月份判断
            if (month < 1 || month > 12) return false;

            // 日期判断
            // 计算月份天数
            int dayCount = 31;
            switch (month) {
                case 1:
                case 3:
                case 5:
                case 7:
                case 8:
                case 10:
                case 12:
                    dayCount = 31;
                    break;
                case 2:
                    // 2月份判断是否为闰年
                    if ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)) {
                        dayCount = 29;
                        break;
                    } else {
                        dayCount = 28;
                        break;
                    }
                case 4:
                case 6:
                case 9:
                case 11:
                    dayCount = 30;
                    break;
            }

            if (day < 1 || day > dayCount) return false;

            return true;
        }
    }
}
