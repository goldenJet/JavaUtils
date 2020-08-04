**手机号工具类**
## 一、google 的工具
1. 使用的是 libphonenumber

``` xml
<!--手机号解析-->
<dependency>
    <groupId>com.googlecode.libphonenumber</groupId>
    <artifactId>libphonenumber</artifactId>
    <version>8.10.9</version>
</dependency>
<!--手机归属地定位相关-->
<dependency>
    <groupId>com.googlecode.libphonenumber</groupId>
    <artifactId>geocoder</artifactId>
    <version>2.120</version>
</dependency>
<!-- 手机运营商相关 -->
<dependency>
    <groupId>com.googlecode.libphonenumber</groupId>
    <artifactId>carrier</artifactId>
    <version>1.109</version>
</dependency>
<dependency>
    <groupId>com.googlecode.libphonenumber</groupId>
    <artifactId>prefixmapper</artifactId>
    <version>2.120</version>
</dependency>

```

2. 详见 ```LibphonenumberUtil.java``` java 文件



