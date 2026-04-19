package com.example.sportsshop.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("forward:/login.html");
        registry.addViewController("/shop").setViewName("forward:/shop.html");
        registry.addViewController("/cart").setViewName("forward:/cart.html");
        registry.addViewController("/history").setViewName("forward:/history.html");
        registry.addViewController("/").setViewName("forward:/shop.html");
        registry.addViewController("/seller").setViewName("forward:/seller.html");
    }
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}
