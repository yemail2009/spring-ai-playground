package com.ai.dataCleansing.service;

import com.ai.dataCleansing.model.ConnectivitySpec;
import com.ai.dataCleansing.model.ScreenSpec;
import com.ai.dataCleansing.model.TVSpecification;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// LLMDataExtractionService.java
@Service
@Primary
@Slf4j
public class LLMDataExtractionService {

    @Autowired(required = false)
    private DashScopeLLMService dashScopeLLMService;

    /**
     * 提取规格信息
     */
    public TVSpecification extractSpecifications(String textContent) {
        try {
            log.info("开始提取规格信息，输入文本长度: {}", textContent.length());

            // 1. 预处理文本
            String cleanedText = preprocessText(textContent);

            // 2. 优先使用DashScope服务
            if (dashScopeLLMService != null) {
                log.info("使用DashScope服务进行AI提取");
                return dashScopeLLMService.extractTVSpecifications(cleanedText);
            }

            // 3. 备用方案：使用正则表达式提取
            log.warn("DashScope服务不可用，使用正则表达式提取");
            return extractByRegex(cleanedText);

        } catch (Exception e) {
            log.error("提取规格信息失败", e);
            return createDefaultSpecification(textContent);
        }
    }

    /**
     * 预处理文本
     */
    private String preprocessText(String text) {
        if (text == null) {
            return "";
        }

        // 1. 移除HTML标签
        String cleaned = text.replaceAll("<[^>]*>", "");

        // 2. 标准化空格和换行
        cleaned = cleaned.replaceAll("\\s+", " ");
        cleaned = cleaned.replaceAll("\\n+", "\n");

        // 3. 移除特殊字符但保留中文、英文、数字和常用标点
        cleaned = cleaned.replaceAll("[^a-zA-Z0-9\\u4e00-\\u9fa5\\s\\.,;:!?()\\[\\]{}/\\-+]", "");

        // 4. 截断过长的文本（避免API限制）
        if (cleaned.length() > 4000) {
            cleaned = cleaned.substring(0, 4000) + "...[文本过长已截断]";
            log.warn("输入文本过长，已截断为4000字符");
        }

        return cleaned.trim();
    }

    /**
     * 使用正则表达式提取基本信息
     */
    private TVSpecification extractByRegex(String text) {
        TVSpecification spec = new TVSpecification();

        // 提取型号（多种格式）
        Pattern modelPattern = Pattern.compile(
                "(?:型号|Model|型号编号)[:：]?\\s*([A-Za-z0-9\\-\\_\\/]+(?:\\s+[A-Za-z0-9\\-\\_\\/]+)*)",
                Pattern.CASE_INSENSITIVE
        );
        Matcher modelMatcher = modelPattern.matcher(text);
        if (modelMatcher.find()) {
            spec.setModelNumber(modelMatcher.group(1).trim());
        } else {
            // 尝试查找类似型号的模式
            Pattern altPattern = Pattern.compile("([A-Z]{2,4}[\\-\\_]?\\d+[A-Z]?)");
            Matcher altMatcher = altPattern.matcher(text);
            if (altMatcher.find()) {
                spec.setModelNumber(altMatcher.group(1));
            }
        }

        // 提取品牌
        if (text.matches("(?i).*索尼|SONY.*")) {
            spec.setBrand("SONY");
        } else if (text.matches("(?i).*三星|SAMSUNG.*")) {
            spec.setBrand("SAMSUNG");
        } else if (text.matches("(?i).*LG.*")) {
            spec.setBrand("LG");
        } else if (text.matches("(?i).*海信|Hisense.*")) {
            spec.setBrand("HISENSE");
        } else if (text.matches("(?i).*TCL.*")) {
            spec.setBrand("TCL");
        } else if (text.matches("(?i).*小米|Xiaomi.*")) {
            spec.setBrand("XIAOMI");
        } else if (text.matches("(?i).*华为|Huawei.*")) {
            spec.setBrand("HUAWEI");
        }

        // 提取屏幕尺寸
        Pattern sizePattern = Pattern.compile(
                "(\\d+(?:\\.\\d+)?)\\s*(?:英寸|寸|inch|INCH|\")",
                Pattern.CASE_INSENSITIVE
        );
        Matcher sizeMatcher = sizePattern.matcher(text);
        if (sizeMatcher.find()) {
            ScreenSpec screen = spec.getScreen();
            if (screen == null) {
                screen = new ScreenSpec();
                spec.setScreen(screen);
            }
            try {
                screen.setSize(Double.parseDouble(sizeMatcher.group(1)));
            } catch (NumberFormatException e) {
                log.warn("无法解析屏幕尺寸: {}", sizeMatcher.group(1));
            }
        }

        // 提取分辨率
        Pattern resolutionPattern = Pattern.compile(
                "(?:分辨率|分辨率)[:：]?\\s*(\\d+[xX×]\\d+|4[Kk]|1080[Pp]|720[Pp]|[Ff][Hh][Dd]|[Uu][Hh][Dd])",
                Pattern.CASE_INSENSITIVE
        );
        Matcher resolutionMatcher = resolutionPattern.matcher(text);
        if (resolutionMatcher.find()) {
            ScreenSpec screen = spec.getScreen();
            if (screen == null) {
                screen = new ScreenSpec();
                spec.setScreen(screen);
            }
            screen.setResolution(resolutionMatcher.group(1).toUpperCase());
        }

        // 提取HDMI端口数
        Pattern hdmiPattern = Pattern.compile(
                "(?:HDMI|hdmi)[:\\s]*(\\d+)(?:个|个端口)?",
                Pattern.CASE_INSENSITIVE
        );
        Matcher hdmiMatcher = hdmiPattern.matcher(text);
        if (hdmiMatcher.find()) {
            ConnectivitySpec connectivity = spec.getConnectivity();
            if (connectivity == null) {
                connectivity = new ConnectivitySpec();
                spec.setConnectivity(connectivity);
            }
            try {
                connectivity.setHdmiPorts(Integer.parseInt(hdmiMatcher.group(1)));
            } catch (NumberFormatException e) {
                log.warn("无法解析HDMI端口数: {}", hdmiMatcher.group(1));
            }
        }

        return spec;
    }

    /**
     * 创建默认规格（用于错误处理）
     */
    private TVSpecification createDefaultSpecification(String text) {
        TVSpecification spec = new TVSpecification();
        spec.setModelNumber("UNKNOWN-" + System.currentTimeMillis());
        spec.setBrand("UNKNOWN");

        // 设置默认的屏幕规格
        ScreenSpec screen = new ScreenSpec();
        screen.setSize(55.0); // 默认55英寸
        screen.setResolution("1920x1080"); // 默认1080p
        spec.setScreen(screen);

        // 设置默认的连接规格
        ConnectivitySpec connectivity = new ConnectivitySpec();
        connectivity.setHdmiPorts(3); // 默认3个HDMI
        connectivity.setUsbPorts(2); // 默认2个USB
        connectivity.setWifi(true); // 默认支持WiFi
        spec.setConnectivity(connectivity);

        // 记录原始文本信息
        if (text != null && text.length() > 0) {
            spec.setAdditionalFeatures(Map.of(
                    "extraction_method", "regex_fallback",
                    "original_text_preview", text.substring(0, Math.min(200, text.length())),
                    "error_timestamp", String.valueOf(System.currentTimeMillis())
            ));
        }

        log.warn("使用默认规格信息，型号: {}", spec.getModelNumber());
        return spec;
    }
}
