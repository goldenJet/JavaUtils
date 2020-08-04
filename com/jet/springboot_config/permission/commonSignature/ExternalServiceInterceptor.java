package com.jet.annotation;

import com.alibaba.fastjson.JSON;
import com.jet.pojo.MyResposeData;
import com.jet.util.APIUtil;
import lombok.extern.log4j.Log4j;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;

/*
统一的接口鉴权
*/
@Log4j
@Component
public class ExternalServiceInterceptor extends HandlerInterceptorAdapter {

    // IP 白名单
    private ArrayList<String> ipLeapList = new ArrayList<>();

    {
        // TODO 初始化白名单，作用是直接差内存比较快，不用再请求数据库层的白名单了
//        ipLeapList.add("127.0.0.1");
        ipLeapList.add("192.168.4.106");
        ipLeapList.add("192.168.168.161");
        ipLeapList.add("192.168.168.167");
        ipLeapList.add("192.168.168.253");
        ipLeapList.add("192.168.168.188");
        ipLeapList.add("192.168.170.184");
        ipLeapList.add("192.168.170.187");
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Method method = ((HandlerMethod) handler).getMethod();
        if (AnnotatedElementUtils.isAnnotated(method, RequireSignature.class)) {
            String httpIp = APIUtil.getHttpIp(request);
            log.info("接口权限校验,ip:"+httpIp);
            if (!ipLeapList.contains(httpIp)) {
                String token = request.getParameter("token");
                String signed = request.getParameter("signed");

                log.info(String.format("接口权限校验，token：%s，signed：%s", token, signed));

                // 鉴权
                MyResposeData resposeData = new MyResposeData();
                String checkSig4File = APIUtil.checkSig4File(token, signed, false, null);
                if (checkSig4File != null) {
                    resposeData.initError(resposeData, checkSig4File, "401");
                    log.error(checkSig4File);
                    ajaxResponseJsonReturn(response, request, resposeData);
                    return false;
                }
            }


        }
        return super.preHandle(request, response, handler);
    }


    protected void ajaxResponseJsonReturn(HttpServletResponse response, HttpServletRequest request, MyResposeData resposeData) throws IOException {
        response.setHeader("Content-type", "application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");//Constant.ENCODE_UTF8
        response.setContentType("application/json; charset=utf-8");
        PrintWriter out = response.getWriter();
        out.print(JSON.toJSONString(resposeData));
        out.flush();
        out.close();
    }
}