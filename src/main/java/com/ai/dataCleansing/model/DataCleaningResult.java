package com.ai.dataCleansing.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DataCleaningResult {
    private DataCleaningStatus status;
    private TVSpecification processedData;
    private List<String> validationErrors;
    private List<String> warnings;
    private String errorMessage;
    private Date processTime;
    private Long processingDuration; // 毫秒

    // 构造函数
    public DataCleaningResult() {
        this.status = DataCleaningStatus.INITIALIZED;
        this.validationErrors = new ArrayList<>();
        this.warnings = new ArrayList<>();
        this.processTime = new Date();
    }

    public DataCleaningResult(DataCleaningStatus status) {
        this();
        this.status = status;
    }

    // Getter和Setter
    public DataCleaningStatus getStatus() {
        return status;
    }

    public void setStatus(DataCleaningStatus status) {
        this.status = status;
    }

    public TVSpecification getProcessedData() {
        return processedData;
    }

    public void setProcessedData(TVSpecification processedData) {
        this.processedData = processedData;
    }

    public List<String> getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(List<String> validationErrors) {
        this.validationErrors = validationErrors != null ? validationErrors : new ArrayList<>();
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<String> warnings) {
        this.warnings = warnings != null ? warnings : new ArrayList<>();
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Date getProcessTime() {
        return processTime;
    }

    public void setProcessTime(Date processTime) {
        this.processTime = processTime;
    }

    public Long getProcessingDuration() {
        return processingDuration;
    }

    public void setProcessingDuration(Long processingDuration) {
        this.processingDuration = processingDuration;
    }

    // 添加错误信息
    public void addValidationError(String error) {
        if (this.validationErrors == null) {
            this.validationErrors = new ArrayList<>();
        }
        this.validationErrors.add(error);
    }

    // 添加警告信息
    public void addWarning(String warning) {
        if (this.warnings == null) {
            this.warnings = new ArrayList<>();
        }
        this.warnings.add(warning);
    }

    // 是否成功
    public boolean isSuccess() {
        return status != null && status.isSuccess();
    }

    // 是否失败
    public boolean isFailed() {
        return status != null && status.isFailed();
    }

    // 是否有警告
    public boolean hasWarnings() {
        return warnings != null && !warnings.isEmpty();
    }

    // 是否有错误
    public boolean hasErrors() {
        return validationErrors != null && !validationErrors.isEmpty();
    }

    @Override
    public String toString() {
        return "DataCleaningResult{" +
                "status=" + status +
                ", hasErrors=" + hasErrors() +
                ", hasWarnings=" + hasWarnings() +
                ", errorMessage='" + errorMessage + '\'' +
                ", processTime=" + processTime +
                '}';
    }
}
