package com.ai.dataCleansing.common;

import com.ai.dataCleansing.model.*;
import com.ai.dataCleansing.repository.TVSpecificationEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class TVSpecificationConverter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 业务对象转实体对象
     */
    public TVSpecificationEntity toEntity(TVSpecification spec) {
        if (spec == null) return null;

        TVSpecificationEntity entity = new TVSpecificationEntity();

        // 基本信息
        entity.setModelNumber(spec.getModelNumber());
        entity.setBrand(spec.getBrand());
        entity.setSeries(spec.getSeries());

        // 屏幕规格
        if (spec.getScreen() != null) {
            ScreenSpec screen = spec.getScreen();
            entity.setScreenSize(screen.getSize());
            entity.setScreenResolution(screen.getResolution());
            entity.setDisplayType(screen.getDisplayType());
            entity.setRefreshRate(screen.getRefreshRate());
            entity.setHdrSupport(screen.getHdrSupport());
        }

        // 显示规格
        if (spec.getDisplay() != null) {
            DisplaySpec display = spec.getDisplay();
            entity.setColorTechnology(display.getColorTechnology());
            entity.setContrastRatio(display.getContrastRatio());
            entity.setViewingAngle(display.getViewingAngle());
            entity.setBrightness(display.getBrightness());
        }

        // 音频规格
        if (spec.getAudio() != null) {
            AudioSpec audio = spec.getAudio();
            entity.setAudioOutputPower(audio.getOutputPower());
            entity.setSpeakerConfiguration(audio.getSpeakerConfiguration());
        }

        // 连接规格
        if (spec.getConnectivity() != null) {
            ConnectivitySpec connectivity = spec.getConnectivity();
//            entity.setHdmiPorts(connectivity.getHdmiPorts());
            entity.setUsbPorts(connectivity.getUsbPorts());
            entity.setWifi(connectivity.getWifi());
            entity.setBluetooth(connectivity.getBluetooth());
            entity.setEthernet(connectivity.getEthernet());
        }

        // 电源规格（新增）
        if (spec.getPower() != null) {
            PowerSpec power = spec.getPower();
            entity.setPowerConsumption(power.getPowerConsumption());
            entity.setStandbyPower(power.getStandbyPower());
            entity.setVoltageRange(power.getVoltageRange());
            entity.setFrequency(power.getFrequency());
            entity.setEnergyStar(power.getEnergyStar());
        }

        // 尺寸规格（新增）
        if (spec.getDimension() != null) {
            DimensionSpec dimension = spec.getDimension();
            entity.setWidth(dimension.getWidth());
            entity.setHeight(dimension.getHeight());
            entity.setDepth(dimension.getDepth());
            entity.setWeight(dimension.getWeight());
            entity.setWithStandWidth(dimension.getWithStandWidth());
            entity.setWithStandHeight(dimension.getWithStandHeight());
            entity.setWithStandDepth(dimension.getWithStandDepth());
        }

        // 额外特性转为JSON
        if (spec.getAdditionalFeatures() != null && !spec.getAdditionalFeatures().isEmpty()) {
            try {
                entity.setAdditionalFeatures(objectMapper.writeValueAsString(spec.getAdditionalFeatures()));
            } catch (JsonProcessingException e) {
                // 记录日志但不抛出异常
                entity.setAdditionalFeatures("{}");
            }
        }

        return entity;
    }

    /**
     * 实体对象转业务对象
     */
    public TVSpecification toDomain(TVSpecificationEntity entity) {
        if (entity == null) return null;

        TVSpecification spec = new TVSpecification();

        // 基本信息
        spec.setModelNumber(entity.getModelNumber());
        spec.setBrand(entity.getBrand());
        spec.setSeries(entity.getSeries());

        // 屏幕规格
        ScreenSpec screen = new ScreenSpec();
        screen.setSize(entity.getScreenSize());
        screen.setResolution(entity.getScreenResolution());
        screen.setDisplayType(entity.getDisplayType());
        screen.setRefreshRate(entity.getRefreshRate());
        screen.setHdrSupport(entity.getHdrSupport());
        spec.setScreen(screen);

        // 显示规格
        DisplaySpec display = new DisplaySpec();
        display.setColorTechnology(entity.getColorTechnology());
        display.setContrastRatio(entity.getContrastRatio());
        display.setViewingAngle(entity.getViewingAngle());
        display.setBrightness(entity.getBrightness());
        spec.setDisplay(display);

        // 音频规格
        AudioSpec audio = new AudioSpec();
        audio.setOutputPower(entity.getAudioOutputPower());
        audio.setSpeakerConfiguration(entity.getSpeakerConfiguration());
        spec.setAudio(audio);

        // 连接规格
        ConnectivitySpec connectivity = new ConnectivitySpec();
        connectivity.setHdmiPorts(entity.getHdmiPorts());
        connectivity.setUsbPorts(entity.getUsbPorts());
        connectivity.setWifi(entity.getWifi());
        connectivity.setBluetooth(entity.getBluetooth());
        connectivity.setEthernet(entity.getEthernet());
        spec.setConnectivity(connectivity);

        // 电源规格（新增）
        PowerSpec power = new PowerSpec();
        power.setPowerConsumption(entity.getPowerConsumption());
        power.setStandbyPower(entity.getStandbyPower());
        power.setVoltageRange(entity.getVoltageRange());
        power.setFrequency(entity.getFrequency());
        power.setEnergyStar(entity.getEnergyStar());
        spec.setPower(power);

        // 尺寸规格（新增）
        DimensionSpec dimension = new DimensionSpec();
        dimension.setWidth(entity.getWidth());
        dimension.setHeight(entity.getHeight());
        dimension.setDepth(entity.getDepth());
        dimension.setWeight(entity.getWeight());
        dimension.setWithStandWidth(entity.getWithStandWidth());
        dimension.setWithStandHeight(entity.getWithStandHeight());
        dimension.setWithStandDepth(entity.getWithStandDepth());
        spec.setDimension(dimension);

        // 解析JSON格式的额外特性
        if (entity.getAdditionalFeatures() != null && !entity.getAdditionalFeatures().isEmpty()) {
            try {
                Map<String, String> additionalFeatures = objectMapper.readValue(
                        entity.getAdditionalFeatures(),
                        new TypeReference<Map<String, String>>() {}
                );
                spec.setAdditionalFeatures(additionalFeatures);
            } catch (JsonProcessingException e) {
                spec.setAdditionalFeatures(new HashMap<>());
            }
        }

        return spec;
    }

    /**
     * 更新实体对象
     */
    public void updateEntity(TVSpecificationEntity entity, TVSpecification spec) {
        if (spec == null || entity == null) return;

        // 只更新非空字段
        if (spec.getModelNumber() != null) {
            entity.setModelNumber(spec.getModelNumber());
        }

        if (spec.getBrand() != null) {
            entity.setBrand(spec.getBrand());
        }

        if (spec.getSeries() != null) {
            entity.setSeries(spec.getSeries());
        }

        // 更新屏幕规格
        if (spec.getScreen() != null) {
            ScreenSpec screen = spec.getScreen();
            if (screen.getSize() != null) entity.setScreenSize(screen.getSize());
            if (screen.getResolution() != null) entity.setScreenResolution(screen.getResolution());
            if (screen.getDisplayType() != null) entity.setDisplayType(screen.getDisplayType());
            if (screen.getRefreshRate() != null) entity.setRefreshRate(screen.getRefreshRate());
            if (screen.getHdrSupport() != null) entity.setHdrSupport(screen.getHdrSupport());
        }

        // 更新显示规格
        if (spec.getDisplay() != null) {
            DisplaySpec display = spec.getDisplay();
            if (display.getColorTechnology() != null) entity.setColorTechnology(display.getColorTechnology());
            if (display.getContrastRatio() != null) entity.setContrastRatio(display.getContrastRatio());
            if (display.getViewingAngle() != null) entity.setViewingAngle(display.getViewingAngle());
            if (display.getBrightness() != null) entity.setBrightness(display.getBrightness());
        }

        // 更新连接规格
        if (spec.getConnectivity() != null) {
            ConnectivitySpec connectivity = spec.getConnectivity();
            if (connectivity.getHdmiPorts() != null) entity.setHdmiPorts(connectivity.getHdmiPorts());
            if (connectivity.getUsbPorts() != null) entity.setUsbPorts(connectivity.getUsbPorts());
            if (connectivity.getWifi() != null) entity.setWifi(connectivity.getWifi());
            if (connectivity.getBluetooth() != null) entity.setBluetooth(connectivity.getBluetooth());
            if (connectivity.getEthernet() != null) entity.setEthernet(connectivity.getEthernet());
        }

        // 更新电源规格（新增）
        if (spec.getPower() != null) {
            PowerSpec power = spec.getPower();
            if (power.getPowerConsumption() != null) entity.setPowerConsumption(power.getPowerConsumption());
            if (power.getStandbyPower() != null) entity.setStandbyPower(power.getStandbyPower());
            if (power.getVoltageRange() != null) entity.setVoltageRange(power.getVoltageRange());
            if (power.getFrequency() != null) entity.setFrequency(power.getFrequency());
            if (power.getEnergyStar() != null) entity.setEnergyStar(power.getEnergyStar());
        }

        // 更新尺寸规格（新增）
        if (spec.getDimension() != null) {
            DimensionSpec dimension = spec.getDimension();
            if (dimension.getWidth() != null) entity.setWidth(dimension.getWidth());
            if (dimension.getHeight() != null) entity.setHeight(dimension.getHeight());
            if (dimension.getDepth() != null) entity.setDepth(dimension.getDepth());
            if (dimension.getWeight() != null) entity.setWeight(dimension.getWeight());
            if (dimension.getWithStandWidth() != null) entity.setWithStandWidth(dimension.getWithStandWidth());
            if (dimension.getWithStandHeight() != null) entity.setWithStandHeight(dimension.getWithStandHeight());
            if (dimension.getWithStandDepth() != null) entity.setWithStandDepth(dimension.getWithStandDepth());
        }

        // 更新额外特性
        if (spec.getAdditionalFeatures() != null && !spec.getAdditionalFeatures().isEmpty()) {
            try {
                entity.setAdditionalFeatures(objectMapper.writeValueAsString(spec.getAdditionalFeatures()));
            } catch (JsonProcessingException e) {
                // 记录日志但不抛出异常
            }
        }

        entity.setUpdateTime(new Date());
    }
}
