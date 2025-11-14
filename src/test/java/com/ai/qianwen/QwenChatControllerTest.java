package com.ai.qianwen;

import com.ai.config.RestTemplateConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
@Import(RestTemplateConfig.class) // 显式导入配置类
public class QwenChatControllerTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired(required = true)
    private RestTemplate restTemplate;

    @Test
    public void chat() {
        System.out.println(restTemplate);
    }
}