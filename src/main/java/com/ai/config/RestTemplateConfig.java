package com.ai.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.time.Duration;

@Configuration
public class RestTemplateConfig implements Autowired {

    @Bean  // 确保有这个注解
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                // 不对400以上的状态码抛出异常，让业务层处理
                return false;
            }
        });
        return restTemplate;
        // 或者你的自定义配置
        // return new RestTemplateBuilder().build();
    }

//    @Bean
//    public RestTemplate restTemplate(RestTemplateBuilder builder) {
//        // 创建带连接池的 RestTemplate
//        RestTemplate restTemplate = builder
//                .requestFactory(this::clientHttpRequestFactory)
//                .additionalInterceptors(clientHttpRequestInterceptor())
//                .build();
//
//        // 设置消息转换器（可选）
//        // restTemplate.setMessageConverters(getMessageConverters());
//
//        return restTemplate;
//    }
//
//    @Bean
//    public ClientHttpRequestFactory clientHttpRequestFactory() {
//        // 使用 HttpComponentsClientHttpRequestFactory 替代过时的 SimpleClientHttpRequestFactory
//        return new HttpComponentsClientHttpRequestFactory(httpClient());
//    }
//
//    @Bean
//    public HttpClient httpClient() {
//        // 连接池配置
//        PoolingHttpClientConnectionManager connectionManager =
//                new PoolingHttpClientConnectionManager();
//        connectionManager.setMaxTotal(100); // 最大连接数
//        connectionManager.setDefaultMaxPerRoute(20); // 每个路由的最大连接数
//
//        // 连接配置
//        ConnectionConfig connectionConfig = ConnectionConfig.custom()
//                .setConnectTimeout(Timeout.ofSeconds(10)) // 连接超时
//                .setSocketTimeout(Timeout.ofSeconds(30))  // 读取超时
//                .build();
//
//        // 请求配置
//        RequestConfig requestConfig = RequestConfig.custom()
//                .setConnectionRequestTimeout(Timeout.ofSeconds(5)) // 从连接池获取连接的超时时间
//                .build();
//
//        connectionManager.setDefaultConnectionConfig(connectionConfig);
//
//        return HttpClientBuilder.create()
//                .setConnectionManager(connectionManager)
//                .setDefaultRequestConfig(requestConfig)
//                .build();
//    }
//
//    @Bean
//    public ClientHttpRequestInterceptor clientHttpRequestInterceptor() {
//        return (request, body, execution) -> {
//            // 可以在这里添加通用的请求头等
//            // request.getHeaders().add("User-Agent", "MyApp/1.0");
//            return execution.execute(request, body);
//        };
//    }
//
//    // 如果需要多个 RestTemplate 实例，可以这样配置
//    @Bean(name = "fastRestTemplate")
//    public RestTemplate fastRestTemplate() {
//        return new RestTemplateBuilder()
//                .setConnectTimeout(Duration.ofSeconds(5))
//                .setReadTimeout(Duration.ofSeconds(10))
//                .build();
//    }
//
//    @Bean(name = "slowRestTemplate")
//    public RestTemplate slowRestTemplate() {
//        return new RestTemplateBuilder()
//                .setConnectTimeout(Duration.ofSeconds(30))
//                .setReadTimeout(Duration.ofSeconds(60))
//                .build();
//    }

    @Override
    public boolean required() {
        return false;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return null;
    }
}
