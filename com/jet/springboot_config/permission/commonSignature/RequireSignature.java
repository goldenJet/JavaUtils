package com.wailian.annotation;

import java.lang.annotation.*;

/**
* @Description: 接口权限校验注解
* @Author: Jet.Chen
* @Date: 2019/8/20 16:05
*/
@Documented
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireSignature {
}