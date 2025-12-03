package com.ai;

import com.ai.dataCleansing.model.BatchProcessingResult;
import com.ai.dataCleansing.model.Document;
import com.ai.dataCleansing.service.BatchProcessingService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Arrays;
import java.util.List;

/**
 * 应用主类
 *
 * @author LFCMS Team
 * @version 1.0
 * @since 2024
 */
@SpringBootApplication
//@MapperScan(basePackages = "com.ai.dataCleansing.repository")
//@EnableScheduling
public class Application {

    @Autowired
    private BatchProcessingService batchProcessingService;

    @EventListener(ApplicationReadyEvent.class)
    public void processSampleData() {
        List<Document> documents = Arrays.asList(
//                new Document("55寸4K超高清OLED电视，型号XYZ-55OLED，支持HDR10..."),
//                new Document("电视机规格：65英寸，QLED显示技术，分辨率3840x2160...")

        );

        documents = Arrays.asList(new Document("""
            索尼电视 X90J系列 - 旗舰级4K OLED电视
            
            产品型号：KD-65X90J
            品牌：索尼(SONY)
            系列：X90J系列
            
            【屏幕规格】
            屏幕尺寸：65英寸
            分辨率：3840x2160 (4K超高清)
            显示技术：OLED（有机发光二极管）
            刷新率：120Hz
            HDR支持：是，支持HDR10、杜比视界、HLG
            
            【连接接口】
            HDMI端口：4个（全部为HDMI 2.1）
            USB端口：2个（USB 3.0）
            无线连接：支持WiFi 6 (802.11ax)
            蓝牙：支持蓝牙5.2
            
            【音频系统】
            输出功率：50W
            扬声器配置：2.1声道
            音频技术：杜比全景声、DTS:X
            
            【电源规格】
            功耗：150W（典型值）
            待机功耗：0.5W
            电压范围：100-240V 50/60Hz
            节能认证：Energy Star认证
            
            【尺寸重量】
            宽度：1450mm（不含底座）
            高度：835mm（不含底座）
            厚度：70mm（最薄处）
            重量：25.5kg
            
            带底座尺寸：
            底座宽度：1300mm
            底座高度：900mm
            底座深度：300mm
            
            【智能功能】
            操作系统：Android TV
            语音助手：支持Google Assistant
            投屏功能：支持AirPlay 2、Chromecast
            
            【附加特性】
            游戏模式：支持VRR、ALLM
            护眼功能：低蓝光、无频闪
            安装方式：壁挂/VESA 400x300mm
            """));

        BatchProcessingResult result = batchProcessingService.processDocuments(documents);

        System.out.println("处理完成: " + result.getSuccessfulCount() + " 成功, " +
                result.getFailedCount() + " 失败");
    }

    /**
     * 应用启动入口
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }


}
