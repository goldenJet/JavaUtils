package com.jet.mini.protobuf;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @ClassName: ProtoTest
 * @Description: ProtoBuf 测试
 * @Author: Jet.Chen
 * @Date: 2019/5/8 9:55
 * @Version: 1.0
 **/
public class ProtoTest {

    public static void main(String[] args) {
        try {
            /** Step1：生成 personTest 对象 */
            // personTest 构造器
            PersonTestProtos.PersonTest.Builder personBuilder = PersonTestProtos.PersonTest.newBuilder();
            // personTest 赋值
            personBuilder.setName("Jet Chen");
            personBuilder.setEmail("ckk505214992@gmail.com");
            personBuilder.setSex(PersonTestProtos.PersonTest.Sex.MALE);

            // 内部的 PhoneNumber 构造器
            PersonTestProtos.PersonTest.PhoneNumber.Builder phoneNumberBuilder = PersonTestProtos.PersonTest.PhoneNumber.newBuilder();
            // PhoneNumber 赋值
            phoneNumberBuilder.setType(PersonTestProtos.PersonTest.PhoneNumber.PhoneType.MOBILE);
            phoneNumberBuilder.setNumber("17717037257");

            // personTest 设置 PhoneNumber
            personBuilder.addPhone(phoneNumberBuilder);

            // 生成 personTest 对象
            PersonTestProtos.PersonTest personTest = personBuilder.build();

            /** Step2：序列化和反序列化 */
            // 方式一 byte[]：
            // 序列化
//            byte[] bytes = personTest.toByteArray();
            // 反序列化
//            PersonTestProtos.PersonTest personTestResult = PersonTestProtos.PersonTest.parseFrom(bytes);
//            System.out.println(String.format("反序列化得到的信息，姓名：%s，性别：%d，手机号：%s", personTestResult.getName(), personTest.getSexValue(), personTest.getPhone(0).getNumber()));



            // 方式二 ByteString：
            // 序列化
//            ByteString byteString = personTest.toByteString();
//            System.out.println(byteString.toString());
            // 反序列化
//            PersonTestProtos.PersonTest personTestResult = PersonTestProtos.PersonTest.parseFrom(byteString);
//            System.out.println(String.format("反序列化得到的信息，姓名：%s，性别：%d，手机号：%s", personTestResult.getName(), personTest.getSexValue(), personTest.getPhone(0).getNumber()));



            // 方式三 InputStream
            // 粘包,将一个或者多个 protobuf 对象字节写入 stream
            // 序列化
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            personTest.writeDelimitedTo(byteArrayOutputStream);
            // 反序列化，从 steam 中读取一个或者多个 protobuf 字节对象
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            PersonTestProtos.PersonTest personTestResult = PersonTestProtos.PersonTest.parseDelimitedFrom(byteArrayInputStream);
            System.out.println(String.format("反序列化得到的信息，姓名：%s，性别：%d，手机号：%s", personTestResult.getName(), personTest.getSexValue(), personTest.getPhone(0).getNumber()));

        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
