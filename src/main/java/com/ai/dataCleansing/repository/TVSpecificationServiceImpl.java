package com.ai.dataCleansing.repository;

import com.ai.dataCleansing.common.TVSpecificationConverter;
import com.ai.dataCleansing.model.ConnectivitySpec;
import com.ai.dataCleansing.model.ScreenSpec;
import com.ai.dataCleansing.model.TVSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class TVSpecificationServiceImpl implements TVSpecificationService {
//    @Autowired
    private TVSpecificationMapper tvSpecificationMapper;

    @Autowired
    private TVSpecificationConverter converter;

    @Override
    public boolean save(TVSpecification spec) {
        TVSpecificationEntity entity = converter.toEntity(spec);
        if (entity == null) return false;

        // 设置默认值
        entity.setCreateTime(new Date());
        entity.setUpdateTime(new Date());
        entity.setProcessingStatus("PROCESSED");
        entity.setDataQualityScore(calculateQualityScore(spec));

        return tvSpecificationMapper.insert(entity) > 0;
    }

    @Override
    public boolean updateByModelNumber(TVSpecification spec) {
        if (spec == null || spec.getModelNumber() == null) return false;

        // 先查询现有数据
        TVSpecificationEntity existing = tvSpecificationMapper.selectByModelNumber(spec.getModelNumber());
        if (existing == null) return false;

        // 更新实体
        converter.updateEntity(existing, spec);
        existing.setDataQualityScore(calculateQualityScore(spec));

        return tvSpecificationMapper.updateByModelNumber(existing) > 0;
    }

    @Override
    public boolean deleteById(Long id) {
        return tvSpecificationMapper.logicDelete(id) > 0;
    }

    @Override
    public TVSpecification getByModelNumber(String modelNumber) {
        TVSpecificationEntity entity = tvSpecificationMapper.selectByModelNumber(modelNumber);
        return converter.toDomain(entity);
    }

    @Override
    public List<TVSpecification> getByBrand(String brand) {
        List<TVSpecificationEntity> entities = tvSpecificationMapper.selectByBrand(brand);
        return entities.stream()
                .map(converter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<TVSpecification> getByScreenSizeRange(Double minSize, Double maxSize) {
        List<TVSpecificationEntity> entities = tvSpecificationMapper.selectByScreenSizeRange(minSize, maxSize);
        return entities.stream()
                .map(converter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByModelNumber(String modelNumber) {
        return tvSpecificationMapper.existsByModelNumber(modelNumber);
    }

    @Override
    public boolean batchSave(List<TVSpecification> specs) {
        if (specs == null || specs.isEmpty()) return false;

        List<TVSpecificationEntity> entities = specs.stream()
                .map(spec -> {
                    TVSpecificationEntity entity = converter.toEntity(spec);
                    if (entity != null) {
                        entity.setCreateTime(new Date());
                        entity.setUpdateTime(new Date());
                        entity.setProcessingStatus("PROCESSED");
                        entity.setDataQualityScore(calculateQualityScore(spec));
                    }
                    return entity;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return tvSpecificationMapper.insertBatch(entities) > 0;
    }

    @Override
    public List<TVSpecification> getNeedReprocessing() {
        List<TVSpecificationEntity> entities = tvSpecificationMapper.selectNeedReprocessing();
        return entities.stream()
                .map(converter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Long> countByBrand() {
        List<Map<String, Object>> results = tvSpecificationMapper.countByBrand();
        return results.stream()
                .collect(Collectors.toMap(
                        map -> (String) map.get("brand"),
                        map -> ((Number) map.get("count")).longValue()
                ));
    }

    /**
     * 计算数据质量分数
     */
    private Integer calculateQualityScore(TVSpecification spec) {
        int score = 0;

        // 基础字段检查
        if (spec.getModelNumber() != null && !spec.getModelNumber().isEmpty()) score += 20;
        if (spec.getBrand() != null && !spec.getBrand().isEmpty()) score += 20;

        // 屏幕规格检查
        if (spec.getScreen() != null) {
            ScreenSpec screen = spec.getScreen();
            if (screen.getSize() != null) score += 15;
            if (screen.getResolution() != null && !screen.getResolution().isEmpty()) score += 15;
            if (screen.getDisplayType() != null && !screen.getDisplayType().isEmpty()) score += 10;
        }

        // 连接规格检查
        if (spec.getConnectivity() != null) {
            ConnectivitySpec connectivity = spec.getConnectivity();
            if (connectivity.getHdmiPorts() != null) score += 10;
            if (connectivity.getWifi() != null) score += 5;
        }

        // 限制最高分
        return Math.min(score, 100);
    }
}
