package com.jet.util;

import com.jet.entity.ContractOnlineDictionary;
import com.jet.pojo.PassPortNationEnum;
import com.jet.pojo.SpecialTeamEnum;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Created by miyang on 2018/10/10.
 */
@Log4j
public class CommonUtil {

    //判断是否是护照国家
    public static boolean isPassPortCustomer(String nations){
        if(StringUtils.isNotBlank(nations)){
            if(nations.contains(PassPortNationEnum.DO001.getName())){
                return true;
            }else if(nations.contains(PassPortNationEnum.AB002.getName())){
                return true;
            }else if(nations.contains(PassPortNationEnum.SK003.getName())){
                return true;
            }else if(nations.contains(PassPortNationEnum.CY009.getName())){
                return true;
            }else if(nations.contains(PassPortNationEnum.GL005.getName())){
                return true;
            }else if(nations.contains(PassPortNationEnum.MT006.getName())){
                return true;
            }else if(nations.contains(PassPortNationEnum.SL007.getName())){
                return true;
            }else if(nations.contains(PassPortNationEnum.WT008.getName())){
                return true;
            }else if(nations.contains(PassPortNationEnum.TK010.getName())){
                return true;
            }
        }
        return false;
    }

    /**
    * @Description: 两个 double 相减
    * @Param: [p1, p2]
    * @return: double ； p1 - p2
    * @Author: Jet.Chen
    * @Date: 2018/11/9
    */
    public static double twoDoubleParamSubtract(double p1, double p2){
        return BigDecimal.valueOf(p1).subtract(BigDecimal.valueOf(p2)).doubleValue();
    }

    /**
    * @Description: 两个 double 相加
    * @Param: [p1, p2]
    * @return: double ； p1 + p2
    * @Author: Jet.Chen
    * @Date: 2018/11/9
    */
    public static double twoDoubleParamAdd(double p1, double p2){
        return BigDecimal.valueOf(p1).add(BigDecimal.valueOf(p2)).doubleValue();
    }

    /**
    * @Description: 两个 double 相除
    * @Param: [p1, p2, scale]
    * scale：保留的小数位数
    * @return: double ； p1 / p2
    * @Author: Jet.Chen
    * @Date: 2018/12/11
    */
    public static double div(double p1, double p2, int scale) {
        return BigDecimal.valueOf(p1).divide(BigDecimal.valueOf(p2), scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
    * @Description: 两个 double 相乘
    * @Param: [p1, p2]
    * @return: double ； p1 / p2
    * @Author: Jet.Chen
    * @Date: 2019/10/22
    */
    public static double multiply(double p1, double p2) {
        return BigDecimal.valueOf(p1).multiply(BigDecimal.valueOf(p2)).doubleValue();
    }

    /**
    * @Description: 多个 double 数据相加
    * @Param: [params]
    * @return: double ；p1 + p2 + p3 + ...
    * @Author: Jet.Chen
    * @Date: 2018/11/9
    */
    public static double doubleParamsAdd(List<Double> params){
        BigDecimal bd1 = new BigDecimal("0.0");
        for (double param : params) {
            bd1 = bd1.add(BigDecimal.valueOf(param));
        }
        return bd1.doubleValue();
    }

    public static List<String> generateListString(String str){
        List<String> list = new ArrayList<>();
        list.add(str);
        return list;
    }

    /**
     * @Author: Jet
     * @Description: 计算百分数
     * @Date: 2018/1/26 20:53
     */
    public static String genericPercent(Long num, Long base){
        String result = "-";
        if (num == null || base == null || base == 0) {
            return result;
        }
        if (num == 0) {
            return "0.0%";
        }
        double percent = (Double.valueOf(num) / Double.valueOf(base)) * 100.00;
        DecimalFormat df   = new DecimalFormat("0.0");
        String s = df.format(percent);
        result = s + "%";
        return result;
    }

    public static List<Long> changeArrayToLong(String[] array){
        List<Long> resultList =new ArrayList<>();
        if (array==null || array.length==0){
            return resultList;
        }
        for (String arr : array) {
            if(StringUtils.isNotBlank(arr.trim())){
                resultList.add(Long.parseLong(arr));
            }
        }
        return resultList;
    }

    /**
    * @Description: 文件名切割，因为有些客户端传过来的文件名是包含路径的
    * @Param: [fileName]
    * @return: java.lang.String
    * @Author: Jet.Chen
    * @Date: 2019/3/22 17:12
    */
    public static String fileNameSubstring(String originalFilename){
        if (originalFilename == null) return null;
        String substring = originalFilename.substring(originalFilename.lastIndexOf("/") + 1);
        return substring.substring(substring.lastIndexOf("\\") + 1);
    }

    @Deprecated
    public static String getActivityCheckInMsg(String custName,String title,boolean isInvite){
        StringBuffer stringBuffer= new StringBuffer();
        String activityType = isInvite?"【活动扫码预约】":"【活动扫码签到】";
        stringBuffer.append("外联CRM系统提醒：").append(activityType).append("，客户姓名：【").append(custName)
                .append("】，问卷标题：【").append(title).append("】,具体客户信息请前往CRM系统查看。");
        return stringBuffer.toString();
    }


    /**
     * @Author : jet
     * @Description 替换参数
     * @Date 10:35  2019/5/10
     */
    public static String reParamsWithValue(String content, List<ContractOnlineDictionary> dictionarys,boolean userDefault){
        if(StringUtils.isBlank(content)){
            return content;
        }
        if(dictionarys==null || dictionarys.size()==0){
            return content;
        }
        for (ContractOnlineDictionary dictionary : dictionarys) {
            String temp = dictionary.getParam();
            if(StringUtils.isNotBlank(dictionary.getRealValue())){
                content = content.replace(temp,dictionary.getRealValue());
            }else if(userDefault){
                //使用默认值
                if(StringUtils.isNotBlank(dictionary.getDefaultValue())){
                    content = content.replace(temp,dictionary.getDefaultValue());
                }
            }
        }
        return content;
    }

    /**
    * @Description: 根据事业部名字匹配出事业部编号，默认 “沪”;
     * 上海01、区域02、苏州03、杭州05、北京06、北方07、广州08、青岛09、哈尔滨11、深圳12、总裁办15、成都16、VP2线17、沈阳18、重庆19、无锡20
    * @Param: [DepartmentName]
    * @return: java.lang.String
    * @Author: Jet.Chen
    * @Date: 2019/6/18 16:33
    */
    public static String matchDepartmentNumByDepartmentName(String departmentName){
        if (StringUtils.isBlank(departmentName)) return null;
        if (departmentName.contains("上海")) {
            return "01";
        } else if (departmentName.contains("区域")) {
            return "02";
        } else if (departmentName.contains("苏州")) {
            return "03";
        } else if (departmentName.contains("杭州")) {
            return "05";
        } else if (departmentName.contains("北京")) {
            return "06";
        } else if (departmentName.contains("北方")) {
            return "07";
        } else if (departmentName.contains("广州")) {
            return "08";
        } else if (departmentName.contains("青岛")) {
            return "09";
        } else if (departmentName.contains("哈尔滨")) {
            return "11";
        } else if (departmentName.contains("深圳")) {
            return "12";
        } else if (departmentName.contains("总裁办")) {
            return "15";
        } else if (departmentName.contains("成都")) {
            return "16";
        } else if (departmentName.contains("VP2线")) {
            return "17";
        } else if (departmentName.contains("重庆")) {
            return "19";
        } else if (departmentName.contains("无锡")) {
            return "20";
        }
        return null;
    }

    public static boolean checkIsInToday(Timestamp timestamp){
        if(timestamp==null){
            return false;
        }
        String origin = TimeUtil.formateDate(timestamp);
        Date date = new Date(System.currentTimeMillis());
        String today = TimeUtil.DATE_FORMAT.format(date);
        return today.equals(origin);
    }

    /**
     * 将手机号中间四位替换
     */
    public static String transferMobileEncode(String mobile){
        if (StringUtils.isNotBlank(mobile)) {
           /* if (mobile.length() != 11) {
                return mobile;
            } else {*/
            int length = mobile.length();
            if (length >= 8) {
                return mobile.substring(0, length - 8) + "****" + mobile.substring(length -4, length);
            } else if (length >= 4){
                return "****" + mobile.substring(length -4, length);
            } else {
                return "****" + mobile.substring(length -1, length);
            }
            // }
        } else {
            return mobile;
        }
    }

    /**
     * 将邮箱 @ 之前的三位替换
     */
    public static String transferEmailEncode(String email){
        int indexIfAt = email.indexOf("@");
        if (indexIfAt == -1){
            return email;
        } else if (indexIfAt < 4){
            return "***" + email.substring(indexIfAt, email.length());
        } else {
            return email.substring(0, indexIfAt - 3) + "***" + email.substring(indexIfAt, email.length());
        }
    }
    /**
     * 将座机的中间四位替换
     */
    public static String transferPhoneEncode(String phone){
        int indexIfAt = phone.indexOf("-");
        if (indexIfAt == -1){
            return phone.substring(0, 2) + "****" + phone.substring(6, phone.length());
        } else if (indexIfAt == 3 || indexIfAt == 4){
            return phone.substring(0, 3) + "****" + phone.substring(7, phone.length());
        } else {
            return "***-**" + phone.substring(indexIfAt + 3, phone.length()) ;
        }
    }
    /**
     * 将微信的中间四位替换
     */
    public static String transferWeChatEncode(String weChat){
        if (weChat.length() < 3){
            return weChat.substring(0, 1) + "**";
        } else if (weChat.length() < 5){
            return weChat.substring(0, 2) + "**";
        } else {
            return weChat.substring(0, 2) + "**" + weChat.substring(4, weChat.length());
        }
    }
    /**
     * 汉语中数字大写
     */
    private static final String[] CN_UPPER_NUMBER = { "零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖" };
    /**
     * 汉语中货币单位大写，这样的设计类似于占位符
     */
    private static final String[] CN_UPPER_MONETRAY_UNIT = { "分", "角", "元", "拾", "佰", "仟", "万", "拾", "佰", "仟", "亿", "拾", "佰", "仟", "兆", "拾", "佰", "仟" };
    /**
     * 特殊字符：整
     */
    private static final String CN_FULL = "整";
    /**
     * 特殊字符：负
     */
    private static final String CN_NEGATIVE = "负";
    /**
     * 金额的精度，默认值为2
     */
    private static final int MONEY_PRECISION = 2;
    /**
     * 特殊字符：零元整
     */
    private static final String CN_ZEOR_FULL = "零元";

    /**
     * 把输入的金额转换为汉语中人民币的大写
     *
     * @param numberOfMoney
     * 输入的金额
     * @return 对应的汉语大写
     */
    public static String number2CNMontrayUnit(BigDecimal numberOfMoney) {
        StringBuffer sb = new StringBuffer();
        // 返回-1：表示该数小于0 0：表示该数等于0 1：表示该数大于0
        int signum = numberOfMoney.signum();
        // 零元的情况
        if (signum == 0) {
            return CN_ZEOR_FULL;
        }
        // 这里会进行金额的四舍五入
        long number = numberOfMoney.movePointRight(MONEY_PRECISION).setScale(0, 4).abs().longValue();
        // 得到小数点后两位值
        long scale = number % 100;
        int numUnit = 0;
        int numIndex = 0;
        boolean getZero = false;
        // 判断最后两位数，一共有四中情况：00 = 0, 01 = 1, 10, 11
        if (!(scale > 0)) {
            numIndex = 2;
            number = number / 100;
            getZero = true;
        }
        if ((scale > 0) && (!(scale % 10 > 0))) {
            numIndex = 1;
            number = number / 10;
            getZero = true;
        }
        int zeroSize = 0;
        while (true) {
            if (number <= 0) {
                break;
            }
            // 每次获取到最后一个数
            numUnit = (int) (number % 10);
            if (numUnit > 0) {
                if ((numIndex == 9) && (zeroSize >= 3)) {
                    sb.insert(0, CN_UPPER_MONETRAY_UNIT[6]);
                }
                if ((numIndex == 13) && (zeroSize >= 3)) {
                    sb.insert(0, CN_UPPER_MONETRAY_UNIT[10]);
                }
                sb.insert(0, CN_UPPER_MONETRAY_UNIT[numIndex]);
                sb.insert(0, CN_UPPER_NUMBER[numUnit]);
                getZero = false;
                zeroSize = 0;
            } else {
                ++zeroSize;
                //"分", "角", "元","万", "亿","兆"位不会出现零
                if (numIndex != 0 && numIndex != 1 && numIndex != 2
                        && numIndex != 6 && numIndex != 10 && numIndex != 14) {
                    if (!(getZero)) {
                        sb.insert(0, CN_UPPER_NUMBER[numUnit]);
                    }
                }

                if (numIndex == 2) {
                    if (number > 0) {
                        sb.insert(0, CN_UPPER_MONETRAY_UNIT[numIndex]);
                    }
                } else if (((numIndex - 2) % 4 == 0) && (number % 1000 > 0)) {
                    sb.insert(0, CN_UPPER_MONETRAY_UNIT[numIndex]);
                }
                getZero = true;
            }
            // 让number每次都去掉最后一个数
            number = number / 10;
            ++numIndex;
        }
        // 如果signum == -1，则说明输入的数字为负数，就在最前面追加特殊字符：负
        if (signum == -1) {
            sb.insert(0, CN_NEGATIVE);

        }// 除了0.00其他数据都要带特殊字符：整

        sb.append(CN_FULL);
        log.info("金额转大写：" + sb.toString());
        return sb.toString();
    }



    /**
     * @Author : jet
     * @Description 比较时间
     * @Date 14:05  2019/9/24
     */
    public static boolean compareTimestamp(Timestamp s1,Timestamp s2){
        long t1 = s1==null?0:s1.getTime();
        long t2 = s2==null?System.currentTimeMillis():s2.getTime();
        return t1-t2>=0;
    }


    /**
     * @Author : jet
     * @Description 校验是否是纯数字id
     * @Date 9:56  2019/10/29
     */
    public static boolean checkIsNumber(String checkValue){
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(checkValue).matches();
    }

    /**
     * @Author : jet
     * @Description 检查是否为空
     * @Date 11:16  2019/12/4
     */
    public static boolean isEmpty(Object str){
        return (str == null || "".equals(str));
    }


    public static String[] chars = new String[] { "a", "b", "c", "d", "e", "f",
            "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
            "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z" };


    /**
     * @Author : jet
     * @Description 本算法利用62个可打印字符，通过随机生成32位UUID，由于UUID都为十六进制，
     *              所以将UUID分成8组，每4个为一组，然后通过模62操作，结果作为索引取出字符
     * @Date 16:06  2019/11/7
     */
    public static String generateShortUuid() {
        StringBuffer shortBuffer = new StringBuffer();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        for (int i = 0; i < 8; i++) {
            String str = uuid.substring(i * 4, i * 4 + 4);
            int x = Integer.parseInt(str, 16);
            shortBuffer.append(chars[x % 0x3E]);
        }
        return shortBuffer.toString();
    }

}
