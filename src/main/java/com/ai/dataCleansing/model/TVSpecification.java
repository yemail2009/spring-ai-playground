package com.ai.dataCleansing.model;

import lombok.Data;

import java.util.Map;

@Data
public class TVSpecification {
    private String modelNumber;
    private String brand;
    private String series;
    private ScreenSpec screen;
    private DisplaySpec display;
    private AudioSpec audio;
    private ConnectivitySpec connectivity;
    private PowerSpec power;
    private DimensionSpec dimension;
    private Map<String, String> additionalFeatures;
}
