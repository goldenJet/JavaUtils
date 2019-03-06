package com.jet.springboot_vonfig.permission;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName: MobileFilterConfig
 * @Description: 手机端过滤器注册
 * @Author: Jet.Chen
 * @Date: 2019/2/14 18:19
 * @Version: 1.0
 **/
@Configuration
public class MobileFilterConfig {

    @Bean
    public FilterRegistrationBean registFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new MobileFilter());
        registration.addUrlPatterns("/m/**");
        registration.setName("mobileFilter");
        registration.setOrder(1);
        return registration;
    }

}
