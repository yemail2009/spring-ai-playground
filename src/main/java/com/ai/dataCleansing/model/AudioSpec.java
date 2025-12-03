package com.ai.dataCleansing.model;

import java.util.ArrayList;
import java.util.List;

public class AudioSpec {
    private String outputPower;
    private String speakerConfiguration;
    private List<String> audioTechnologies;

    // 构造函数
    public AudioSpec() {
        this.audioTechnologies = new ArrayList<>();
    }

    // Getter和Setter
    public String getOutputPower() { return outputPower; }
    public void setOutputPower(String outputPower) { this.outputPower = outputPower; }

    public String getSpeakerConfiguration() { return speakerConfiguration; }
    public void setSpeakerConfiguration(String speakerConfiguration) {
        this.speakerConfiguration = speakerConfiguration;
    }

    public List<String> getAudioTechnologies() { return audioTechnologies; }
    public void setAudioTechnologies(List<String> audioTechnologies) {
        this.audioTechnologies = audioTechnologies != null ? audioTechnologies : new ArrayList<>();
    }
}
