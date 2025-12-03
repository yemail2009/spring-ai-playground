package com.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "aliyun.bailian")
public class AliyunBailianConfig {
    private String apiKey;
    private String endpoint = "https://dashscope.aliyuncs.com";
}
