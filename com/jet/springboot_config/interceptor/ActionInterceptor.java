package com.jet.springboot_config.interceptor;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.management.relation.Role;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jet.chen on 2017/2/28.
 */
@Service
@Log4j
public class ActionInterceptor implements HandlerInterceptor {

    @Autowired
    ActionService actionService;

    @Autowired
    PageActionConvertService pageActionConvertService;

    @Autowired
    RoleMenuAuthRepository roleMenuAuthRepository;

    @Autowired
    MenuRepository menuRepository;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        PageHeader pageHeader = pageActionConvertService.genericPageHeader(httpServletRequest);
        httpServletRequest.setAttribute("pageHeader", pageHeader);
        httpServletRequest.setAttribute("accountInfo", pageActionConvertService.getAccountInfo(httpServletRequest));
        String uri = httpServletRequest.getRequestURI();
        uri = uri.substring(1, uri.length());
        Menu menu = menuRepository.findTopByUrlPath(uri);
        if (menu != null) {
            try {
                StaffProfile staffProfile = (StaffProfile) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                Role role = staffProfile.getRoles().get(0);
                RoleMenuAuth roleMenuAuth = roleMenuAuthRepository.findTopByRoleAndMenu(role, menu);
                boolean update = roleMenuAuth.getAllowUpdate()!= null && roleMenuAuth.getAllowUpdate() == 1;
                httpServletRequest.setAttribute("authUpdate", update);
            }catch (Exception e){

            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        String uri = httpServletRequest.getRequestURI();
        uri = uri.substring(1, uri.length());
        List<Action> actionList = actionService.searchActionByCondition(new QueryCondition("url", QueryCondition.Condition.LIKE, uri));
        if (actionList.size() == 1) {
            Action action = actionList.get(0);
            String className = action.getClassName();
            if (className != null) {
                Class handelClass = Class.forName(className);
                Object object = handelClass.newInstance();
                if (object instanceof ActionHandle) {
                    ActionHandle actionHandle = (ActionHandle) object;
                    actionHandle.afterCompletion(action, httpServletRequest, httpServletResponse, o, e);
                } else {
                    if (action.getMethodName() != null) {
                        Map<Class, Object> parameterValue = new HashMap<>();
                        parameterValue.put(HttpServletRequest.class, httpServletRequest);
                        parameterValue.put(HttpServletResponse.class, httpServletResponse);
                        parameterValue.put(Object.class, o);
                        parameterValue.put(Action.class, action);
                        parameterValue.put(Exception.class, e);
                        executeMethodByActionMethodName(parameterValue, handelClass, action.getMethodName());
                    }
                }
            }
        }
    }


    private void executeMethodByActionMethodName(Map<Class, Object> parameterValue, Class targetClass, String methodName) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        Method[] methods = targetClass.getMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                Parameter[] parameters = method.getParameters();
                List<Object> objectList = new ArrayList<>();
                for (Parameter parameter : parameters) {
                    objectList.add(parameterValue.get(parameter.getType()));
                }
                method.invoke(targetClass.newInstance(), objectList.toArray());
                return;
            }
        }
        log.info("Not found method:" + methodName + " by class:" + targetClass.getName());
    }
}
