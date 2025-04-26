package com.rstep1.post_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.rstep1.post_service.feign.UserServiceErrorDecoder;

import feign.codec.ErrorDecoder;

@Configuration
public class FeignConfiguration {
    @Bean
    public ErrorDecoder errorDecoder() {
        return new UserServiceErrorDecoder();
    }
}
