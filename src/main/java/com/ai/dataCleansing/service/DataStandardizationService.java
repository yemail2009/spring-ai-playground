package com.ai.dataCleansing.service;

import com.ai.dataCleansing.model.*;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class DataStandardizationService {
    private static final Map<String, String> DISPLAY_TYPE_MAPPING = Map.of(
            "液晶", "LED",
            "有机发光二极管", "OLED",
            "量子点", "QLED",
            "迷你LED", "Mini-LED",
            "等离子", "Plasma"
    );

    private static final Map<String, String> RESOLUTION_MAPPING = Map.of(
            "超高清", "3840x2160",
            "4k", "3840x2160",
            "全高清", "1920x1080",
            "高清", "1280x720"
    );

    public TVSpecification standardizeData(TVSpecification rawSpec) {
        TVSpecification standardized = new TVSpecification();

        // 标准化型号
        standardized.setModelNumber(standardizeModelNumber(rawSpec.getModelNumber()));

        // 标准化品牌
        standardized.setBrand(standardizeBrand(rawSpec.getBrand()));

        // 标准化屏幕信息
        standardized.setScreen(standardizeScreenSpec(rawSpec.getScreen()));

        // 标准化其他规格
        standardized.setDisplay(standardizeDisplaySpec(rawSpec.getDisplay()));
        standardized.setAudio(standardizeAudioSpec(rawSpec.getAudio()));
        standardized.setConnectivity(standardizeConnectivitySpec(rawSpec.getConnectivity()));

        return standardized;
    }

    private ScreenSpec standardizeScreenSpec(ScreenSpec screen) {
        if (screen == null) return null;

        ScreenSpec standardized = new ScreenSpec();

        // 标准化尺寸
        standardized.setSize(extractNumericSize(screen.getSize()));

        // 标准化分辨率
        standardized.setResolution(RESOLUTION_MAPPING.getOrDefault(
                screen.getResolution().toLowerCase(), screen.getResolution()));

        // 标准化显示技术
        standardized.setDisplayType(DISPLAY_TYPE_MAPPING.getOrDefault(
                screen.getDisplayType(), screen.getDisplayType()));

        return standardized;
    }

    private Double extractNumericSize(Object sizeObj) {
        if (sizeObj instanceof Double) {
            return (Double) sizeObj;
        } else if (sizeObj instanceof String) {
            String sizeStr = (String) sizeObj;
            // 使用正则表达式提取数字
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(\\d+(?:\\.\\d+)?)");
            java.util.regex.Matcher matcher = pattern.matcher(sizeStr);
            if (matcher.find()) {
                return Double.parseDouble(matcher.group(1));
            }
        }
        return null;
    }

    private String standardizeModelNumber(String modelNumber) {
        if (modelNumber == null || modelNumber.trim().isEmpty()) {
            return null;
        }

        String standardized = modelNumber.trim();

        // 移除常见的非型号字符
        standardized = standardized.replaceAll("(?i)型号[:：\\s]*", "");
        standardized = standardized.replaceAll("(?i)model[:：\\s]*", "");
        standardized = standardized.replaceAll("[™®©]", "");

        // 统一大小写格式（通常为大写）
        standardized = standardized.toUpperCase();

        // 移除多余的空格和分隔符
        standardized = standardized.replaceAll("\\s+", " ");
        standardized = standardized.replaceAll("[_\\-]+", "-");

        return standardized;
    }

    private String standardizeBrand(String brand) {
        if (brand == null || brand.trim().isEmpty()) {
            return "UNKNOWN";
        }

        String standardized = brand.trim().toUpperCase();

        // 品牌名称映射表
        // 双重花括号初始化 - 适用于一次性使用
        Map<String, String> brandMapping = new HashMap<String, String>() {{
            put("SONY", "SONY");
            put("索尼", "SONY");
            put("SAMSUNG", "SAMSUNG");
            put("三星", "SAMSUNG");
            put("LG", "LG");
            put("乐金", "LG");
            put("TCL", "TCL");
            put("海信", "HISENSE");
            put("HISENSE", "HISENSE");
            put("创维", "SKYWORTH");
            put("SKYWORTH", "SKYWORTH");
            put("小米", "XIAOMI");
            put("XIAOMI", "XIAOMI");
            put("华为", "HUAWEI");
            put("HUAWEI", "HUAWEI");
            put("康佳", "KONKA");
            put("KONKA", "KONKA");
            put("长虹", "CHANGHONG");
            put("CHANGHONG", "CHANGHONG");
        }};

        return brandMapping.getOrDefault(standardized, standardized);
    }

    private DisplaySpec standardizeDisplaySpec(DisplaySpec display) {
        if (display == null) return null;

        DisplaySpec standardized = new DisplaySpec();

        // 标准化色彩技术
        if (display.getColorTechnology() != null) {
            String colorTech = display.getColorTechnology().toLowerCase();
            if (colorTech.contains("量子点") || colorTech.contains("qled")) {
                standardized.setColorTechnology("QLED量子点");
            } else if (colorTech.contains("广色域") || colorTech.contains("wide color")) {
                standardized.setColorTechnology("广色域");
            } else if (colorTech.contains("杜比视界") || colorTech.contains("dolby vision")) {
                standardized.setColorTechnology("杜比视界");
            } else {
                standardized.setColorTechnology(display.getColorTechnology());
            }
        }

        // 标准化对比度
        if (display.getContrastRatio() != null) {
            standardized.setContrastRatio(display.getContrastRatio());
        }

        // 标准化可视角度
        if (display.getViewingAngle() != null) {
            String angle = display.getViewingAngle();
            // 提取数字角度
            Pattern pattern = Pattern.compile("(\\d+)");
            Matcher matcher = pattern.matcher(angle);
            if (matcher.find()) {
                int degree = Integer.parseInt(matcher.group(1));
                standardized.setViewingAngle(degree + "度");
            } else {
                standardized.setViewingAngle(angle);
            }
        }

        // 标准化亮度
        if (display.getBrightness() != null) {
            String brightness = display.getBrightness();
            // 提取数字和单位
            Pattern pattern = Pattern.compile("(\\d+(?:\\.\\d+)?)\\s*(尼特|nits|nit|cd/m²)?", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(brightness);
            if (matcher.find()) {
                String value = matcher.group(1);
                String unit = matcher.group(2);
                standardized.setBrightness(value + (unit != null ? " " + unit : " 尼特"));
            } else {
                standardized.setBrightness(brightness);
            }
        }

        return standardized;
    }

    private AudioSpec standardizeAudioSpec(AudioSpec audio) {
        if (audio == null) return null;

        AudioSpec standardized = new AudioSpec();

        // 标准化输出功率
        if (audio.getOutputPower() != null) {
            String power = audio.getOutputPower();
            // 提取瓦特数
            Pattern pattern = Pattern.compile("(\\d+(?:\\.\\d+)?)\\s*(瓦|w|瓦特)?", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(power);
            if (matcher.find()) {
                String value = matcher.group(1);
                standardized.setOutputPower(value + "W");
            } else {
                standardized.setOutputPower(power);
            }
        }

        // 标准化扬声器配置
        if (audio.getSpeakerConfiguration() != null) {
            String config = audio.getSpeakerConfiguration().toLowerCase();
            if (config.contains("2.0") || config.contains("双扬声器")) {
                standardized.setSpeakerConfiguration("2.0声道");
            } else if (config.contains("2.1")) {
                standardized.setSpeakerConfiguration("2.1声道");
            } else if (config.contains("5.1")) {
                standardized.setSpeakerConfiguration("5.1声道");
            } else if (config.contains("soundbar") || config.contains("回音壁")) {
                standardized.setSpeakerConfiguration("SoundBar");
            } else {
                standardized.setSpeakerConfiguration(audio.getSpeakerConfiguration());
            }
        }

        // 标准化音频技术
        if (audio.getAudioTechnologies() != null) {
            List<String> standardizedTechs = audio.getAudioTechnologies().stream()
                    .map(tech -> {
                        String lowerTech = tech.toLowerCase();
                        if (lowerTech.contains("杜比全景声") || lowerTech.contains("dolby atmos")) {
                            return "杜比全景声";
                        } else if (lowerTech.contains("dts") || lowerTech.contains("dts:x")) {
                            return "DTS:X";
                        } else if (lowerTech.contains("蝰蛇音效")) {
                            return "蝰蛇音效";
                        } else {
                            return tech;
                        }
                    })
                    .distinct()
                    .collect(Collectors.toList());
            standardized.setAudioTechnologies(standardizedTechs);
        }

        return standardized;
    }

    private ConnectivitySpec standardizeConnectivitySpec(ConnectivitySpec connectivity) {
        if (connectivity == null) return null;

        ConnectivitySpec standardized = new ConnectivitySpec();

        // 标准化端口数量
        standardized.setHdmiPorts(parsePortCount(connectivity.getHdmiPorts()));
        standardized.setUsbPorts(parsePortCount(connectivity.getUsbPorts()));

        // 标准化网络连接
        standardized.setWifi(parseBoolean(connectivity.getWifi()));
        standardized.setBluetooth(parseBoolean(connectivity.getBluetooth()));
        standardized.setEthernet(parseBoolean(connectivity.getEthernet()));

        return standardized;
    }

    private Integer parsePortCount(Object portCount) {
        if (portCount == null) return null;

        if (portCount instanceof Integer) {
            return (Integer) portCount;
        } else if (portCount instanceof String) {
            String str = (String) portCount;
            // 提取数字
            Pattern pattern = Pattern.compile("(\\d+)");
            Matcher matcher = pattern.matcher(str);
            if (matcher.find()) {
                return Integer.parseInt(matcher.group(1));
            }
        }
        return null;
    }

    private Boolean parseBoolean(Object value) {
        if (value == null) return false;

        if (value instanceof Boolean) {
            return (Boolean) value;
        } else if (value instanceof String) {
            String str = ((String) value).toLowerCase();
            return str.contains("支持") || str.contains("是") ||
                    str.contains("true") || str.contains("yes") ||
                    str.contains("有") || str.contains("具备");
        }
        return false;
    }
}
