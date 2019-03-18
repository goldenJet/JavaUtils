package com.jet.util;

import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;

@Log4j
@Component
public class APIUtil {

    // 签名前缀
    public static final String SIGNEDSTR = "jetAPI";

    /**
     * @Author: Jet
     * @Description: 拿请求ip
     * @Date: 2017/12/5 17:35
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
    * @Date: 2018/10/30
    */
    public static String getUserAgent(HttpServletRequest request){
        return request.getHeader("user-agent");
    }


    /**
    * @Description: 文件上传下载验签
    * @Param: [str, date]
    * @return: java.lang.String
    * @Author: Jet.Chen
    * @Date: 2019/3/12 10:21
    */
    public static String checkSig4File(String token, String signed, boolean utFlag){
        try {
            if (StringUtils.isBlank(token) || StringUtils.isBlank(signed)) {
                return "token 或 signed 为空";
            }
            token = new String(Base64Utils.decodeFromString(token));
            int tokenLength = token.length();
            if ((utFlag && tokenLength < 15) || (!utFlag && tokenLength != 14)) {
                return "token 错误";
            }
            // 时间戳获取
            String timestampStr = token.substring(tokenLength - 14, tokenLength); //20180521121200
            // 时间校验 有效时间10分钟
            long tokenTime = (new SimpleDateFormat("yyyyMMddHHmmss")).parse(timestampStr).getTime();
            if (Math.abs(System.currentTimeMillis() - tokenTime) > 10*60*1000) { // 有效期为10分钟
                return "token 已过期";
            }
            // 签名拼接
            StringBuilder stringBuilder;
            if (utFlag) {
                stringBuilder = new StringBuilder(SIGNEDSTR).append("_").append(token, 0, tokenLength - 14).append("_").append(timestampStr);
            } else {
                stringBuilder = new StringBuilder(SIGNEDSTR).append("_").append(timestampStr);
            }
            // 签名生成
            String signedMake = MD5Util.generateMD5(stringBuilder.toString());
            return signed.equals(signedMake) ? null : "签名校验失败";
        } catch (Exception e) {
            log.error("checkSign4File error, token【"+token+"】,signed【"+signed+"】", e);
            return "签名校验失败";
        }
    }
}
