package com.jet.springboot_vonfig.permission;

import com.wailian.entity.StaffProfile;

/**
 * @ClassName: MobileThreadLocal
 * @Description: 移动端的权限校验是手写的，所以需要手写一个 ThreadLocal 来存储当前用户
 * @Author: Jet.Chen
 * @Date: 2019/3/4 11:31
 * @Version: 1.0
 **/
public class MobileThreadLocal {

    private static final ThreadLocal<StaffProfile> mobileThreadLocal = new ThreadLocal<>();

    public static void add(StaffProfile staffProfile){
        mobileThreadLocal.set(staffProfile);
    }


    public static StaffProfile getCurrentUser(){
        return mobileThreadLocal.get();
    }


    public static void remove(){
        mobileThreadLocal.remove();
    }
}
