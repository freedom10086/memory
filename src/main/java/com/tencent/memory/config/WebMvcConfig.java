package com.tencent.memory.config;

import com.tencent.memory.filter.AuthFilter;
import com.tencent.memory.filter.PagingFilter;
import com.tencent.memory.interceptor.ExecuteTimeInterceptor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ExecuteTimeInterceptor())
                .addPathPatterns("/**");
    }

    @Bean
    public FilterRegistrationBean authFilterRegistration() {
        FilterRegistrationBean<AuthFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new AuthFilter());
        registration.addUrlPatterns("/galleries/*", "/files/*");
        registration.setName("AuthFilter");
        registration.setOrder(1);
        return registration;
    }

    @Bean
    public FilterRegistrationBean pagingFilterRegistration() {
        FilterRegistrationBean<PagingFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new PagingFilter());
        registration.addUrlPatterns("/galleries/", "/files/");
        registration.setName("PagingFilter");
        registration.setOrder(2);
        return registration;
    }

}