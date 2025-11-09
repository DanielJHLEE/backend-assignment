package com.allra.backend.global.config;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import reactor.netty.http.client.HttpClient;

/**
 * WebClientConfig
 * 
 * 외부 API 호출(WebClient) 전용 설정 클래스.
 * - Mock API (webclient.mock-base-url)
 * - 향후 실제 PG사 연동 시 Base URL만 교체 가능.
 */
@Slf4j
@Configuration
public class WebClientConfig {

    /**
     * application.yml → webclient.mock-base-url 값 주입
     * 환경변수 MOCK_BASE_URL이 설정되어 있으면 우선 적용됨.
     */
    @Value("${webclient.mock-base-url}")
    private String mockBaseUrl;

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        log.info("WebClient Base URL: {}", mockBaseUrl);  // 로그로 값 확인

        // HTTP 타임아웃 설정
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .responseTimeout(Duration.ofSeconds(10))
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(10, TimeUnit.SECONDS))
                                .addHandlerLast(new WriteTimeoutHandler(10, TimeUnit.SECONDS)));

        // 대용량 응답 시 버퍼 제한 해제
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(2 * 1024 * 1024)) // 2MB
                .build();

        log.info("WebClient Base URL: {}", mockBaseUrl);

        return builder
                .baseUrl(mockBaseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .exchangeStrategies(strategies)
                .filter(logRequest())   // 요청 로깅
                .filter(logResponse())  // 응답 로깅
                .build();
    }

    /** 요청 로깅 필터 */
    private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(request -> {
            log.info("[WebClient] Request: {} {}", request.method(), request.url());
            return reactor.core.publisher.Mono.just(request);
        });
    }

    /** 응답 로깅 필터 */
    private ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(response -> {
            log.info("[WebClient] Response: {}", response.statusCode());
            return reactor.core.publisher.Mono.just(response);
        });
    }
}
