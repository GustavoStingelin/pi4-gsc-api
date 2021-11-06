package com.gs.pi4.api.config;


import java.util.List;

import com.gs.pi4.api.app.serialization.converter.YamlJackson2HttpMessageConverter;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer{

    private static final MediaType MEDIA_TYPE_YAML = MediaType.valueOf("application/x-yaml");

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new YamlJackson2HttpMessageConverter());
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
        .allowedMethods("GET", "POST", "HEAD", "PUT", "PATCH", "PUT", "DELETE")
        .allowedOrigins("http://localhost:3000", "http://26.141.51.43:3000", "http://25.6.75.20:3000", "http://25.1.175.3:3000")
        .allowCredentials(true);
    }
    
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {

        // via header
        configurer
            .favorParameter(false)
            .ignoreAcceptHeader(false)
            .useRegisteredExtensionsOnly(false)
            .defaultContentType(MediaType.APPLICATION_JSON)
            .mediaType("json", MediaType.APPLICATION_JSON)
            .mediaType("xml", MediaType.APPLICATION_XML)
            .mediaType("x-yaml", MEDIA_TYPE_YAML);
    }
}
