package com.goldenfly.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    /**
     * Bean RestTemplate pour les appels HTTP externes
     * Utilisé par WaveService et OrangeMoneyService
     */
    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10000); // 10 secondes
        factory.setReadTimeout(30000);    // 30 secondes

        return new RestTemplate(factory);
    }
}