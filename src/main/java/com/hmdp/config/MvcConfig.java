package com.hmdp.config;

import com.hmdp.utils.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .excludePathPatterns(
                        "/shop/**", // 排除店铺相关的请求
                        "/shop-type/**", // 排除店铺类型相关的请求
                        "/upload/**", // 排除文件上传相关的请求
                        "/voucher/**", // 排除优惠券相关的请求
                        "/blog/hot", // 排除获取热门博客的请求
                        "/user/code", // 排除获取验证码的请求
                        "/user/login" // 排除登录请求
                );
    }
}
