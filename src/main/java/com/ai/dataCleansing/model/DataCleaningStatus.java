package com.ai.dataCleansing.model;

import lombok.Data;

public enum DataCleaningStatus {
    INITIALIZED("初始化", 0),
    EXTRACTED("已提取", 1),
    STANDARDIZED("已标准化", 2),
    VALIDATED("已验证", 3),
    SUCCESS("处理成功", 4),
    UPDATED("已更新", 5),
    CORRECTED("已修正", 6),
    VALIDATION_FAILED("验证失败", 7),
    EXTRACTION_FAILED("提取失败", 8),
    STANDARDIZATION_FAILED("标准化失败", 9),
    FAILED("处理失败", 10),
    DUPLICATE("数据重复", 11),
    NEEDS_REVIEW("需要人工审核", 12),
    PARTIAL_SUCCESS("部分成功", 13);

    private final String description;
    private final int code;

    DataCleaningStatus(String description, int code) {
        this.description = description;
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public int getCode() {
        return code;
    }

    public boolean isSuccess() {
        return this == SUCCESS ||
                this == UPDATED ||
                this == CORRECTED ||
                this == PARTIAL_SUCCESS;
    }

    public boolean isFailed() {
        return this == FAILED ||
                this == EXTRACTION_FAILED ||
                this == STANDARDIZATION_FAILED ||
                this == VALIDATION_FAILED;
    }

    public boolean isWarning() {
        return this == NEEDS_REVIEW ||
                this == DUPLICATE;
    }

    public static DataCleaningStatus fromCode(int code) {
        for (DataCleaningStatus status : values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        return FAILED;
    }

    @Override
    public String toString() {
        return this.name() + "(" + description + ")";
    }
}
