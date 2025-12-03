package com.ai.dataCleansing.model;

import lombok.Data;

@Data
public class ScreenSpec {
    private Double size; // 英寸
    private String resolution; // 如 "3840x2160"
    private String displayType; // LED, OLED, QLED等
    private String refreshRate; // 刷新率
    private Boolean hdrSupport;
}
