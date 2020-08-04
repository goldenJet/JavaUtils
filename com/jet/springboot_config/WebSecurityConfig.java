package com.jet.springboot_config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.util.StringUtils;

/**
 * Created by jet.chen on 2017/2/23.
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    UserAuthenticationService userAuthenticationService;

    @Autowired
    SuccessLoginHandler successLoginHandler;

    @Autowired
    Environment environment;

    @Autowired
    JetAuthProvider jetAuthProvider;

    @Autowired
    JetLogoutHandler jetLogoutHandler;


    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeRequests()

                //所有静态资源及mobile资源都忽略权限控制
                .antMatchers("/resources/**", "/staff/forgotPassword/**", "/sms/sendVaildateCode", "/sms/login", "/auth/**"
                )
                .permitAll()
                //任意请求角色必须为用户或者管理员
//                .anyRequest().hasAnyRole("USER", "ADMIN")
                .anyRequest().authenticated()

                .and()
                //下面标示的路径不用进行CSRF验证
                .csrf().disable()
                //配置表单登陆
                .formLogin()
                //配置登陆页面路径并允许所有人访问登陆页面
                .loginPage("/login").permitAll()
                .successHandler(successLoginHandler)
                //登陆成功时的处理类
                .and()
                //配置登出页面路径并允许所有人访问登陆页面
                .logout().logoutUrl("/logout").permitAll()
                // 登出成功后的处理页
                .addLogoutHandler(jetLogoutHandler)
                .and()
                .rememberMe()
                .rememberMeParameter("remember-me")
                .tokenValiditySeconds(120*60*1000) // 2小时
                .rememberMeCookieName("RM_COOKIE")
                // 保证跨域的过滤器首先触发
                .and().addFilterBefore(new SelfRequestFilter(), ChannelProcessingFilter.class) 
                .headers().frameOptions().disable()
                // 禁用 STS
                .httpStrictTransportSecurity().disable();

    }


    /**
     * @param auth
     * @throws Exception 权限配置管理
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        /*String encrypt = environment.getProperty("jet.password.encrypt");
        String strength = environment.getProperty("jet.password.secret");
        PasswordEncoder passwordEncoder = null;
        switch (encrypt.toUpperCase()) {
            case "SCRYPT":
                passwordEncoder = new SCryptPasswordEncoder();
                break;
            case "BCRYPT":
                int sha_strength = 1;
                if (!StringUtils.isEmpty(strength)) {
                    try {
                        sha_strength = Integer.parseInt(strength);
                    } catch (NumberFormatException e) {

                    }
                }
                passwordEncoder = new BCryptPasswordEncoder(sha_strength);
                break;
            case "SHA-256":
                passwordEncoder = new StandardPasswordEncoder(strength);
                break;
            case "PBKDF2":
                if (!StringUtils.isEmpty(strength)) {
                    passwordEncoder = new Pbkdf2PasswordEncoder(strength);
                }
                break;
            default:
                passwordEncoder = NoOpPasswordEncoder.getInstance();

        }
        auth.userDetailsService(userAuthenticationService).passwordEncoder(passwordEncoder);*/

        // 自定义验证规则
        // public class JetAuthProvider implements AuthenticationProvider
        auth.authenticationProvider(jetAuthProvider);
    }


}
