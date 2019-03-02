# IP解析工具
## 流程
1. 直接请求淘宝的IP库
1. 设置超时时间，如果超时，则请求本地的纯真数据库
## 文件说明
1. IPVO.java 为封装的响应实体类
1. OkHttpUtil.java 为http请求工具类
1. NetUtil.java 的 ```public static IPVO getIpInfo(String ip)``` 为主要入口
1. IPAddressUtils.java 为纯真库的解析方法
    >注意：此文件内的部分注解是可以删除的，引用的原因是因为我区分了配置文件，在此是可以完全脱离 Spring 的托管而使用
1. qqwry.dat 是纯真数据库文件，想要更新则可以手动去下载