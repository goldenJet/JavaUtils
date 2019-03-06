# 权限校验模板 - 利用cookie
## 流程
1. 会经过过滤器和拦截器
1. 过滤器会过滤一些黑名单等，拦截器会进行权限的校验
1. 权限校验通过则会将用户信息存入ThreadLocal
1. 在拦截器执行的末端，由于使用的是模板引擎，所以在给模板的model增加一个用户信息的参数
1. 记得要在```XXX extends WebMvcConfigurerAdapter```这个配置类中添加配置
``` java
@Override
public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(actionInterceptor);
    // 移动端权限校验
    registry.addInterceptor(permissionHandle).addPathPatterns("/m/**")
            .excludePathPatterns("/m/index/*", "/m/ts/*");
    super.addInterceptors(registry);
}
```
