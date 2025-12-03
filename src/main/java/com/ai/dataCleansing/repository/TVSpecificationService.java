package com.ai.dataCleansing.repository;

import com.ai.dataCleansing.model.TVSpecification;

import java.util.List;
import java.util.Map;

public interface TVSpecificationService {
    boolean save(TVSpecification spec);
    boolean updateByModelNumber(TVSpecification spec);
    boolean deleteById(Long id);
    TVSpecification getByModelNumber(String modelNumber);
    List<TVSpecification> getByBrand(String brand);
    List<TVSpecification> getByScreenSizeRange(Double minSize, Double maxSize);
    boolean existsByModelNumber(String modelNumber);
    boolean batchSave(List<TVSpecification> specs);
    List<TVSpecification> getNeedReprocessing();
    Map<String, Long> countByBrand();
}
