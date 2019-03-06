package com.jet.springboot_vonfig.permission;

import com.jet.customized.api.AppsController;
import com.jet.customized.service.CustomizedCustomerService;
import com.jet.entity.StaffProfile;
import com.jet.mobile.controller.IndexController;
import com.jet.repository.StaffProfileRepository;
import com.jet.util.APIUtil;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;

/**
 * @ClassName: PermissionHandle
 * @Description: 手机端权限控制器
 * @Author: Jet.Chen
 * @Date: 2019/2/15 9:43
 * @Version: 1.0
 **/
@Component
@Log4j
public class PermissionHandle implements HandlerInterceptor {

    // IP 白名单
    private ArrayList<String> ipLeapList = new ArrayList<>();

    {
        // TODO 初始化白名单，作用是直接差内存比较快，不用再请求数据库层的白名单了
        ipLeapList.add("192.168.154.77");
        ipLeapList.add("192.168.154.109");
    }


    @Autowired
    AppsController appsController;

    @Autowired
    IndexController indexController;

    @Autowired
    CustomizedCustomerService customizedCustomerService;

    @Autowired
    StaffProfileRepository staffProfileRepository;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String requestURI = request.getRequestURI();
        String httpIp = APIUtil.getHttpIp(request);

        // 查询ip是否被加入白名单
        if (!ipLeapList.contains(httpIp)) {
            // 进行统一的权限校验
//            StaffProfile staffProfile = appsController.checkCookie(request, response);
            StaffProfile staffProfile = this.checkCookie(request, response);
            if (staffProfile == null) {
                response.sendRedirect("/error/signedError");
                return false;
            }
            MobileThreadLocal.add(staffProfile);
        } else {
            MobileThreadLocal.add(staffProfileRepository.findOne(40L));
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // modal 中增加 accountInfo
        if (modelAndView != null) {
            ModelMap modelMap = modelAndView.getModelMap();
            if (modelMap != null) {
                modelMap.addAttribute("accountInfo", customizedCustomerService.getAccountInfo(null, null, MobileThreadLocal.getCurrentUser()));
            }
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        MobileThreadLocal.remove();
    }

    /**
     * @Description: cookie校验
     * @Param: [request, response]
     * @return: com.wailian.entity.StaffProfile
     * @Author: Jet.Chen
     * @Date: 2018/12/20
     */
    public StaffProfile checkCookie(HttpServletRequest request, HttpServletResponse response){
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) return null;
        Optional<Cookie> first = Arrays.stream(cookies).filter(val -> "APPS_SESSION".equals(val.getName())).findFirst();
        if (!first.isPresent()) return null;
        Cookie cookie = first.get();
        String key = cookie.getValue();
        if (StringUtils.isBlank(key)) {
            // 反手就是一个删除cookie
            cookie.setMaxAge(0);
            response.addCookie(cookie);
            return null;
        }
        if (!redisClient.existsKey(key)) {
            // 反手就是一个删除cookie
            cookie.setMaxAge(0);
            response.addCookie(cookie);
            return null;
        }
        Map<String, String> map = redisClient.mapGet(key);
        StaffProfile staffProfile = staffProfileRepository.findOne(Long.parseLong(map.get("staffId")));
        if (staffProfile == null || staffProfile.getStatus().getId() != 18) {
            // 反手就是一个删除cookie
            cookie.setMaxAge(0);
            response.addCookie(cookie);
            // 删除redis
            redisClient.del(key);
            return null;
        }
        // 刷新时间
        redisClient.mapSet(key, map, 7600);
        return staffProfile;
    }

}
