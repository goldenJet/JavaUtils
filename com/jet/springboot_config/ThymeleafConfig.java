package com.jet.springboot_config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.TemplateResolver;

/**
 * Created by jet.chen on 2017/2/17.
 * 用作 返回html静态页面渲染使用（和properties中的配置是两回事）
 */
@Configuration
public class ThymeleafConfig {
    @Autowired
    TemplateEngine templateEngine;


    @Bean
    InitializingBean addResolverToTemplateEngine() {
        return () -> {
            TemplateResolver resolver = new TemplateResolver();
            resolver.setResourceResolver(new ByteArrayResourceResolver());
            resolver.setPrefix(null);
            resolver.setSuffix(null);
            resolver.setCacheable(false);
            resolver.setTemplateMode("HTML5");
            resolver.setCharacterEncoding("utf-8");
            templateEngine.addTemplateResolver(resolver);
        };
    }


}
