package com.wailian.util;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberToCarrierMapper;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.google.i18n.phonenumbers.geocoding.PhoneNumberOfflineGeocoder;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @Author: Jet
 * @Description: 国际手机号校验
 * @Date: 2018/5/9 9:20
 */
public class LibphonenumberUtil {

    private static final PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();

    private static PhoneNumberToCarrierMapper carrier = PhoneNumberToCarrierMapper.getInstance();

    private static PhoneNumberOfflineGeocoder geocoder = PhoneNumberOfflineGeocoder.getInstance();

    private static final String DEFAULT_COUNTRY = "CN";

    private static final String[] phoneCases = new String[] {
            "8618611234515",  //中国 true
            "00886912347718",   //台湾
            "006581234994",     //新加坡
            "15911234718",      //中国
            "008201234704546",  //Korea
            "17091234155"       //中国170
    };

    public static final Map<String, String> CHINESE_CARRIER_MAPPER = new HashMap<>();
    static {
        CHINESE_CARRIER_MAPPER.put("China Mobile", "中国移动");
        CHINESE_CARRIER_MAPPER.put("China Unicom", "中国联通");
        CHINESE_CARRIER_MAPPER.put("China Telecom", "中国电信");
    }

    public static void main(String[] args) {
        System.out.println(doGeo("17811981865", "86"));
    }

    /**
     * @Author: Jet
     * @Description ① 可以加区号，也可以不加，区号默认86
     * ② 区号前面的“+”和“00”占位可加可不加
     * ② 手机号中间可以增加“-”
     * @param phone “+8617717031234 +008617717031234 8617717031234 177-1703-1234”
     * @Date: 2018/5/9 9:21
     */
    public static boolean doValidUniversal(String phone) {
        Phonenumber.PhoneNumber phoneNumber = doParse(phone);
        return phoneNumber.hasNationalNumber() && doValid(phoneNumber.getNationalNumber() + "", phoneNumber.getCountryCode() + "");
    }

    /**
     * @Author: Jet
     * @Description 电话解析逻辑
     * @param phone “+8617717031234 +008617717031234 8617717031234 177-1703-1234””
     * @return 电话实体类 Phonenumber.PhoneNumber
     * @Date: 2018/5/9 9:21
     */
    public static Phonenumber.PhoneNumber doParse(String phone) {
        try {
            return phoneNumberUtil.parse(phone, DEFAULT_COUNTRY);
        } catch (NumberParseException e) {
            throw new NumberFormatException("invalid phone number: " + phone);
        }
    }

    /**
     * @Author: Jet
     * @Description 手机校验逻辑
     * @param phoneNumber 手机号
     * @param countryCode 手机区号
     * @Date: 2018/5/9 9:21
     */
    public static boolean doValid(String phoneNumber, String countryCode){
        int ccode = Integer.parseInt(countryCode);
        long phone = Long.parseLong(phoneNumber);

        Phonenumber.PhoneNumber pn = new Phonenumber.PhoneNumber();
        pn.setCountryCode(ccode);
        pn.setNationalNumber(phone);

        return phoneNumberUtil.isValidNumber(pn);
    }

    /**
     * @Author: Jet
     * @Description 手机运营商
     * @param phoneNumber 手机号
     * @param countryCode 手机区号
     * @return 能转成中文则返回中文，否则返回英文的
     * @Date: 2018/5/9 9:21
     */
    public static String doCarrier(String phoneNumber, String countryCode){
        int ccode = Integer.parseInt(countryCode);
        long phone = Long.parseLong(phoneNumber);

        Phonenumber.PhoneNumber pn = new Phonenumber.PhoneNumber();
        pn.setCountryCode(ccode);
        pn.setNationalNumber(phone);

        //返回结果只有英文，自己转成成中文
        String carrierEn = carrier.getNameForNumber(pn, Locale.ENGLISH);

        return CHINESE_CARRIER_MAPPER.containsKey(carrierEn)?CHINESE_CARRIER_MAPPER.get(carrierEn):carrierEn;
    }

    /**
     * @Author: Jet
     * @Description 手机归属地
     * @param phoneNumber 手机号
     * @param countryCode 手机区号
     * @Date: 2018/5/9 9:21
     */
    public static String doGeo(String phoneNumber, String countryCode){
        int ccode = Integer.parseInt(countryCode);
        long phone = Long.parseLong(phoneNumber);

        Phonenumber.PhoneNumber pn = new Phonenumber.PhoneNumber();
        pn.setCountryCode(ccode);
        pn.setNationalNumber(phone);

        return geocoder.getDescriptionForNumber(pn, Locale.CHINESE);
    }
}