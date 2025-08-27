package com.turfbooking.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web configuration for the Turf Booking System
 * Handles CORS, static resources, and view controllers
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Static resources from classpath
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(3600);
        
        // Frontend resources
        registry.addResourceHandler("/css/**")
                .addResourceLocations("file:./frontend/css/", "classpath:/static/css/")
                .setCachePeriod(3600);
        
        registry.addResourceHandler("/js/**")
                .addResourceLocations("file:./frontend/js/", "classpath:/static/js/")
                .setCachePeriod(3600);
        
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:./frontend/images/", "classpath:/static/images/")
                .setCachePeriod(3600);

        // Uploaded images
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:./uploads/")
                .setCachePeriod(3600);
        
        // Root level HTML files
        registry.addResourceHandler("/*.html")
                .addResourceLocations("file:./frontend/", "classpath:/static/")
                .setCachePeriod(0); // Don't cache HTML files
    }
    
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Map root to index.html
        registry.addViewController("/").setViewName("forward:/index.html");
        registry.addViewController("/home").setViewName("forward:/index.html");
        
        // Map other pages
        registry.addViewController("/search").setViewName("forward:/search.html");
        registry.addViewController("/turf-details").setViewName("forward:/turf-details.html");
        registry.addViewController("/tournaments").setViewName("forward:/tournaments.html");
        registry.addViewController("/about").setViewName("forward:/about.html");
        registry.addViewController("/login").setViewName("forward:/login.html");
        registry.addViewController("/register").setViewName("forward:/register.html");
        registry.addViewController("/dashboard").setViewName("forward:/dashboard.html");
        registry.addViewController("/profile").setViewName("forward:/profile.html");
        registry.addViewController("/bookings").setViewName("forward:/bookings.html");
        registry.addViewController("/transactions").setViewName("forward:/transactions.html");
        registry.addViewController("/admin").setViewName("forward:/admin.html");
    }
}