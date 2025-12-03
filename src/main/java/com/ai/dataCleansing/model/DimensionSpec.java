package com.ai.dataCleansing.model;

import lombok.Data;

@Data
public class DimensionSpec {
    private String width;          // 宽度，如 "123.5cm"
    private String height;         // 高度，如 "75.2cm"
    private String depth;          // 厚度，如 "8.5cm"
    private String weight;         // 重量，如 "25.3kg"
    private String withStandWidth; // 带底座宽度
    private String withStandHeight; // 带底座高度
    private String withStandDepth;  // 带底座深度

    // 无参构造函数
    public DimensionSpec() {
    }

    // 带主要参数的构造函数
    public DimensionSpec(String width, String height, String depth, String weight) {
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.weight = weight;
    }

    // 全参构造函数
    public DimensionSpec(String width, String height, String depth, String weight,
                         String withStandWidth, String withStandHeight, String withStandDepth) {
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.weight = weight;
        this.withStandWidth = withStandWidth;
        this.withStandHeight = withStandHeight;
        this.withStandDepth = withStandDepth;
    }

    // Getter和Setter方法
    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getDepth() {
        return depth;
    }

    public void setDepth(String depth) {
        this.depth = depth;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getWithStandWidth() {
        return withStandWidth;
    }

    public void setWithStandWidth(String withStandWidth) {
        this.withStandWidth = withStandWidth;
    }

    public String getWithStandHeight() {
        return withStandHeight;
    }

    public void setWithStandHeight(String withStandHeight) {
        this.withStandHeight = withStandHeight;
    }

    public String getWithStandDepth() {
        return withStandDepth;
    }

    public void setWithStandDepth(String withStandDepth) {
        this.withStandDepth = withStandDepth;
    }

    @Override
    public String toString() {
        return "DimensionSpec{" +
                "width='" + width + '\'' +
                ", height='" + height + '\'' +
                ", depth='" + depth + '\'' +
                ", weight='" + weight + '\'' +
                ", withStandWidth='" + withStandWidth + '\'' +
                ", withStandHeight='" + withStandHeight + '\'' +
                ", withStandDepth='" + withStandDepth + '\'' +
                '}';
    }
}
