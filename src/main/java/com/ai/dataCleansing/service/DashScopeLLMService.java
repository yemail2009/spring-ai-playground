package com.ai.dataCleansing.service;

import com.ai.dataCleansing.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;

// DashScopeLLMService.java
@Service
@Slf4j
public class DashScopeLLMService {

    @Value("${ai.dashscope.api-key:}")
    private String apiKey;

    @Value("${ai.dashscope.endpoint:https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation}")
    private String endpoint;

    @Value("${ai.dashscope.model:qwen-turbo}")
    private String model;

    @Value("${ai.dashscope.temperature:0.1}")
    private double temperature;

    @Value("${ai.dashscope.max-tokens:2000}")
    private int maxTokens;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public DashScopeLLMService() {
        this.restTemplate = createRestTemplate();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * 创建专用的 RestTemplate
     */
    private RestTemplate createRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        // 设置请求工厂
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(30000);
        factory.setConnectTimeout(60000);
        restTemplate.setRequestFactory(factory);

        // 添加拦截器
        restTemplate.setInterceptors(Collections.singletonList(new HeaderInterceptor()));

        return restTemplate;
    }

    /**
     * 提取电视机规格信息
     */
    public TVSpecification extractTVSpecifications(String text) {
        try {
            log.info("开始使用DashScope提取电视机规格信息，文本长度: {}", text.length());

            String prompt = buildExtractionPrompt(text);
            String response = callDashScopeAPI(prompt);

            TVSpecification result = parseResponse(response);
            log.info("电视机规格信息提取成功，型号: {}", result.getModelNumber());

            return result;

        } catch (Exception e) {
            log.error("提取电视机规格信息失败", e);
            throw new RuntimeException("AI提取失败: " + e.getMessage(), e);
        }
    }

    /**
     * 构建适合通义千问的提取提示词
     */
    private String buildExtractionPrompt(String text) {
        return String.format("""
            你是一个专业的电视机规格信息提取专家。请从以下文本中提取电视机规格信息，并以严格的JSON格式返回。

            【文本内容】：
            %s

            【需要提取的信息】：
            1. 型号 (modelNumber) - 电视机的具体型号
            2. 品牌 (brand) - 电视机的品牌，如：索尼、三星、LG等
            3. 屏幕尺寸 (screen.size) - 单位：英寸，只返回数字
            4. 分辨率 (screen.resolution) - 如：3840x2160、1920x1080
            5. 显示技术 (screen.displayType) - 如：OLED、QLED、LED、Mini-LED
            6. 刷新率 (screen.refreshRate) - 如：120Hz、60Hz
            7. 是否支持HDR (screen.hdrSupport) - true或false
            8. HDMI端口数量 (connectivity.hdmiPorts) - 数字
            9. USB端口数量 (connectivity.usbPorts) - 数字
            10. 是否支持WiFi (connectivity.wifi) - true或false
            11. 是否支持蓝牙 (connectivity.bluetooth) - true或false
            12. 功耗 (power.powerConsumption) - 如：150W
            13. 待机功耗 (power.standbyPower) - 如：0.5W
            14. 宽度 (dimension.width) - 如：1450mm
            15. 高度 (dimension.height) - 如：835mm
            16. 厚度 (dimension.depth) - 如：70mm
            17. 重量 (dimension.weight) - 如：25.5kg

            【JSON格式要求】：
            {
              "modelNumber": "字符串或null",
              "brand": "字符串或null",
              "screen": {
                "size": "数字或null",
                "resolution": "字符串或null",
                "displayType": "字符串或null",
                "refreshRate": "字符串或null",
                "hdrSupport": "布尔值或null"
              },
              "connectivity": {
                "hdmiPorts": "数字或null",
                "usbPorts": "数字或null",
                "wifi": "布尔值或null",
                "bluetooth": "布尔值或null"
              },
              "power": {
                "powerConsumption": "字符串或null",
                "standbyPower": "字符串或null"
              },
              "dimension": {
                "width": "字符串或null",
                "height": "字符串或null",
                "depth": "字符串或null",
                "weight": "字符串或null"
              }
            }

            【重要说明】：
            1. 只返回JSON对象，不要有任何额外的解释
            2. 如果信息不存在，使用null
            3. 确保JSON格式正确
            4. 品牌名称统一使用英文大写，如：SONY、SAMSUNG、LG
            5. 分辨率统一为"宽度x高度"格式
            """, text);
    }

    /**
     * 调用 DashScope API
     */
    private String callDashScopeAPI(String prompt) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new RuntimeException("DashScope API密钥未配置");
        }

        try {
            // 构建符合DashScope格式的请求
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);

            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of(
                    "role", "user",
                    "content", prompt
            ));
            requestBody.put("input", Map.of("messages", messages));

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("result_format", "text");
            parameters.put("temperature", temperature);
            parameters.put("max_tokens", maxTokens);
            parameters.put("top_p", 0.8);
            requestBody.put("parameters", parameters);

            // 构建请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey.trim());
            headers.set("X-DashScope-SSE", "disable");

            // 记录请求信息（不记录完整API密钥）
            log.debug("调用DashScope API，模型: {}, 端点: {}", model, endpoint);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            // 发送请求
            ResponseEntity<Map> response = restTemplate.exchange(
                    endpoint,
                    HttpMethod.POST,
                    request,
                    Map.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.warn("DashScope API 成功返回，状态码: {}, body 内容为 {}", response.getStatusCode(), response.getBody());
                return extractContentFromResponse(response.getBody());
            } else {
                log.error("DashScope API调用失败，状态码: {}", response.getStatusCode());
                throw new RuntimeException("API调用失败: " + response.getStatusCode());
            }

        } catch (HttpClientErrorException e) {
            log.error("DashScope HTTP客户端错误: {}", e.getStatusCode());
            log.error("错误响应体: {}", e.getResponseBodyAsString());
            throw new RuntimeException("HTTP错误: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("调用DashScope API失败", e);
            throw new RuntimeException("调用AI服务失败: " + e.getMessage(), e);
        }
    }

    /**
     * 从响应中提取内容
     */
    private String extractContentFromResponse(Map<String, Object> responseBody) {
        try {
            log.debug("DashScope API原始响应: {}", objectMapper.writeValueAsString(responseBody));

            // 方法1：尝试从output.text提取
            Map<String, Object> output = (Map<String, Object>) responseBody.get("output");
            if (output != null) {
                // 新版API可能在output.text中
                String text = (String) output.get("text");
                if (text != null && !text.trim().isEmpty()) {
                    log.debug("从output.text提取到内容，长度: {}", text.length());
                    return text.trim();
                }

                // 或者在output.choices中
                List<Map<String, Object>> choices = (List<Map<String, Object>>) output.get("choices");
                if (choices != null && !choices.isEmpty()) {
                    for (Map<String, Object> choice : choices) {
                        Map<String, Object> message = (Map<String, Object>) choice.get("message");
                        if (message != null) {
                            String content = (String) message.get("content");
                            if (content != null && !content.trim().isEmpty()) {
                                log.debug("从output.choices提取到内容，长度: {}", content.length());
                                return content.trim();
                            }
                        }
                    }
                }
            }

            // 方法2：尝试从顶层提取
            String text = (String) responseBody.get("text");
            if (text != null && !text.trim().isEmpty()) {
                log.debug("从顶层text字段提取到内容，长度: {}", text.length());
                return text.trim();
            }

            // 方法3：尝试从data字段提取
            String data = (String) responseBody.get("data");
            if (data != null && !data.trim().isEmpty()) {
                log.debug("从data字段提取到内容，长度: {}", data.length());
                return data.trim();
            }

            // 如果没有找到内容，记录完整的响应体以便调试
            log.error("无法从响应中提取内容，响应结构: {}", responseBody.keySet());
            throw new RuntimeException("无法从API响应中提取内容，响应格式可能已更改");

        } catch (Exception e) {
            log.error("解析DashScope响应失败", e);
            throw new RuntimeException("解析API响应失败: " + e.getMessage(), e);
        }
    }

    /**
     * 解析响应为TVSpecification对象
     */
    private TVSpecification parseResponse(String response) {
        try {
            // 清理响应文本，提取JSON部分
            String jsonContent = cleanAndExtractJson(response);
            log.debug("清理后的JSON内容: {}", jsonContent);

            // 解析JSON
            JsonNode rootNode = objectMapper.readTree(jsonContent);

            TVSpecification spec = new TVSpecification();

            // 解析基本字段
            spec.setModelNumber(getText(rootNode, "modelNumber"));
            spec.setBrand(getText(rootNode, "brand"));

            // 解析屏幕规格
            JsonNode screenNode = rootNode.path("screen");
            if (!screenNode.isMissingNode()) {
                ScreenSpec screen = new ScreenSpec();
                screen.setSize(getDouble(screenNode, "size"));
                screen.setResolution(getText(screenNode, "resolution"));
                screen.setDisplayType(getText(screenNode, "displayType"));
                screen.setRefreshRate(getText(screenNode, "refreshRate"));
                screen.setHdrSupport(getBoolean(screenNode, "hdrSupport"));
                spec.setScreen(screen);
            } else {
                spec.setScreen(new ScreenSpec());
            }

            // 解析连接规格
            JsonNode connectivityNode = rootNode.path("connectivity");
            if (!connectivityNode.isMissingNode()) {
                ConnectivitySpec connectivity = new ConnectivitySpec();
                connectivity.setHdmiPorts(getInt(connectivityNode, "hdmiPorts"));
                connectivity.setUsbPorts(getInt(connectivityNode, "usbPorts"));
                connectivity.setWifi(getBoolean(connectivityNode, "wifi"));
                connectivity.setBluetooth(getBoolean(connectivityNode, "bluetooth"));
                spec.setConnectivity(connectivity);
            } else {
                spec.setConnectivity(new ConnectivitySpec());
            }

            // 解析电源规格
            JsonNode powerNode = rootNode.path("power");
            if (!powerNode.isMissingNode()) {
                PowerSpec power = new PowerSpec();
                power.setPowerConsumption(getText(powerNode, "powerConsumption"));
                power.setStandbyPower(getText(powerNode, "standbyPower"));
                spec.setPower(power);
            } else {
                spec.setPower(new PowerSpec());
            }

            // 解析尺寸规格
            JsonNode dimensionNode = rootNode.path("dimension");
            if (!dimensionNode.isMissingNode()) {
                DimensionSpec dimension = new DimensionSpec();
                dimension.setWidth(getText(dimensionNode, "width"));
                dimension.setHeight(getText(dimensionNode, "height"));
                dimension.setDepth(getText(dimensionNode, "depth"));
                dimension.setWeight(getText(dimensionNode, "weight"));
                spec.setDimension(dimension);
            } else {
                spec.setDimension(new DimensionSpec());
            }

            return spec;

        } catch (JsonProcessingException e) {
            log.error("JSON解析失败，原始响应: {}", response, e);
            throw new RuntimeException("JSON解析失败: " + e.getMessage(), e);
        }
    }

    /**
     * 清理和提取JSON内容
     */
    private String cleanAndExtractJson(String text) {
        // 移除可能的markdown代码块标记
        text = text.replace("```json", "").replace("```", "").trim();

        // 查找第一个{和最后一个}
        int start = text.indexOf('{');
        int end = text.lastIndexOf('}');

        if (start >= 0 && end > start) {
            return text.substring(start, end + 1);
        }

        // 如果找不到完整的JSON，尝试修复常见的JSON格式错误
        return fixJsonFormat(text);
    }

    /**
     * 修复常见的JSON格式错误
     */
    private String fixJsonFormat(String text) {
        // 移除开头的非JSON字符
        text = text.replaceAll("^[^{]*", "");

        // 移除结尾的非JSON字符
        text = text.replaceAll("[^}]*$", "");

        // 修复缺失的引号
        text = text.replaceAll("(\\w+):", "\"$1\":");

        return text;
    }

    private String getText(JsonNode node, String fieldName) {
        JsonNode fieldNode = node.path(fieldName);
        if (fieldNode.isMissingNode() || fieldNode.isNull()) {
            return null;
        }
        return fieldNode.asText();
    }

    private Double getDouble(JsonNode node, String fieldName) {
        JsonNode fieldNode = node.path(fieldName);
        if (fieldNode.isMissingNode() || fieldNode.isNull()) {
            return null;
        }
        if (fieldNode.isNumber()) {
            return fieldNode.asDouble();
        }
        try {
            return Double.parseDouble(fieldNode.asText());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Integer getInt(JsonNode node, String fieldName) {
        JsonNode fieldNode = node.path(fieldName);
        if (fieldNode.isMissingNode() || fieldNode.isNull()) {
            return null;
        }
        if (fieldNode.isNumber()) {
            return fieldNode.asInt();
        }
        try {
            return Integer.parseInt(fieldNode.asText());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Boolean getBoolean(JsonNode node, String fieldName) {
        JsonNode fieldNode = node.path(fieldName);
        if (fieldNode.isMissingNode() || fieldNode.isNull()) {
            return null;
        }
        if (fieldNode.isBoolean()) {
            return fieldNode.asBoolean();
        }
        String value = fieldNode.asText().toLowerCase();
        return value.equals("true") || value.equals("是") || value.equals("支持") || value.equals("yes");
    }

    /**
     * 请求头拦截器
     */
    private class HeaderInterceptor implements ClientHttpRequestInterceptor {
        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
            // 添加必要的请求头
            request.getHeaders().set("User-Agent", "TV-Data-Cleansing/1.0");
            request.getHeaders().set("Accept", "application/json");
            request.getHeaders().set("Accept-Encoding", "gzip, deflate, br");

            // 记录请求信息（不记录敏感信息）
            log.debug("发送请求到: {}", request.getURI());
            log.debug("请求方法: {}", request.getMethod());

            return execution.execute(request, body);
        }
    }
}
