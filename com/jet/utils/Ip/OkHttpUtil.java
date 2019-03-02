package com.jet.mini.utils;

import lombok.extern.log4j.Log4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: OkHttpUtil
 * @Description:
 * @Author: Jet.Chen
 * @Date: 2019/2/2 14:05
 * @Version: 1.0
 **/
@Log4j
public class OkHttpUtil {

    /**
    * @Description: get 请求
    * @Param: [url]
    * @return: java.lang.String
    * @Author: Jet.Chen
    * @Date: 2019/2/2 14:31
    */
    public static String getStr(String url){
        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(50, TimeUnit.MILLISECONDS) // 连接超时时间
                .retryOnConnectionFailure(false)
                .readTimeout(100, TimeUnit.MILLISECONDS) // 读取超时时间
                .writeTimeout(100, TimeUnit.MILLISECONDS) // 写超时时间
                .build();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.110 Safari/537.36")
                .build();
        try {
            try (Response response = client.newCall(request).execute()) {
                return response.body() == null ? null : response.body().string();
            }
        } catch (IOException e) {
            log.error("OkHttpUtil getStr error", e);
        }
        return null;
    }

}
