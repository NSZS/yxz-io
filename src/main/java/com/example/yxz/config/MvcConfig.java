//package com.example.yxz.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.converter.HttpMessageConverter;
//import org.springframework.http.converter.StringHttpMessageConverter;
//import org.springframework.web.servlet.config.annotation.EnableWebMvc;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
//
//import java.nio.charset.StandardCharsets;
//import java.util.List;
//
///**
// * @author yawn < http://jvm123.com >
// */
//@Configuration
//@EnableWebMvc
//class MvcConfig extends WebMvcConfigurerAdapter {
//
//    @Override
//    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
//        converters.add(new StringHttpMessageConverter(StandardCharsets.UTF_8));
//        super.configureMessageConverters(converters);
//    }
//}