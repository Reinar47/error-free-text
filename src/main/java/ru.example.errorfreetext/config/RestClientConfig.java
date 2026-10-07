package ru.example.errorfreetext.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${yandex.speller.url}")
    private String yandexSpellerUrl;

    @Value("${yandex.speller.connect-timeout-ms:5000}")
    private int connectTimeoutMs;

    @Value("${yandex.speller.read-timeout-ms:10000}")
    private int readTimeoutMs;

    @Bean
    public RestClient yandexSpellerRestClient() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(connectTimeoutMs);
        factory.setReadTimeout(readTimeoutMs);

        return RestClient.builder()
                .baseUrl(yandexSpellerUrl)
                .requestFactory(factory)
                .build();
    }
}