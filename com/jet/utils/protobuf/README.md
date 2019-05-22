**protobuf**

## ideal 插件
Protobuf Support

## maven
``` xml
<!-- protobuf -->
<dependency>     
    <groupId>com.google.protobuf</groupId>     
    <artifactId>protobuf-java</artifactId>     
    <version>3.7.1</version>
</dependency>
```

## 使用
- step1

建立 JetProtos.proto 文件

- step2

命令行编译生成 PersonTestProtos.java
```
protoc -I=./ --java_out=./ ./JetProtos.proto
或
protoc -proto_path=./ --java_out=./ ./JetProtos.proto
```

- step3

使用见测试类 ProtoTest.java



