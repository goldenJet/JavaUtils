package com.jet.utils;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by jet.chen on 4/25/2017.
 * 对象属性传递（如 JPA 的修改即 save 操作可用）
 */
public class CrmBeansUtil {

    public static void copyObjectNotAwareNull(Object sourceObject, Object targetObject){
        final BeanWrapper src = new BeanWrapperImpl(sourceObject);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<String>();
        for(java.beans.PropertyDescriptor pd : pds) {
            try{
                Object srcValue = src.getPropertyValue(pd.getName());
                if (srcValue == null){
                    emptyNames.add(pd.getName());
                }
            } catch(Exception e) {
                emptyNames.add(pd.getName());
            }
        }
        String[] result = new String[emptyNames.size()];
        result = emptyNames.toArray(result);
        BeanUtils.copyProperties(sourceObject, targetObject, result);

    }
}
