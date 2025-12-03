package com.ai.dataCleansing.common;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TextPreprocessor {
    /**
     * 预处理文本，移除噪音字符
     */
    public String preprocessText(String text) {
        if (text == null) return "";

        String processed = text;

        // 移除HTML标签
        processed = processed.replaceAll("<[^>]*>", "");

        // 移除多余的空格和换行
        processed = processed.replaceAll("\\s+", " ");
        processed = processed.replaceAll("\\n+", "\n");

        // 移除特殊字符但保留必要的标点
        processed = processed.replaceAll("[^a-zA-Z0-9\\u4e00-\\u9fa5\\s.,;:!?()\\[\\]{}/\\-+]", "");

        // 标准化标点
        processed = processed.replaceAll("，", ",")
                .replaceAll("。", ".")
                .replaceAll("；", ";")
                .replaceAll("：", ":")
                .replaceAll("！", "!")
                .replaceAll("？", "?")
                .replaceAll("（", "(")
                .replaceAll("）", ")")
                .replaceAll("【", "[")
                .replaceAll("】", "]")
                .replaceAll("、", ",");

        return processed.trim();
    }

    /**
     * 提取文本中的关键段落
     */
    public List<String> extractKeyParagraphs(String text, List<String> keywords) {
        List<String> paragraphs = new ArrayList<>();
        String[] lines = text.split("[。.!?？]");

        for (String line : lines) {
            if (containsAnyKeyword(line, keywords)) {
                paragraphs.add(line.trim());
            }
        }

        return paragraphs;
    }

    private boolean containsAnyKeyword(String text, List<String> keywords) {
        if (text == null || keywords == null || keywords.isEmpty()) {
            return false;
        }

        String lowerText = text.toLowerCase();
        for (String keyword : keywords) {
            if (keyword != null && lowerText.contains(keyword.toLowerCase())) {
                return true;
            }
        }

        return false;
    }
}
