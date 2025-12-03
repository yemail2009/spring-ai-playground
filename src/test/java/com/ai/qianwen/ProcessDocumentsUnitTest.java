//package com.ai.qianwen;
//
//import com.ai.dataCleansing.common.TextPreprocessor;
//import com.ai.dataCleansing.model.*;
//import com.ai.dataCleansing.repository.TVSpecificationService;
//import com.ai.dataCleansing.service.DataCleaningEngine;
//import com.ai.dataCleansing.service.DataStandardizationService;
//import com.ai.dataCleansing.service.LLMDataExtractionService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//import java.util.stream.Collectors;
//
//@SpringBootTest
//@MockBean(LLMDataExtractionService.class)
//@MockBean(TVSpecificationService.class)
//@MockBean(DataStandardizationService.class)
//public class ProcessDocumentsUnitTest {
//
//    @Autowired
//    private DataCleaningEngine dataCleaningEngine;
//
//    @MockBean
//    private LLMDataExtractionService extractionService;
//
//    @MockBean
//    private TVSpecificationService tvSpecificationService;
//
//    @MockBean
//    private DataStandardizationService standardizationService;
//
//    @MockBean
//    private TextPreprocessor textPreprocessor;
//
//    private List<Document> testDocuments;
//
//    @BeforeEach
//    public void setUp() {
//        // 创建测试文档列表
//        testDocuments = new ArrayList<>();
//
//        testDocuments.add(new Document("""
//            索尼电视 KD-65X90J
//            65英寸4K OLED电视
//            支持HDR，刷新率120Hz
//            4个HDMI接口，2个USB接口
//            支持WiFi和蓝牙
//            """));
//
//        testDocuments.add(new Document("""
//            三星电视 UA55TU8000
//            55英寸QLED电视
//            支持4K分辨率
//            3个HDMI接口
//            带智能系统
//            """));
//
//        testDocuments.add(new Document("""
//            LG OLED电视 OLED65C1PCB
//            65英寸OLED屏幕
//            120Hz刷新率，支持HDR
//            4个HDMI 2.1接口
//            """));
//
//        testDocuments.add(new Document("""
//            海信电视 65E8G
//            65英寸ULED电视
//            量子点技术
//            144Hz刷新率
//            2个HDMI 2.1接口
//            """));
//
//        testDocuments.add(new Document("")); // 空文档，用于测试错误情况
//
//        // 模拟文本预处理
//        when(textPreprocessor.preprocessText(anyString()))
//                .thenAnswer(invocation -> invocation.getArgument(0));
//
//        // 模拟空文档预处理
//        when(textPreprocessor.preprocessText(""))
//                .thenReturn("");
//    }
//
////    @Test
////    public void testProcessDocuments_AllSuccess() {
////        // 模拟大模型提取成功
////        when(extractionService.extractSpecifications(anyString()))
////                .thenAnswer(invocation -> {
////                    String content = invocation.getArgument(0);
////                    return createMockSpecificationFromContent(content);
////                });
////
////        // 模拟数据标准化
////        when(standardizationService.standardizeData(any(TVSpecification.class)))
////                .thenAnswer(invocation -> {
////                    TVSpecification spec = invocation.getArgument(0);
////                    return spec; // 直接返回，不修改
////                });
////
////        // 模拟数据库操作 - 所有文档都不存在
////        when(tvSpecificationService.existsByModelNumber(anyString()))
////                .thenReturn(false);
////        when(tvSpecificationService.save(any(TVSpecification.class)))
////                .thenReturn(true);
////
////        // 执行批量处理
////        BatchProcessingResult result = dataCleaningEngine.processDocuments(testDocuments);
////
////        // 验证结果
////        assertNotNull(result);
////        assertEquals(5, result.getTotalProcessed()); // 包含5个文档
////        assertEquals(4, result.getSuccessfulCount()); // 4个成功（空文档会失败）
////        assertEquals(1, result.getFailedCount()); // 1个失败（空文档）
////        assertEquals(0, result.getNeedsReviewCount());
////        assertNotNull(result.getTotalDuration());
////        assertTrue(result.getTotalDuration() > 0);
////
////        // 验证详细结果
////        assertNotNull(result.getDetailedResults());
////        assertEquals(5, result.getDetailedResults().size());
////
////        // 验证成功的结果
////        long successCount = result.getDetailedResults().stream()
////                .filter(DataCleaningResult::isSuccess)
////                .count();
////        assertEquals(4, successCount);
////
////        // 验证失败的结果
////        long failedCount = result.getDetailedResults().stream()
////                .filter(DataCleaningResult::isFailed)
////                .count();
////        assertEquals(1, failedCount);
////
////        // 验证空文档的处理结果
////        DataCleaningResult emptyDocResult = result.getDetailedResults().get(4); // 第5个是空文档
////        assertEquals(DataCleaningStatus.FAILED, emptyDocResult.getStatus());
////        assertNotNull(emptyDocResult.getErrorMessage());
////        assertTrue(emptyDocResult.getErrorMessage().contains("文本内容过短"));
////    }
////
////    @Test
////    public void testProcessDocuments_MixedResults() {
////        // 模拟不同的处理结果
////        when(extractionService.extractSpecifications(anyString()))
////                .thenAnswer(invocation -> {
////                    String content = invocation.getArgument(0);
////                    if (content.contains("索尼")) {
////                        return createValidSpecification("KD-65X90J", "SONY");
////                    } else if (content.contains("三星")) {
////                        // 三星文档缺少必要字段，验证会失败
////                        return createInvalidSpecificationMissingModel();
////                    } else if (content.contains("LG")) {
////                        return createValidSpecification("OLED65C1PCB", "LG");
////                    } else if (content.contains("海信")) {
////                        // 海信文档需要人工审核
////                        TVSpecification spec = createValidSpecification("65E8G", "HISENSE");
////                        spec.setBrand(null); // 缺少品牌，需要人工审核
////                        return spec;
////                    } else {
////                        return null; // 空文档提取失败
////                    }
////                });
////
////        // 模拟标准化服务
////        when(standardizationService.standardizeData(any(TVSpecification.class)))
////                .thenAnswer(invocation -> {
////                    TVSpecification spec = invocation.getArgument(0);
////                    return spec; // 直接返回
////                });
////
////        // 模拟数据库操作
////        when(tvSpecificationService.existsByModelNumber(anyString()))
////                .thenReturn(false);
////        when(tvSpecificationService.save(any(TVSpecification.class)))
////                .thenReturn(true);
////
////        // 执行批量处理
////        BatchProcessingResult result = dataCleaningEngine.processDocuments(testDocuments);
////
////        // 验证混合结果
////        assertNotNull(result);
////        assertEquals(5, result.getTotalProcessed());
////
////        // 统计各种状态
////        Map<DataCleaningStatus, Long> statusCount = result.getDetailedResults().stream()
////                .collect(Collectors.groupingBy(DataCleaningResult::getStatus, Collectors.counting()));
////
////        // 验证状态分布
////        assertEquals(2, (long) statusCount.getOrDefault(DataCleaningStatus.SUCCESS, 0L)); // 索尼和LG成功
////        assertEquals(2, (long) statusCount.getOrDefault(DataCleaningStatus.FAILED, 0L)); // 三星和空文档失败
////        assertEquals(1, (long) statusCount.getOrDefault(DataCleaningStatus.NEEDS_REVIEW, 0L)); // 海信需要审核
////
////        // 验证统计计数
////        assertEquals(2, result.getSuccessfulCount());
////        assertEquals(2, result.getFailedCount());
////        assertEquals(1, result.getNeedsReviewCount());
////
////        // 验证成功率
////        assertEquals(40.0, result.getSuccessRate(), 0.001); // 2/5 = 40%
////        assertEquals(40.0, result.getFailureRate(), 0.001); // 2/5 = 40%
////        assertEquals(20.0, result.getNeedsReviewRate(), 0.001); // 1/5 = 20%
////    }
////
////    @Test
////    public void testProcessDocuments_WithDuplicates() {
////        // 模拟提取相同型号的数据
////        when(extractionService.extractSpecifications(anyString()))
////                .thenReturn(createValidSpecification("KD-65X90J", "SONY"));
////
////        // 模拟数据库 - 第一个不存在，后面的都存在
////        when(tvSpecificationService.existsByModelNumber("KD-65X90J"))
////                .thenReturn(false, true, true, true, true); // 第一次false，后面都是true
////
////        when(tvSpecificationService.save(any(TVSpecification.class)))
////                .thenReturn(true);
////        when(tvSpecificationService.updateByModelNumber(any(TVSpecification.class)))
////                .thenReturn(true);
////
////        // 执行批量处理（5个相同型号的文档）
////        BatchProcessingResult result = dataCleaningEngine.processDocuments(testDocuments);
////
////        // 验证结果
////        assertNotNull(result);
////        assertEquals(5, result.getTotalProcessed());
////
////        // 统计状态
////        Map<DataCleaningStatus, Long> statusCount = result.getDetailedResults().stream()
////                .collect(Collectors.groupingBy(DataCleaningResult::getStatus, Collectors.counting()));
////
////        // 第一个应该成功，后面的应该更新
////        assertEquals(1, (long) statusCount.getOrDefault(DataCleaningStatus.SUCCESS, 0L));
////        assertEquals(4, (long) statusCount.getOrDefault(DataCleaningStatus.UPDATED, 0L));
////
////        assertEquals(5, result.getSuccessfulCount()); // SUCCESS和UPDATED都算成功
////        assertEquals(0, result.getFailedCount());
////    }
////
////    @Test
////    public void testProcessDocuments_WithDatabaseErrors() {
////        // 模拟提取成功
////        when(extractionService.extractSpecifications(anyString()))
////                .thenReturn(createValidSpecification("KD-65X90J", "SONY"));
////
////        // 模拟标准化成功
////        when(standardizationService.standardizeData(any(TVSpecification.class)))
////                .thenAnswer(invocation -> invocation.getArgument(0));
////
////        // 模拟数据库操作失败
////        when(tvSpecificationService.existsByModelNumber(anyString()))
////                .thenReturn(false);
////        when(tvSpecificationService.save(any(TVSpecification.class)))
////                .thenThrow(new RuntimeException("数据库连接失败"));
////
////        // 执行批量处理
////        BatchProcessingResult result = dataCleaningEngine.processDocuments(testDocuments);
////
////        // 验证所有都失败
////        assertNotNull(result);
////        assertEquals(5, result.getTotalProcessed());
////        assertEquals(0, result.getSuccessfulCount());
////        assertEquals(5, result.getFailedCount());
////
////        // 验证错误信息
////        for (DataCleaningResult detail : result.getDetailedResults()) {
////            assertEquals(DataCleaningStatus.FAILED, detail.getStatus());
////            assertTrue(detail.getErrorMessage().contains("数据库操作失败"));
////        }
////    }
////
////    @Test
////    public void testProcessDocuments_EmptyDocumentList() {
////        // 测试空列表
////        BatchProcessingResult result = dataCleaningEngine.processDocuments(new ArrayList<>());
////
////        assertNotNull(result);
////        assertEquals(0, result.getTotalProcessed());
////        assertEquals(0, result.getSuccessfulCount());
////        assertEquals(0, result.getFailedCount());
////        assertEquals(0, result.getNeedsReviewCount());
////        assertTrue(result.getDetailedResults().isEmpty());
////        assertEquals(0.0, result.getSuccessRate(), 0.001);
////    }
////
////    @Test
////    public void testProcessDocuments_WithLargeBatch() {
////        // 创建大量测试文档
////        List<Document> largeBatch = new ArrayList<>();
////        for (int i = 0; i < 100; i++) {
////            largeBatch.add(new Document(String.format("""
////                电视型号：TV-%03d
////                品牌：测试品牌
////                尺寸：55英寸
////                分辨率：4K
////                HDMI接口：3个
////                """, i)));
////        }
////
////        // 模拟处理成功
////        when(extractionService.extractSpecifications(anyString()))
////                .thenAnswer(invocation -> {
////                    String content = invocation.getArgument(0);
////                    // 从内容中提取型号
////                    Pattern pattern = Pattern.compile("TV-(\\d+)");
////                    Matcher matcher = pattern.matcher(content);
////                    if (matcher.find()) {
////                        return createValidSpecification("TV-" + matcher.group(1), "TEST");
////                    }
////                    return createValidSpecification("DEFAULT", "TEST");
////                });
////
////        when(standardizationService.standardizeData(any(TVSpecification.class)))
////                .thenAnswer(invocation -> invocation.getArgument(0));
////
////        when(tvSpecificationService.existsByModelNumber(anyString()))
////                .thenReturn(false);
////        when(tvSpecificationService.save(any(TVSpecification.class)))
////                .thenReturn(true);
////
////        // 执行批量处理
////        long startTime = System.currentTimeMillis();
////        BatchProcessingResult result = dataCleaningEngine.processDocuments(largeBatch);
////        long endTime = System.currentTimeMillis();
////
////        // 验证结果
////        assertNotNull(result);
////        assertEquals(100, result.getTotalProcessed());
////        assertEquals(100, result.getSuccessfulCount());
////        assertEquals(0, result.getFailedCount());
////
////        // 验证处理时间（应该有一定延迟，因为每个文档之间有100ms间隔）
////        long processingTime = endTime - startTime;
////        assertTrue(processingTime >= 100 * 100); // 至少100个文档 * 100ms
////
////        // 验证详细结果数量
////        assertEquals(100, result.getDetailedResults().size());
////
////        // 验证所有结果都是成功的
////        boolean allSuccess = result.getDetailedResults().stream()
////                .allMatch(DataCleaningResult::isSuccess);
////        assertTrue(allSuccess);
////    }
////
////    @Test
////    public void testProcessDocuments_WithThreadInterruption() throws InterruptedException {
////        // 模拟提取需要较长时间
////        when(extractionService.extractSpecifications(anyString()))
////                .thenAnswer(invocation -> {
////                    Thread.sleep(200); // 模拟耗时操作
////                    return createValidSpecification("TEST", "BRAND");
////                });
////
////        when(standardizationService.standardizeData(any(TVSpecification.class)))
////                .thenAnswer(invocation -> invocation.getArgument(0));
////
////        when(tvSpecificationService.existsByModelNumber(anyString()))
////                .thenReturn(false);
////        when(tvSpecificationService.save(any(TVSpecification.class)))
////                .thenReturn(true);
////
////        // 创建单独的线程执行，以便可以中断
////        List<Document> smallBatch = testDocuments.subList(0, 2);
////        BatchProcessingResult[] resultHolder = new BatchProcessingResult[1];
////
////        Thread processingThread = new Thread(() -> {
////            resultHolder[0] = dataCleaningEngine.processDocuments(smallBatch);
////        });
////
////        processingThread.start();
////
////        // 等待一段时间后中断线程
////        Thread.sleep(100);
////        processingThread.interrupt();
////
////        // 等待线程结束
////        processingThread.join();
////
////        // 验证结果可能为null（如果被中断）
////        // 这个测试主要是验证中断处理逻辑，不一定会得到完整结果
////        assertNotNull(resultHolder[0]); // 在现有实现中，应该仍然有结果
////    }
////
////    @Test
////    public void testProcessDocuments_ResultCompleteness() {
////        // 测试结果的完整性
////        when(extractionService.extractSpecifications(anyString()))
////                .thenReturn(createValidSpecification("MODEL", "BRAND"));
////
////        when(standardizationService.standardizeData(any(TVSpecification.class)))
////                .thenAnswer(invocation -> invocation.getArgument(0));
////
////        when(tvSpecificationService.existsByModelNumber(anyString()))
////                .thenReturn(false);
////        when(tvSpecificationService.save(any(TVSpecification.class)))
////                .thenReturn(true);
////
////        BatchProcessingResult result = dataCleaningEngine.processDocuments(testDocuments.subList(0, 3));
////
////        // 验证结果对象的完整性
////        assertNotNull(result);
////        assertNotNull(result.getProcessStartTime());
////        assertNotNull(result.getProcessEndTime());
////        assertNotNull(result.getTotalDuration());
////        assertTrue(result.getTotalDuration() >= 0);
////
////        // 验证详细结果
////        assertNotNull(result.getDetailedResults());
////        assertEquals(3, result.getDetailedResults().size());
////
////        // 验证每个详细结果的完整性
////        for (DataCleaningResult detail : result.getDetailedResults()) {
////            assertNotNull(detail);
////            assertNotNull(detail.getStatus());
////            assertNotNull(detail.getProcessTime());
////            assertNotNull(detail.getProcessingDuration());
////            assertTrue(detail.getProcessingDuration() > 0);
////
////            // 成功的结果应该有processedData
////            if (detail.isSuccess()) {
////                assertNotNull(detail.getProcessedData());
////                assertNotNull(detail.getProcessedData().getModelNumber());
////                assertNotNull(detail.getProcessedData().getBrand());
////            }
////        }
////    }
////
////    @Test
////    public void testProcessDocuments_BatchResultToString() {
////        // 测试BatchProcessingResult的toString方法
////        BatchProcessingResult result = new BatchProcessingResult();
////        result.setTotalProcessed(10);
////        result.setSuccessfulCount(7);
////        result.setFailedCount(2);
////        result.setNeedsReviewCount(1);
////        result.setTotalDuration(5000L);
////
////        String toString = result.toString();
////
////        assertNotNull(toString);
////        assertTrue(toString.contains("total=10"));
////        assertTrue(toString.contains("success=7"));
////        assertTrue(toString.contains("failed=2"));
////        assertTrue(toString.contains("review=1"));
////        assertTrue(toString.contains("duration=5000ms"));
////    }
////
////    @Test
////    public void testProcessDocuments_CalculateRates() {
////        BatchProcessingResult result = new BatchProcessingResult();
////
////        // 测试各种情况下的计算
////        result.setTotalProcessed(0);
////        result.setSuccessfulCount(0);
////        result.setFailedCount(0);
////        result.setNeedsReviewCount(0);
////
////        assertEquals(0.0, result.getSuccessRate(), 0.001);
////        assertEquals(0.0, result.getFailureRate(), 0.001);
////        assertEquals(0.0, result.getNeedsReviewRate(), 0.001);
////
////        result.setTotalProcessed(10);
////        result.setSuccessfulCount(6);
////        result.setFailedCount(3);
////        result.setNeedsReviewCount(1);
////
////        assertEquals(60.0, result.getSuccessRate(), 0.001); // 6/10 = 60%
////        assertEquals(30.0, result.getFailureRate(), 0.001); // 3/10 = 30%
////        assertEquals(10.0, result.getNeedsReviewRate(), 0.001); // 1/10 = 10%
////    }
////
////    @Test
////    public void testProcessDocuments_CompleteProcessing() {
////        BatchProcessingResult result = new BatchProcessingResult();
////
////        // 记录开始时间
////        long startTime = System.currentTimeMillis();
////        result.setProcessStartTime(new Date(startTime));
////
////        // 模拟处理一段时间
////        try {
////            Thread.sleep(100);
////        } catch (InterruptedException e) {
////            Thread.currentThread().interrupt();
////        }
////
////        // 完成处理
////        result.completeProcessing();
////
////        // 验证结束时间和持续时间
////        assertNotNull(result.getProcessEndTime());
////        assertNotNull(result.getTotalDuration());
////        assertTrue(result.getTotalDuration() >= 100);
////    }
////
////    // 辅助方法：创建有效的规格
////    private TVSpecification createValidSpecification(String modelNumber, String brand) {
////        TVSpecification spec = new TVSpecification();
////        spec.setModelNumber(modelNumber);
////        spec.setBrand(brand);
////
////        ScreenSpec screen = new ScreenSpec();
////        screen.setSize(55.0);
////        screen.setResolution("3840x2160");
////        screen.setHdrSupport(true);
////        spec.setScreen(screen);
////
////        ConnectivitySpec connectivity = new ConnectivitySpec();
////        connectivity.setHdmiPorts(3);
////        connectivity.setWifi(true);
////        spec.setConnectivity(connectivity);
////
////        return spec;
////    }
////
////    // 辅助方法：根据内容创建模拟规格
////    private TVSpecification createMockSpecificationFromContent(String content) {
////        TVSpecification spec = new TVSpecification();
////
////        if (content.contains("索尼")) {
////            spec.setModelNumber("KD-65X90J");
////            spec.setBrand("索尼");
////            spec.setScreen(new ScreenSpec());
////            spec.getScreen().setSize(65.0);
////        } else if (content.contains("三星")) {
////            spec.setModelNumber("UA55TU8000");
////            spec.setBrand("三星");
////            spec.setScreen(new ScreenSpec());
////            spec.getScreen().setSize(55.0);
////        } else if (content.contains("LG")) {
////            spec.setModelNumber("OLED65C1PCB");
////            spec.setBrand("LG");
////            spec.setScreen(new ScreenSpec());
////            spec.getScreen().setSize(65.0);
////        } else if (content.contains("海信")) {
////            spec.setModelNumber("65E8G");
////            spec.setBrand("海信");
////            spec.setScreen(new ScreenSpec());
////            spec.getScreen().setSize(65.0);
////        } else {
////            // 默认
////            spec.setModelNumber("DEFAULT");
////            spec.setBrand("DEFAULT");
////            spec.setScreen(new ScreenSpec());
////            spec.getScreen().setSize(50.0);
////        }
////
////        spec.getScreen().setResolution("3840x2160");
////        spec.getScreen().setHdrSupport(true);
////
////        ConnectivitySpec connectivity = new ConnectivitySpec();
////        connectivity.setHdmiPorts(4);
////        connectivity.setWifi(true);
////        spec.setConnectivity(connectivity);
////
////        return spec;
////    }
////
////    // 辅助方法：创建缺少型号的无效规格
////    private TVSpecification createInvalidSpecificationMissingModel() {
////        TVSpecification spec = new TVSpecification();
////        spec.setBrand("三星"); // 有品牌
////        // 缺少型号，会导致验证失败
////        spec.setScreen(new ScreenSpec());
////        spec.getScreen().setSize(55.0);
////        return spec;
////    }
//}
