# 统一的接口鉴权，利用 AOP 的方式

## 流程

1. 自定义注解 `@interface RequireSignature`
1. 在需要统一鉴权的接口上添加注解 `@RequireSignature`
1. springBoot 拦截器配置 `registry.addInterceptor(externalServiceInterceptor).addPathPatterns("/api/**");`
1. 编写拦截器，进行鉴权操作 `ExternalServiceInterceptor.java`

``` java
@RequireSignature
@PostMapping("/api/advisory")
public MyResposeData advisory(@RequestBody Map param) {
    // TODO do something
}
```
