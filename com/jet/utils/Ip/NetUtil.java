package com.jet.mini.utils;

import com.google.gson.Gson;
import com.jet.mini.pojo.IPVO;
import com.qiniu.util.StringUtils;
import lombok.extern.log4j.Log4j;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * @ClassName: NetUtil
 * @Description: 网络工具类 util
 * @Author: Jet.Chen
 * @Date: 2019/1/31 17:03
 * @Version: 1.0
 **/
@Log4j
public class NetUtil {


    /**
    * @Description: 拿请求ip
    * @Param: [request]
    * @return: java.lang.String
    * @Author: Jet.Chen
    * @Date: 2019/1/31 17:04
    */
    public static String getHttpIp(HttpServletRequest request){
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
    * @Description: 获取用户浏览器信息
    * @Param: [request]
    * @return: java.lang.String
    * @Author: Jet.Chen
    * @Date: 2019/1/31 17:05
    */
    public static String getUserAgent(HttpServletRequest request){
        return request.getHeader("user-agent");
    }

    /**
    * @Description: 请求淘宝的公共api组件获取IP地址信息
     * 2019-2-12 已弃用，因为今天发现淘宝的ip库宕了，遂决定做一个本地的ip库，做为备份
    * @Param: [ip]
    * @return: com.jet.mini.pojo.IPVO
    * @Author: Jet.Chen
    * @Date: 2019/2/2 15:11
    */
//    @Deprecated
    public static IPVO getIpInfo(String ip) {
        IPVO result = null;
        long t = System.currentTimeMillis();
        String taobao = OkHttpUtil.getStr("http://ip.taobao.com/service/getIpInfo.php?ip=" + ip);
        System.out.println(System.currentTimeMillis() - t);
        if (!StringUtils.isNullOrEmpty(taobao)) {
            Gson gson = new Gson();
            Map map = gson.fromJson(taobao, Map.class);
            if (map != null && (Double) map.get("code") == 0) {
                result = new IPVO((Map)map.get("data"));
            }
        }
        if (result == null) {
            // 从本地读取
            result = getIpInfoFromLocal(ip);
        }
        return result;
    }

    /**
    * @Description: 从本地（纯真IP地址库）获取ip信息
    * @Param: [ip]
    * @return: com.jet.mini.pojo.IPVO
    * @Author: Jet.Chen
    * @Date: 2019/3/2 18:48
    */
    public static IPVO getIpInfoFromLocal(String ip) {
        return IPAddressUtils.getIPLocation(ip);
    }


    /**
    * @Description:  从ip的字符串形式得到字节数组形式
    * @Param: [ip] 字符串形式的ip
    * @return: byte[] 字节数组形式的ip
    * @Author: Jet.Chen
    * @Date: 2019/2/12 21:47
    */
    static byte[] getIpByteArrayFromString(String ip) {
        byte[] ret = new byte[4];
        StringTokenizer st = new StringTokenizer(ip, ".");
        try {
            ret[0] = (byte) (Integer.parseInt(st.nextToken()) & 0xFF);
            ret[1] = (byte) (Integer.parseInt(st.nextToken()) & 0xFF);
            ret[2] = (byte) (Integer.parseInt(st.nextToken()) & 0xFF);
            ret[3] = (byte) (Integer.parseInt(st.nextToken()) & 0xFF);
        } catch (Exception e) {
            log.error("从ip的字符串形式得到字节数组形式报错" + e.getMessage(), e);
        }
        return ret;
    }

    public static void main(String[] args) {
        System.out.println(IPAddressUtils.getIPLocation("127.0.0.1").toString());
    }

    /**
    * @Description: 字节数组IP转String
    * @Param: [ip] ip的字节数组形式
    * @return: java.lang.String 字符串形式的ip
    * @Author: Jet.Chen
    * @Date: 2019/2/12 21:47
    */
    static String getIpStringFromBytes(byte[] ip) {
        return String.valueOf(ip[0] & 0xFF) +
                '.' +
                (ip[1] & 0xFF) +
                '.' +
                (ip[2] & 0xFF) +
                '.' +
                (ip[3] & 0xFF);
    }

    /**
    * @Description: 根据指定的编码方式将字节数组转换成字符串
    * @param b        字节数组
    * @param offset   要转换的起始位置
    * @param len      要转换的长度
    * @param encoding 编码方式
    * @return: java.lang.String 如果encoding不支持，返回一个缺省编码的字符串
    * @Author: Jet.Chen
    * @Date: 2019/2/12 21:47
    */
    static String getString(byte[] b, int offset, int len, String encoding) {
        try {
            return new String(b, offset, len, encoding);
        } catch (UnsupportedEncodingException e) {
            return new String(b, offset, len);
        }
    }


}
