package com.ai.qianwen;

import com.ai.dataCleansing.model.TVSpecification;
import com.ai.dataCleansing.service.DashScopeLLMService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// DashScopeAPITester.java
@Component
@Slf4j
public class DashScopeAPITester {

    @Value("${ai.dashscope.api-key:}")
    private String apiKey;

    @Autowired
    private DashScopeLLMService dashScopeLLMService;

    /**
     * 测试API连接
     */
    public void testConnection() {
        log.info("开始测试DashScope API连接...");

        if (apiKey == null || apiKey.trim().isEmpty()) {
            log.error("API密钥未配置!");
            return;
        }

        try {
            // 测试简单的请求
            String testPrompt = "你好，请回复'连接成功'";

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey.trim());

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "qwen-turbo");

            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of("role", "user", "content", testPrompt));
            requestBody.put("input", Map.of("messages", messages));

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("result_format", "text");
            parameters.put("temperature", 0.1);
            requestBody.put("parameters", parameters);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            log.info("发送测试请求到DashScope...");

            ResponseEntity<Map> response = restTemplate.exchange(
                    "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation",
                    HttpMethod.POST,
                    request,
                    Map.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("✅ DashScope API连接测试成功!");

                // 解析响应
                Map<String, Object> responseBody = response.getBody();
                if (responseBody != null) {
                    Map<String, Object> output = (Map<String, Object>) responseBody.get("output");
                    if (output != null) {
                        List<Map<String, Object>> choices = (List<Map<String, Object>>) output.get("choices");
                        if (choices != null && !choices.isEmpty()) {
                            Map<String, Object> choice = choices.get(0);
                            Map<String, Object> message = (Map<String, Object>) choice.get("message");
                            String content = (String) message.get("content");
                            log.info("API响应内容: {}", content);
                        }
                    }
                }
            } else {
                log.error("❌ DashScope API连接测试失败，状态码: {}", response.getStatusCode());
                log.error("响应头: {}", response.getHeaders());
            }

        } catch (Exception e) {
            log.error("❌ DashScope API连接测试异常", e);
        }
    }

    /**
     * 测试电视机规格提取
     */
    public void testTVSpecificationExtraction() {
        log.info("开始测试电视机规格提取...");

        String testText = """
            索尼电视 KD-65X90J
            65英寸4K超高清OLED电视
            支持HDR10，刷新率120Hz
            有4个HDMI 2.1接口，2个USB 3.0接口
            支持WiFi 6和蓝牙5.2
            功耗150W，待机功耗0.5W
            尺寸：1450×835×70mm（不含底座）
            重量：25.5kg
            """;

        try {
            TVSpecification result = dashScopeLLMService.extractTVSpecifications(testText);

            log.info("✅ 电视机规格提取测试成功!");
            log.info("提取结果:");
            log.info("  型号: {}", result.getModelNumber());
            log.info("  品牌: {}", result.getBrand());

            if (result.getScreen() != null) {
                log.info("  屏幕尺寸: {}英寸", result.getScreen().getSize());
                log.info("  分辨率: {}", result.getScreen().getResolution());
                log.info("  显示技术: {}", result.getScreen().getDisplayType());
            }

            if (result.getConnectivity() != null) {
                log.info("  HDMI端口: {}个", result.getConnectivity().getHdmiPorts());
                log.info("  USB端口: {}个", result.getConnectivity().getUsbPorts());
                log.info("  支持WiFi: {}", result.getConnectivity().getWifi());
            }

        } catch (Exception e) {
            log.error("❌ 电视机规格提取测试失败", e);
        }
    }

    /**
     * 检查API密钥格式
     */
    public void checkApiKeyFormat() {
        log.info("检查API密钥格式...");

        if (apiKey == null) {
            log.error("API密钥为空!");
            return;
        }

        log.info("API密钥长度: {} 字符", apiKey.length());

        // DashScope API密钥通常以 sk- 开头
        if (apiKey.startsWith("sk-")) {
            log.info("✅ API密钥格式正确（以sk-开头）");
        } else {
            log.warn("⚠️ API密钥可能不是标准的DashScope格式");
        }

        // 显示前10个字符和后10个字符（用于验证）
        if (apiKey.length() > 20) {
            String preview = apiKey.substring(0, 10) + "..." + apiKey.substring(apiKey.length() - 10);
            log.info("API密钥预览: {}", preview);
        }
    }
}
