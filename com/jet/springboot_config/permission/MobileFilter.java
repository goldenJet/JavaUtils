package com.jet.springboot_vonfig.permission;

import com.jet.utils.APIUtil;
import lombok.extern.log4j.Log4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

/**
 * @ClassName: MobileFilter
 * @Description: 手机端访问过滤器
 * @Author: Jet.Chen
 * @Date: 2019/2/14 18:03
 * @Version: 1.0
 **/
@Log4j
public class MobileFilter implements Filter {

    // IP 黑名单
    private ArrayList<String> ipBlockList = new ArrayList<>();

    {
        // TODO 初始化黑名单，作用是直接差内存比较快，不用再请求数据库层的黑名单了
        ipBlockList.add("192.168.154.7777");
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        long t = System.currentTimeMillis();

        HttpServletResponse res = (HttpServletResponse) response;
        HttpServletRequest req = (HttpServletRequest) request;

//        String requestURI = req.getRequestURI();
        String httpIp = APIUtil.getHttpIp(req);
        // 请求路径不用打印，因为已经有过滤器打印过了

        // 打印请求参数
        Map<String, String[]> parameterMap = req.getParameterMap();
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            log.info("参数打印, key: " + entry.getKey() + ", value: " + Arrays.toString(entry.getValue()));
        }
        // 查询ip是否被加入黑名单
        if (ipBlockList.contains(httpIp)) {
            res.sendRedirect("/error/ipBlock");
            return;
        }
        // TODO 同一个IP在1分钟中登陆次数过多，则加入黑名单

        chain.doFilter(request, res);
        log.info("接口处理时长：" + (System.currentTimeMillis() - t) + " 毫秒");
    }
    @Override
    public void destroy() {
    }
}