package com.jet.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @ClassName: ReflectionUtil
 * @Description: 反射工具类
 * @Author: Jet.Chen
 * @Date: 2019/2/20 18:46
 * @Version: 1.0
 **/
public class ReflectionUtil {

    /**
    * @Description: 获取私有成员变量的值
    * @Param: [instance, fieldName]
    * @return: java.lang.Object
    * @Author: Jet.Chen
    * @Date: 2019/2/20 18:46
    */
    public static Object getValue(Object instance, String fieldName)
            throws IllegalAccessException, NoSuchFieldException {

        return getValue(instance.getClass(), fieldName);
    }

    /**
    * @Description: 获取私有成员变量的值
    * @Param: [clazz, fieldName]
    * @return: java.lang.Object
    * @Author: Jet.Chen
    * @Date: 2019/2/20 19:16
    */
    public static Object getValue(Class<?> clazz, String fieldName)
            throws IllegalAccessException, NoSuchFieldException {

        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true); // 参数值为true，禁止访问控制检查

        return field.get(clazz);
    }

    /**
    * @Description: 设置私有成员变量的值
    * @Param: [instance, fileName, value]
    * @return: void
    * @Author: Jet.Chen
    * @Date: 2019/2/20 18:47
    */
    public static void setValue(Object instance, String fileName, Object value)
            throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {

        setValue(instance.getClass(), fileName, value);
    }

    /**
    * @Description: 设置私有成员变量的值
    * @Param: [clazz, fileName, value]
    * @return: void
    * @Author: Jet.Chen
    * @Date: 2019/2/20 19:17
    */
    public static void setValue(Class<?> clazz, String fileName, Object value)
            throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {

        Field field = clazz.getDeclaredField(fileName);
        field.setAccessible(true);
        field.set(clazz, value);
    }

    /**
    * @Description: 访问私有方法
    * @Param: [instance, methodName, classes, objects]
    * @return: java.lang.Object
    * @Author: Jet.Chen
    * @Date: 2019/2/20 18:47
    */
    public static Object callMethod(Object instance, String methodName, Class[] classes, Object[] objects)
            throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException {

        return callMethod(instance.getClass(), methodName, classes, objects);
    }


    /**
    * @Description: 访问私有方法
    * @Param: [clazz, methodName, classes, objects]
    * @return: java.lang.Object
    * @Author: Jet.Chen
    * @Date: 2019/2/20 19:18
    */
    public static Object callMethod(Class<?> clazz, String methodName, Class[] classes, Object[] objects)
            throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException {

        Method method = clazz.getDeclaredMethod(methodName, classes);
        method.setAccessible(true);
        return method.invoke(clazz, objects);
    }
}
