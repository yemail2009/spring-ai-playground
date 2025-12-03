package com.ai.dataCleansing.service;

import com.ai.dataCleansing.model.BatchProcessingResult;
import com.ai.dataCleansing.model.DataCleaningResult;
import com.ai.dataCleansing.model.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class BatchProcessingService {
    @Autowired
    private DataCleaningEngine cleaningEngine;

    public BatchProcessingResult processDocuments(List<Document> documents) {
        BatchProcessingResult result = new BatchProcessingResult();

        List<CompletableFuture<DataCleaningResult>> futures = documents.stream()
                .map(document -> CompletableFuture.supplyAsync(() ->
                        cleaningEngine.cleanAndExtractData(document.getContent())))
                .collect(Collectors.toList());

        // 等待所有任务完成
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        List<DataCleaningResult> results = futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

        result.setTotalProcessed(documents.size());
        result.setSuccessfulCount((int) results.stream()
                .filter(r -> r.getStatus().isSuccess()).count());
        result.setFailedCount((int) results.stream()
                .filter(r -> !r.getStatus().isSuccess()).count());
        result.setDetailedResults(results);

        return result;
    }
}
