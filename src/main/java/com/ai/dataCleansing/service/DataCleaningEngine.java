package com.ai.dataCleansing.service;

import com.ai.dataCleansing.model.*;
import com.ai.dataCleansing.repository.TVSpecificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class DataCleaningEngine {
    @Autowired
    private LLMDataExtractionService extractionService;

    @Autowired
    private DataStandardizationService standardizationService;

    @Autowired
    private TVSpecificationService repository;

    public DataCleaningResult cleanAndExtractData(String documentContent) {
        DataCleaningResult result = new DataCleaningResult();

        try {
            // 步骤1: 使用大模型提取信息
            TVSpecification rawExtraction = extractionService.extractSpecifications(documentContent);

            // 步骤2: 数据标准化
            TVSpecification standardized = standardizationService.standardizeData(rawExtraction);

            // 步骤3: 数据验证
            ValidationResult validation = validateData(standardized);

            if (validation.isValid()) {
                // 步骤4: 保存到主数据
                repository.save(standardized);
                result.setStatus(DataCleaningStatus.SUCCESS);
                result.setProcessedData(standardized);
            } else {
                result.setStatus(DataCleaningStatus.VALIDATION_FAILED);
                result.setValidationErrors(validation.getErrors());

                // 步骤5: 尝试数据映射修正
                TVSpecification corrected = attemptDataCorrection(standardized, validation.getErrors());
                if (corrected != null) {
                    log.warn("持久化的数据为 {}", corrected.toString());
//                    repository.save(corrected);
                    result.setStatus(DataCleaningStatus.CORRECTED);
                    result.setProcessedData(corrected);
                }
            }

        } catch (Exception e) {
            result.setStatus(DataCleaningStatus.FAILED);
            result.setErrorMessage(e.getMessage());
        }

        return result;
    }

    private ValidationResult validateData(TVSpecification spec) {
        List<String> errors = new ArrayList<>();

        // 验证必要字段
        if (spec.getModelNumber() == null || spec.getModelNumber().trim().isEmpty()) {
            errors.add("型号不能为空");
        }

        if (spec.getScreen() == null || spec.getScreen().getSize() == null) {
            errors.add("屏幕尺寸不能为空");
        }

        // 验证数据范围
        if (spec.getScreen() != null && spec.getScreen().getSize() != null) {
            if (spec.getScreen().getSize() < 10 || spec.getScreen().getSize() > 120) {
                errors.add("屏幕尺寸超出合理范围");
            }
        }

        return new ValidationResult(errors.isEmpty(), errors);
    }

    private TVSpecification attemptDataCorrection(TVSpecification spec, List<String> errors) {
        // 基于错误类型进行智能修正
        TVSpecification corrected = spec;

        for (String error : errors) {
            if (error.contains("屏幕尺寸")) {
                corrected = correctScreenSize(corrected);
            }
            // 其他修正逻辑...
        }

        return corrected;
    }

    private TVSpecification correctScreenSize(TVSpecification spec) {
        if (spec == null || spec.getScreen() == null) {
            return spec;
        }

        ScreenSpec screen = spec.getScreen();

        // 尝试从型号中提取尺寸信息
        if (screen.getSize() == null && spec.getModelNumber() != null) {
            String model = spec.getModelNumber();
            // 查找常见的尺寸模式：XX寸、XX英寸、XX"
            Pattern pattern = Pattern.compile("(\\d{2,3})(?:寸|英寸|\"|inch)", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(model);

            if (matcher.find()) {
                try {
                    double size = Double.parseDouble(matcher.group(1));
                    if (size >= 20 && size <= 120) {
                        screen.setSize(size);
                    }
                } catch (NumberFormatException e) {
                    // 忽略转换错误
                }
            }
        }

        // 如果从型号中提取失败，尝试从其他字段提取
        if (screen.getSize() == null && spec.getAdditionalFeatures() != null) {
            for (Map.Entry<String, String> entry : spec.getAdditionalFeatures().entrySet()) {
                String key = entry.getKey().toLowerCase();
                String value = entry.getValue();

                if (key.contains("尺寸") || key.contains("大小") || key.contains("inch")) {
                    Pattern pattern = Pattern.compile("(\\d{2,3})(?:\\.\\d+)?");
                    Matcher matcher = pattern.matcher(value);
                    if (matcher.find()) {
                        try {
                            double size = Double.parseDouble(matcher.group(1));
                            if (size >= 20 && size <= 120) {
                                screen.setSize(size);
                                break;
                            }
                        } catch (NumberFormatException e) {
                            // 忽略转换错误
                        }
                    }
                }
            }
        }

        return spec;
    }

    // 添加更多的修正方法
    private TVSpecification correctResolution(TVSpecification spec) {
        if (spec == null || spec.getScreen() == null) {
            return spec;
        }

        ScreenSpec screen = spec.getScreen();

        // 如果分辨率缺失，根据尺寸推断
        if ((screen.getResolution() == null || screen.getResolution().isEmpty())
                && screen.getSize() != null) {

            double size = screen.getSize();
            if (size >= 50) {
                // 大尺寸电视通常是4K
                screen.setResolution("3840x2160");
            } else if (size >= 40) {
                // 中等尺寸可能是1080p或4K
                screen.setResolution("1920x1080");
            } else {
                // 小尺寸通常是720p
                screen.setResolution("1280x720");
            }
        }

        return spec;
    }

    // 综合修正方法
    private TVSpecification applyAllCorrections(TVSpecification spec, List<String> errors) {
        TVSpecification corrected = spec;

        for (String error : errors) {
            if (error.contains("屏幕尺寸")) {
                corrected = correctScreenSize(corrected);
            } else if (error.contains("分辨率")) {
                corrected = correctResolution(corrected);
            } else if (error.contains("品牌") || error.contains("型号")) {
                corrected = correctModelAndBrand(corrected);
            }
            // 可以添加更多修正条件
        }

        return corrected;
    }

    private TVSpecification correctModelAndBrand(TVSpecification spec) {
        if (spec == null) return spec;

        // 如果品牌为空，尝试从型号中推断
        if ((spec.getBrand() == null || spec.getBrand().isEmpty())
                && spec.getModelNumber() != null) {

            String model = spec.getModelNumber().toUpperCase();

            if (model.contains("SONY") || model.contains("KD-") || model.contains("XBR-")) {
                spec.setBrand("SONY");
            } else if (model.contains("SAMSUNG") || model.contains("UA") || model.contains("QN")) {
                spec.setBrand("SAMSUNG");
            } else if (model.contains("LG") || model.contains("OLED")) {
                spec.setBrand("LG");
            } else if (model.contains("TCL")) {
                spec.setBrand("TCL");
            } else if (model.contains("HISENSE")) {
                spec.setBrand("HISENSE");
            }
        }

        return spec;
    }
}
