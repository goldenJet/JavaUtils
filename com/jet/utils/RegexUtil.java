package com.jet.utils;

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
        str = str.trim();
        if (StringUtils.isBlank(str)) return false;
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
        str = str.trim();
        if (StringUtils.isBlank(str)) return false;
//        String regExp = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        String regExp = "^[\\w!#$%&'*+/=?^_`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?$";
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
        str = str.trim();
        if (StringUtils.isBlank(str)) return false;
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

    /**
    * @Description:  判断是否是PC端的请求
    * @Param: [userAgent] request.getHeader("User-Agent")
    * @return: boolean
    * @Author: Jet.Chen
    * @Date: 2018/8/13
    */
    public static boolean isPC(String userAgent) throws PatternSyntaxException {
        boolean isPC = true;
        if (StringUtils.isBlank(userAgent)) return true;
        String[] mobileAgents = {"iphone", "android","ipad", "phone", "mobile", "wap", "netfront", "java", "opera mobi",
                "opera mini", "ucweb", "windows ce", "symbian", "series", "webos", "sony", "blackberry", "dopod",
                "nokia", "samsung", "palmsource", "xda", "pieplus", "meizu", "midp", "cldc", "motorola", "foma",
                "docomo", "up.browser", "up.link", "blazer", "helio", "hosin", "huawei", "novarra", "coolpad", "webos",
                "techfaith", "palmsource", "alcatel", "amoi", "ktouch", "nexian", "ericsson", "philips", "sagem",
                "wellcom", "bunjalloo", "maui", "smartphone", "iemobile", "spice", "bird", "zte-", "longcos",
                "pantech", "gionee", "portalmmm", "jig browser", "hiptop", "benq", "haier", "^lct", "320x320",
                "240x320", "176x220", "w3c ", "acs-", "alav", "alca", "amoi", "audi", "avan", "benq", "bird", "blac",
                "blaz", "brew", "cell", "cldc", "cmd-", "dang", "doco", "eric", "hipt", "inno", "ipaq", "java", "jigs",
                "kddi", "keji", "leno", "lg-c", "lg-d", "lg-g", "lge-", "maui", "maxo", "midp", "mits", "mmef", "mobi",
                "mot-", "moto", "mwbp", "nec-", "newt", "noki", "oper", "palm", "pana", "pant", "phil", "play", "port",
                "prox", "qwap", "sage", "sams", "sany", "sch-", "sec-", "send", "seri", "sgh-", "shar", "sie-", "siem",
                "smal", "smar", "sony", "sph-", "symb", "t-mo", "teli", "tim-", "tosh", "tsm-", "upg1", "upsi", "vk-v",
                "voda", "wap-", "wapa", "wapi", "wapp", "wapr", "webc", "winw", "winw", "xda", "xda-",
                "Googlebot-Mobile"};
        if (!userAgent.toLowerCase().contains("ipad")) {
            // 2019-4-4 ipad 视为pc端
            for (String mobileAgent : mobileAgents) {
                if (userAgent.toLowerCase().contains(mobileAgent) && userAgent.toLowerCase().indexOf("windows nt")<= 0 && userAgent.toLowerCase().indexOf("macintosh") <= 0) {
                    isPC = false;
                    break;
                }
            }
        }
        return isPC;
    }


    /**
    * @Description: 相似度比较，
    * @Param: [str, target]
    * @return: float 完全相似=1.0    完全不相似=0.0
    * @Author: Jet.Chen
    * @Date: 2018/11/12
    */
    public static float getSimilarityRatio(String str, String target) {
        if (StringUtils.isBlank(str)) {
            if (StringUtils.isBlank(target)) return 1.0f;
            return 0.0f;
        } else {
            if (StringUtils.isBlank(target)) return 0.0f;
        }
        return 1 - (float) getSimilarityRatioCompare(str, target) / Math.max(str.length(), target.length());
    }

    /**
    * @Description: 矩阵模式来比较相似度
    * @Param: [str, target]
    * @return: int
    * @Author: Jet.Chen
    * @Date: 2018/11/12
    */
    private static int getSimilarityRatioCompare(String str, String target) {
        int d[][];
        int n = str.length();
        int m = target.length();
        int i;
        int j;
        char ch1;
        char ch2;
        int temp;
        d = new int[n + 1][m + 1];
        for (i = 0; i <= n; i++) { // 初始化第一列
            d[i][0] = i;
        }

        for (j = 0; j <= m; j++) { // 初始化第一行
            d[0][j] = j;
        }

        for (i = 1; i <= n; i++) { // 遍历str
            ch1 = str.charAt(i - 1);
            // 去匹配target
            for (j = 1; j <= m; j++) {
                ch2 = target.charAt(j - 1);
                if (ch1 == ch2) {
                    temp = 0;
                } else {
                    temp = 1;
                }

                // 左边+1,上边+1, 左上角+temp取最小
                d[i][j] = min(d[i - 1][j] + 1, d[i][j - 1] + 1, d[i - 1][j - 1] + temp);
            }
        }
        return d[n][m];
    }
}
