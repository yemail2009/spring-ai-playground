package com.ai.service;

import com.ai.config.AliyunBailianConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Service
@Slf4j
public class AliyunBailianService {

    @Autowired
    private AliyunBailianConfig config;

    @Autowired
    private RestTemplate restTemplate;

    private static final String API_URL = "/api/v1/services/aigc/text-generation/generation";

    /**
     * 同步调用通义千问
     */
    public String callQwen(String prompt) {
        try {
            String url = config.getEndpoint() + API_URL;

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "qwen-turbo");

            Map<String, Object> input = new HashMap<>();
            input.put("prompt", prompt);
            requestBody.put("input", input);

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("result_format", "message");
            requestBody.put("parameters", parameters);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + config.getApiKey());
            headers.set("X-DashScope-SSE", "disable");

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

            return parseResponse(response.getBody());

        } catch (Exception e) {
            log.error("调用阿里云百炼API失败", e);
            throw new RuntimeException("API调用失败: " + e.getMessage());
        }
    }

    /**
     * 流式调用
     */
    public void callQwenStream(String prompt, Consumer<String> callback) {
        try {
            String url = config.getEndpoint() + API_URL;

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "qwen-turbo");

            Map<String, Object> input = new HashMap<>();
            input.put("prompt", prompt);
            requestBody.put("input", input);

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("stream", true);
            parameters.put("incremental_output", true);
            requestBody.put("parameters", parameters);

            // 使用WebClient进行流式调用
            WebClient.builder()
                    .baseUrl(config.getEndpoint())
                    .defaultHeader("Authorization", "Bearer " + config.getApiKey())
                    .defaultHeader("Content-Type", "application/json")
                    .defaultHeader("X-DashScope-SSE", "enable")
                    .build()
                    .post()
                    .uri(API_URL)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToFlux(String.class)
                    .subscribe(chunk -> {
                        String text = parseStreamResponse(chunk);
                        if (text != null) {
                            callback.accept(text);
                        }
                    });

        } catch (Exception e) {
            log.error("流式调用失败", e);
            throw new RuntimeException("流式调用失败: " + e.getMessage());
        }
    }

    private String parseResponse(Map<String, Object> response) {
        try {
            Map<String, Object> output = (Map<String, Object>) response.get("output");
            if (output != null) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) output.get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    if (message != null) {
                        return (String) message.get("content");
                    }
                }
            }
            return "未获取到有效响应";
        } catch (Exception e) {
            log.error("解析响应失败", e);
            return "解析响应失败";
        }
    }

    private String parseStreamResponse(String chunk) {
        try {
            if (chunk.startsWith("data: ")) {
                String jsonStr = chunk.substring(6);
                if (jsonStr.equals("[DONE]")) {
                    return null;
                }

                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> data = mapper.readValue(jsonStr, Map.class);

                if (data.containsKey("output")) {
                    Map<String, Object> output = (Map<String, Object>) data.get("output");
                    if (output != null && output.containsKey("choices")) {
                        List<Map<String, Object>> choices = (List<Map<String, Object>>) output.get("choices");
                        if (choices != null && !choices.isEmpty()) {
                            Map<String, Object> choice = choices.get(0);
                            if (choice.containsKey("message")) {
                                Map<String, Object> message = (Map<String, Object>) choice.get("message");
                                return (String) message.get("content");
                            }
                        }
                    }
                }
            }
            return null;
        } catch (Exception e) {
            log.error("解析流式响应失败: {}", chunk, e);
            return null;
        }
    }
}
