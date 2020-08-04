package com.AESUtil.util;

import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;
import java.security.SecureRandom;

/**
 * Created by jet.chen on 2017/5/24.
 */
@Log4j
public class AESUtil {

    private static final String ENCODERULES = "jetchen.cn";
    /**
     * 加密
     */
    public static String AESEncode(String encodeRules,String content){
        return AESEncodeBase(encodeRules, content, 1);
    }
    /**
     * 解密
     */
    public static String AESDncode(String encodeRules,String content){
        return AESDncodeBase(encodeRules, content, 1);
    }

    /**
     * @Author: Jet
     * @Description:
     * @param type 1跟教育所使用的最新版本的加密算法一致，兼容mysql原生
     * @Date: 2018/4/20 12:45
     */
    public static String AESEncodeBase(String encodeRules,String content, int type){
        try {
            byte[] raw;
            if (type == 1) {
                // 改良的简版，作用是保证java 和 mysql 加解密一致
                raw = encodeRules.getBytes("utf-8");
            } else {
                KeyGenerator keygen= KeyGenerator.getInstance("AES");
                // 根据传入的字节数组产生的随机源，在windows上每次都产生相同的key，但是solaris或者部分Linux系统上则不然
                //keygen.init(128, new SecureRandom(encodeRules.getBytes()));
                //防止linux下 随机生成key
                SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
                secureRandom.setSeed(encodeRules.getBytes());
                keygen.init(128, secureRandom);
                SecretKey original_key=keygen.generateKey();
                raw=original_key.getEncoded();
            }

            SecretKey key=new SecretKeySpec(raw, "AES");
            Cipher cipher= Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte [] byte_encode=content.getBytes("utf-8");
            byte [] byte_AES=cipher.doFinal(byte_encode);
            return new BASE64Encoder().encode(byte_AES);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | InvalidKeyException | UnsupportedEncodingException | BadPaddingException e) {
            log.error("AES 加密异常：" + content, e);
        }

        //如果有错就返返回nulll
        return null;
    }
    /**
     * @Author: Jet
     * @Description:
     * @param type 1:跟教育所使用的最新版本的加密算法一致，兼容mysql原生
     * @Date: 2018/4/20 12:48
     */
    public static String AESDncodeBase(String encodeRules,String content, int type){
        try {
            byte[] raw;
            if (type == 1) {
                // 改良的简版，作用是保证java 和 mysql 加解密一致
                raw = encodeRules.getBytes("utf-8");
            } else {
                KeyGenerator keygen= KeyGenerator.getInstance("AES");
                // 根据传入的字节数组产生的随机源，在windows上每次都产生相同的key，但是solaris或者部分Linux系统上则不然
                //keygen.init(128, new SecureRandom(encodeRules.getBytes()));
                //防止linux下 随机生成key
                SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
                secureRandom.setSeed(encodeRules.getBytes());
                keygen.init(128, secureRandom);
                SecretKey original_key=keygen.generateKey();
                raw=original_key.getEncoded();
            }
            SecretKey key=new SecretKeySpec(raw, "AES");
            Cipher cipher=Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte [] byte_content= new BASE64Decoder().decodeBuffer(content);
            //解密
            byte [] byte_decode=cipher.doFinal(byte_content);
            return new String(byte_decode,"utf-8");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IOException | IllegalBlockSizeException | BadPaddingException e) {
            log.error("AES 解密异常：" + content, e);
        }

        //如果有错就返回nulll
        return null;
    }

    /**
     * 使用默认的规则进行加密
     */
    public static String AESEncode(String content){
        if (StringUtils.isBlank(content)) {
            return null;
        }
        return AESEncode(ENCODERULES, content);
    }
    /**
     * 使用默认的规则进行解密
     */
    public static String AESDncode(String content){
        if (StringUtils.isBlank(content)) {
            return null;
        }
        return AESDncode(ENCODERULES, content);
    }

    /**
     * 使用默认的规则进行解密
     */
    public static String AESDncodeOld(String content){
        if (StringUtils.isBlank(content)) {
            return null;
        }
        return AESDncodeBase(ENCODERULES, content, 0);
    }

    public static void main(String[] args) {
        // hzKqaQgz3XDfoNwgN4Ay0A==
        System.out.println(AESEncode("WLCRM_PASSD_0728", "FkWMiT9MzsH153KVpm0USQ=="));
    }
}
