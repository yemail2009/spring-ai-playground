package com.ai.dataCleansing.service;

import com.ai.dataCleansing.model.TVSpecification;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class LLMDataExtractionService {
    @Value("${ai.model.api.key}")
    private String apiKey;

    @Value("${ai.model.endpoint}")
    private String modelEndpoint;

    private static final String EXTRACTION_PROMPT_TEMPLATE = """
        请从以下电视机规格文本中提取结构化信息。文本内容：
        %s
        
        请按照以下JSON格式返回提取结果，只返回JSON数据：
        {
            "modelNumber": "型号",
            "brand": "品牌",
            "series": "系列",
            "screen": {
                "size": 尺寸(数字),
                "resolution": "分辨率",
                "displayType": "显示技术",
                "refreshRate": "刷新率",
                "hdrSupport": true/false
            },
            "display": {
                "colorTechnology": "色彩技术",
                "contrastRatio": 对比度,
                "viewingAngle": "可视角度",
                "brightness": "亮度"
            },
            "audio": {
                "outputPower": "输出功率",
                "speakerConfiguration": "扬声器配置",
                "audioTechnologies": ["音频技术1", "音频技术2"]
            },
            "connectivity": {
                "hdmiPorts": HDMI端口数,
                "usbPorts": USB端口数,
                "wifi": true/false,
                "bluetooth": true/false,
                "ethernet": true/false
            },
            "power": {
                "powerConsumption": "功耗",
                "standbyPower": "待机功耗"
            },
            "dimension": {
                "width": "宽度",
                "height": "高度",
                "depth": "厚度",
                "weight": "重量"
            },
            "additionalFeatures": {
                "feature1": "value1",
                "feature2": "value2"
            }
        }
        
        如果某些信息无法提取，请使用null或空值。
        """;

    public TVSpecification extractSpecifications(String textContent) {
        String prompt = String.format(EXTRACTION_PROMPT_TEMPLATE, textContent);

        // 调用大模型API
        String response = callLLMAPI(prompt);

        // 解析响应并转换为对象
        return parseLLMResponse(response);
    }

    private String callLLMAPI(String prompt) {
        // 使用HTTP客户端调用大模型API
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(modelEndpoint);

            // 构建请求体
            String requestBody = buildRequestBody(prompt);
            httpPost.setEntity(new StringEntity(requestBody, ContentType.APPLICATION_JSON));
            httpPost.setHeader("Authorization", "Bearer " + apiKey);

            HttpResponse response = httpClient.execute(httpPost);
            String responseBody = EntityUtils.toString(response.getEntity());

            return extractContentFromResponse(responseBody);
        } catch (Exception e) {
            throw new RuntimeException("调用大模型API失败", e);
        }
    }

    private TVSpecification parseLLMResponse(String response) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(response, TVSpecification.class);
        } catch (Exception e) {
            throw new RuntimeException("解析大模型响应失败", e);
        }
    }

    private String buildRequestBody(String prompt) {
        // 构建大模型API请求体（以OpenAI格式为例）
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode requestBody = mapper.createObjectNode();

        requestBody.put("model", "gpt-4");  // 或其他适合的模型
        requestBody.put("temperature", 0.1);  // 低温度确保输出稳定

        ArrayNode messages = mapper.createArrayNode();
        ObjectNode message = mapper.createObjectNode();
        message.put("role", "user");
        message.put("content", prompt);
        messages.add(message);

        requestBody.set("messages", messages);
        requestBody.put("max_tokens", 2000);

        try {
            return mapper.writeValueAsString(requestBody);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("构建请求体失败", e);
        }
    }

    private String extractContentFromResponse(String responseBody) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(responseBody);

            // 解析不同的响应格式
            if (responseBody.contains("choices")) {
                // OpenAI格式
                JsonNode choicesNode = rootNode.path("choices");
                if (choicesNode.isArray() && choicesNode.size() > 0) {
                    JsonNode messageNode = choicesNode.get(0).path("message");
                    return messageNode.path("content").asText();
                }
            } else if (responseBody.contains("result")) {
                // 百度文心等格式
                return rootNode.path("result").asText();
            } else if (responseBody.contains("data")) {
                // 其他自定义格式
                return rootNode.path("data").path("content").asText();
            }

            // 如果以上都不匹配，尝试直接提取JSON部分
            return extractJsonFromText(responseBody);

        } catch (Exception e) {
            throw new RuntimeException("解析API响应失败", e);
        }
    }

    private String extractJsonFromText(String text) {
        // 使用正则表达式提取JSON部分
        Pattern pattern = Pattern.compile("\\{.*\\}", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            return matcher.group();
        }

        return text;  // 返回原始文本
    }

    // 处理各种大模型API的响应格式
    private String handleDifferentAPIResponses(String responseBody) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            // 尝试解析为JSON
            JsonNode rootNode = mapper.readTree(responseBody);

            // 检查常见的大模型API响应格式
            if (rootNode.has("choices") && rootNode.get("choices").isArray()) {
                // OpenAI格式
                return rootNode.get("choices").get(0).get("message").get("content").asText();
            } else if (rootNode.has("output") && rootNode.get("output").has("text")) {
                // Claude格式
                return rootNode.get("output").get("text").asText();
            } else if (rootNode.has("result")) {
                // 百度文心一言格式
                return rootNode.get("result").asText();
            } else if (rootNode.has("content")) {
                // 通用格式
                return rootNode.get("content").asText();
            } else {
                // 如果无法识别格式，返回第一个文本字段
                Iterator<JsonNode> elements = rootNode.elements();
                while (elements.hasNext()) {
                    JsonNode element = elements.next();
                    if (element.isTextual() && element.asText().length() > 50) {
                        return element.asText();
                    }
                }
            }
        } catch (Exception e) {
            // 如果不是JSON，直接返回
        }

        return responseBody;
    }

    // 备用的API调用方法（使用RestTemplate）
    private String callLLMAPIWithRestTemplate(String prompt) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            // 构建请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "gpt-4");
            requestBody.put("messages", List.of(Map.of("role", "user", "content", prompt)));
            requestBody.put("temperature", 0.1);
            requestBody.put("max_tokens", 2000);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            // 发送请求
            ResponseEntity<Map> response = restTemplate.exchange(
                    modelEndpoint,
                    HttpMethod.POST,
                    request,
                    Map.class
            );

            // 解析响应
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                List<Map<String, Object>> choices = (List<Map<String, Object>>) body.get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    return (String) message.get("content");
                }
            }

            throw new RuntimeException("API调用失败: " + response.getStatusCode());

        } catch (Exception e) {
            throw new RuntimeException("调用大模型API失败", e);
        }
    }
}
