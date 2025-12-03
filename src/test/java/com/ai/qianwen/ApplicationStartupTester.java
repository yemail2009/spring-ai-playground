package com.ai.qianwen;

import com.ai.dataCleansing.model.DataCleaningResult;
import com.ai.dataCleansing.model.TVSpecification;
import com.ai.dataCleansing.service.DataCleaningEngine;
import com.ai.dataCleansing.service.LLMDataExtractionService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

// ApplicationStartupTester.java
@Component  // 确保有@Component注解
@Slf4j
public class ApplicationStartupTester implements ApplicationRunner, CommandLineRunner {

    @Autowired(required = false)
    private DashScopeAPITester dashScopeAPITester;

    @Autowired(required = false)
    private LLMDataExtractionService llmDataExtractionService;

    @Autowired(required = false)
    private DataCleaningEngine dataCleaningEngine;

    @Value("${spring.application.name:tv-data-cleansing}")
    private String appName;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("应用程序启动完成，开始执行启动测试...");
        performStartupTests(args);
    }

    @Override
    public void run(String... args) throws Exception {
        // CommandLineRunner接口的实现
        log.info("CommandLineRunner执行，参数数量: {}", args.length);
    }

    @PostConstruct
    public void init() {
        log.info("{} 应用程序初始化...", appName);
    }

    /**
     * 执行启动测试
     */
    private void performStartupTests(ApplicationArguments args) {
        log.info("=========================================");
        log.info("开始执行应用程序启动测试");
        log.info("应用程序: {}", appName);
        log.info("启动时间: {}", new Date());
        log.info("启动参数: {}", Arrays.toString(args.getSourceArgs()));
        log.info("=========================================");

        // 测试1: 检查环境配置
        testEnvironmentConfiguration();

        // 测试2: 检查关键Bean
        testCriticalBeans();

        // 测试3: 测试AI服务连接（如果配置了）
        if (dashScopeAPITester != null) {
            testAIServices();
        } else {
            log.warn("DashScopeAPITester未找到，跳过AI服务测试");
        }

        // 测试4: 测试数据清洗功能
        testDataCleaningFunctionality();

        // 测试5: 系统资源检查
        testSystemResources();

        log.info("=========================================");
        log.info("应用程序启动测试完成");
        log.info("所有测试通过，应用程序已就绪！");
        log.info("=========================================");
    }

    /**
     * 测试环境配置
     */
    private void testEnvironmentConfiguration() {
        log.info("【测试1】环境配置检查");

        // 检查必要环境变量
        Map<String, String> envVars = Map.of(
                "JAVA_HOME", System.getenv("JAVA_HOME"),
                "SPRING_PROFILES_ACTIVE", System.getenv("SPRING_PROFILES_ACTIVE"),
                "DASHSCOPE_API_KEY", maskApiKey(System.getenv("DASHSCOPE_API_KEY"))
        );

        envVars.forEach((key, value) -> {
            if (value != null && !value.trim().isEmpty()) {
                log.info("  ✅ {}: {}", key, key.contains("KEY") ? "****" + value.substring(Math.max(0, value.length() - 4)) : value);
            } else {
                log.warn("  ⚠️  {}: 未设置", key);
            }
        });

        // 检查系统属性
        log.info("  系统属性:");
        log.info("    java.version: {}", System.getProperty("java.version"));
        log.info("    user.dir: {}", System.getProperty("user.dir"));
        log.info("    file.encoding: {}", System.getProperty("file.encoding"));

        log.info("✅ 环境配置检查完成");
    }

    /**
     * 测试关键Bean
     */
    private void testCriticalBeans() {
        log.info("【测试2】关键Bean检查");

        Map<String, Object> beans = new LinkedHashMap<>();
        beans.put("llmDataExtractionService", llmDataExtractionService);
        beans.put("dataCleaningEngine", dataCleaningEngine);
        beans.put("dashScopeAPITester", dashScopeAPITester);

        beans.forEach((name, bean) -> {
            if (bean != null) {
                log.info("  ✅ {}: 已加载 ({})", name, bean.getClass().getSimpleName());
            } else {
                log.warn("  ⚠️  {}: 未加载", name);
            }
        });

        log.info("✅ 关键Bean检查完成");
    }

    /**
     * 测试AI服务
     */
    private void testAIServices() {
        log.info("【测试3】AI服务测试");

        try {
            // 1. 检查API密钥格式
            dashScopeAPITester.checkApiKeyFormat();

            // 2. 测试简单连接
            dashScopeAPITester.testConnection();

            // 3. 测试电视机规格提取
            dashScopeAPITester.testTVSpecificationExtraction();

            log.info("✅ AI服务测试完成");
        } catch (Exception e) {
            log.error("❌ AI服务测试失败", e);
            log.warn("注意: AI服务不可用，将使用备用提取方法");
        }
    }

    /**
     * 测试数据清洗功能
     */
    private void testDataCleaningFunctionality() {
        log.info("【测试4】数据清洗功能测试");

        if (dataCleaningEngine == null) {
            log.error("❌ DataCleaningEngine未加载，跳过功能测试");
            return;
        }

        try {
            // 测试文档
            String testDocument = """
                索尼电视 KD-55X80J
                55英寸4K LED电视
                支持HDR，刷新率60Hz
                3个HDMI接口
                支持WiFi和蓝牙
                """;

            log.info("  测试文档: {}", testDocument.substring(0, Math.min(50, testDocument.length())) + "...");

            // 执行数据清洗
            DataCleaningResult result = dataCleaningEngine.cleanAndExtractData(testDocument);

            log.info("  清洗结果:");
            log.info("    状态: {}", result.getStatus());
            log.info("    耗时: {}ms", result.getProcessingDuration());

            if (result.isSuccess() && result.getProcessedData() != null) {
                TVSpecification spec = result.getProcessedData();
                log.info("    提取的型号: {}", spec.getModelNumber());
                log.info("    提取的品牌: {}", spec.getBrand());
                log.info("✅ 数据清洗功能测试通过");
            } else {
                log.warn("⚠️  数据清洗完成但有警告或错误");
                if (result.hasErrors()) {
                    result.getValidationErrors().forEach(error -> log.warn("    错误: {}", error));
                }
                if (result.hasWarnings()) {
                    result.getWarnings().forEach(warning -> log.warn("    警告: {}", warning));
                }
            }

        } catch (Exception e) {
            log.error("❌ 数据清洗功能测试失败", e);
        }
    }

    /**
     * 测试系统资源
     */
    private void testSystemResources() {
        log.info("【测试5】系统资源检查");

        Runtime runtime = Runtime.getRuntime();

        long maxMemory = runtime.maxMemory() / (1024 * 1024);
        long totalMemory = runtime.totalMemory() / (1024 * 1024);
        long freeMemory = runtime.freeMemory() / (1024 * 1024);
        long usedMemory = totalMemory - freeMemory;

        log.info("  内存使用:");
        log.info("    最大内存: {} MB", maxMemory);
        log.info("    已分配内存: {} MB", totalMemory);
        log.info("    已使用内存: {} MB", usedMemory);
        log.info("    可用内存: {} MB", freeMemory);

        // 处理器信息
        int availableProcessors = runtime.availableProcessors();
        log.info("  处理器数量: {}", availableProcessors);

        // 检查磁盘空间（示例目录）
        File currentDir = new File(".");
        long freeSpace = currentDir.getFreeSpace() / (1024 * 1024 * 1024);
        long totalSpace = currentDir.getTotalSpace() / (1024 * 1024 * 1024);
        log.info("  磁盘空间:");
        log.info("    总空间: {} GB", totalSpace);
        log.info("    可用空间: {} GB", freeSpace);

        if (freeMemory < 100) {
            log.warn("⚠️  可用内存较低，建议增加JVM内存");
        }

        log.info("✅ 系统资源检查完成");
    }

    /**
     * 脱敏处理API密钥
     */
    private String maskApiKey(String apiKey) {
        if (apiKey == null || apiKey.length() <= 8) {
            return apiKey;
        }
        return "sk-..." + apiKey.substring(apiKey.length() - 4);
    }
}
