package com.jet.springboot_config;

import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.*;

import java.util.Collection;
import java.util.List;

/**
 * Created by jet.chen on 2017/2/17.
 */
@Configuration
@EnableAsync
@EnableWebMvc
@Log4j
@ComponentScan(basePackages = {"com.demo"})
public class WebConfig extends WebMvcConfigurerAdapter {

    @Autowired
    ActionService actionService;

    @Autowired
    ActionInterceptor actionInterceptor;

//    @Bean
//    @Primary
//    public ObjectMapper objectMapper() {
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
//        return objectMapper;
//    }


    /**
     * 注册资源处理，将指定URL映射到实际的容器目录下
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**").addResourceLocations("classpath:/static/");
        super.addResourceHandlers(registry);
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        for (HttpMessageConverter httpMessageConverter : converters) {
            if (httpMessageConverter instanceof MappingJackson2HttpMessageConverter) {
                MappingJackson2HttpMessageConverter messageConverter = (MappingJackson2HttpMessageConverter) httpMessageConverter;
                messageConverter.getObjectMapper().configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            }
        }
        super.extendMessageConverters(converters);
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        QueryCondition queryCondition = new QueryCondition("windowType", QueryCondition.Condition.EQUAL, WindowType.PAGE);
        Collection<Action> actions = null;
        try {
            actions = actionService.searchActionByCondition(queryCondition);
            log.info("addViewControllers Totle Number:" + actions.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (Action action : actions) {
            log.info("Add Acton To View Controller------>:" + action.getType() + "/" + action.getName());
            registry.addViewController(action.getType() + "/" + action.getName()).setViewName(action.getUrl());
        }
        super.addViewControllers(registry);
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(actionInterceptor);
//        registry.addInterceptor(urlInterceptor);
        super.addInterceptors(registry);
    }


}
