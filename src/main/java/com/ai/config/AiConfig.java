//package com.ai.config;
//
//import org.springframework.ai.chat.client.ChatClient;
//import org.springframework.ai.chat.prompt.ChatOptions;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class AiConfig {
//
//    @Bean
//    public ChatClient chatClient() {
//        return ChatClient.builder()
//                .baseUrl("your-ai-service-url")
//                .defaultOptions(ChatOptions.builder()
//                        // .incrementalOutput(true) // 移除这行
//                        .temperature(0.7)
//                        .maxTokens(1000)
//                        .build())
//                .build();
//    }
//}
