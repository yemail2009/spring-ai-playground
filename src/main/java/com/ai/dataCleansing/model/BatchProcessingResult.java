package com.ai.dataCleansing.model;

import java.util.ArrayList;
import java.util.List;

public class BatchProcessingResult {
    private int totalProcessed;
    private int successfulCount;
    private int failedCount;
    private List<DataCleaningResult> detailedResults;

    // 构造函数
    public BatchProcessingResult() {
        this.detailedResults = new ArrayList<>();
    }

    // Getter和Setter
    public int getTotalProcessed() { return totalProcessed; }
    public void setTotalProcessed(int totalProcessed) { this.totalProcessed = totalProcessed; }

    public int getSuccessfulCount() { return successfulCount; }
    public void setSuccessfulCount(int successfulCount) { this.successfulCount = successfulCount; }

    public int getFailedCount() { return failedCount; }
    public void setFailedCount(int failedCount) { this.failedCount = failedCount; }

    public List<DataCleaningResult> getDetailedResults() { return detailedResults; }
    public void setDetailedResults(List<DataCleaningResult> detailedResults) {
        this.detailedResults = detailedResults != null ? detailedResults : new ArrayList<>();
    }
}
