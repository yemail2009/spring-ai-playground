package com.ai.dataCleansing.repository;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@Data
@TableName("tv_specification")
public class TVSpecificationEntity {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String modelNumber;
    private String brand;
    private String series;

    // 屏幕规格
    private Double screenSize;
    private String screenResolution;
    private String displayType;
    private String refreshRate;
    private Boolean hdrSupport;

    // 显示规格
    private String colorTechnology;
    private Integer contrastRatio;
    private String viewingAngle;
    private String brightness;

    // 音频规格
    private String audioOutputPower;
    private String speakerConfiguration;

    // 连接规格
    private Integer hdmiPorts;
    private Integer usbPorts;
    private Boolean wifi;
    private Boolean bluetooth;
    private Boolean ethernet;

    // 电源规格
    private String powerConsumption;
    private String standbyPower;
    private String voltageRange;
    private String frequency;
    private Boolean energyStar;

    // 尺寸规格
    private String width;
    private String height;
    private String depth;
    private String weight;
    private String withStandWidth;    // 带底座宽度
    private String withStandHeight;   // 带底座高度
    private String withStandDepth;    // 带底座深度

    // 元数据
    private String dataSource;
    private Integer dataQualityScore;
    private String processingStatus;

    // 审计字段
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    private String createdBy;
    private String updatedBy;

    // 逻辑删除字段
    @TableLogic
    private Integer isDeleted;

    // 扩展字段（存储JSON格式的额外特性）
    private String additionalFeatures;

    // 如果需要添加其他字段的getter/setter
    // ... 已有的getter/setter方法

    // 以下是IDE可能自动生成的getter/setter，如果@Data注解已经生成，则不需要手动添加

    // public Long getId() { return id; }
    // public void setId(Long id) { this.id = id; }
    // ... 其他字段的getter/setter
}
