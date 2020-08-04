package com.wailian.util;

import lombok.extern.log4j.Log4j;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Log4j
/**
 * @Author: Jet
 * @Description: MD5 加密
 * @Date: 2018/1/11 9:46
 */
public class MD5Util {

    /**
     * @Author: Jet
     * @Description: CTI token 生成
     * @Date: 2018/1/11 9:51
     */
    public static String CtiToken(){
        //  确定计算方法
        String str1= generateMD5("POLYLINK_MESSAGE_TOKEN");
        //	 获取当前系统时间
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");//设置日期格式
        String time=df.format(new Date());
        //	加密后的字符串+当前时间，
        String str2=str1+time;
        String newstr = generateMD5(str2);
        return newstr;
    }

    /**
     * @Author: Jet
     * @Description: MD5加密生成32位
     * @Date: 2018/1/11 9:51
     */
    public static String generateMD5(String input){
        try {
            //1.初始化MessageDigest信息摘要对象,并指定为MD5不分大小写都可以
            MessageDigest md = java.security.MessageDigest.getInstance("md5");
            //2.传入需要计算的字符串更新摘要信息，传入的为字节数组byte[],
            //将字符串转换为字节数组使用getBytes()方法完成
            //指定时其字符编码 为utf-8
            md.update(input.getBytes("utf-8"));
            //3.计算信息摘要digest()方法
            //返回值为字节数组
            byte [] hashCode = md.digest();
            //4.将byte[] 转换为找度为32位的16进制字符串
            //声明StringBuffer对象来存放最后的值
            StringBuffer sb = new StringBuffer();
            //遍历字节数组
            for(byte b:hashCode){
                //对数组内容转化为16进制，
                sb.append(Character.forDigit(b>>4&0xf, 16));
                //换2次为32位的16进制
                sb.append(Character.forDigit(b&0xf, 16));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            log.error("CRM系统错误", e);
            return null;
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            log.error("CRM系统错误", e);
            return null;
        }
    }
}
