package com.jet.config;

import com.jet.annotation.ExternalServiceInterceptor;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@EnableAsync
@EnableWebMvc
@Log4j
@ComponentScan(basePackages = {"com.jet"})
public class WebConfig extends WebMvcConfigurerAdapter {

    @Autowired
    ExternalServiceInterceptor externalServiceInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 直接接口添加拦截器
        registry.addInterceptor(externalServiceInterceptor).addPathPatterns("/api/**");
        super.addInterceptors(registry);
    }
}
