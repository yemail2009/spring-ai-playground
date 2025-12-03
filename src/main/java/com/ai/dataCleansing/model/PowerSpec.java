package com.ai.dataCleansing.model;

import lombok.Data;

@Data
public class PowerSpec {
    private String powerConsumption;    // 功耗，如 "120W"
    private String standbyPower;        // 待机功耗，如 "0.5W"
    private String voltageRange;        // 电压范围，如 "100-240V"
    private String frequency;           // 频率，如 "50/60Hz"
    private Boolean energyStar;         // 是否节能认证

    // 无参构造函数
    public PowerSpec() {
    }

    // 全参构造函数
    public PowerSpec(String powerConsumption, String standbyPower, String voltageRange,
                     String frequency, Boolean energyStar) {
        this.powerConsumption = powerConsumption;
        this.standbyPower = standbyPower;
        this.voltageRange = voltageRange;
        this.frequency = frequency;
        this.energyStar = energyStar;
    }

    // Getter和Setter方法
    public String getPowerConsumption() {
        return powerConsumption;
    }

    public void setPowerConsumption(String powerConsumption) {
        this.powerConsumption = powerConsumption;
    }

    public String getStandbyPower() {
        return standbyPower;
    }

    public void setStandbyPower(String standbyPower) {
        this.standbyPower = standbyPower;
    }

    public String getVoltageRange() {
        return voltageRange;
    }

    public void setVoltageRange(String voltageRange) {
        this.voltageRange = voltageRange;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public Boolean getEnergyStar() {
        return energyStar;
    }

    public void setEnergyStar(Boolean energyStar) {
        this.energyStar = energyStar;
    }

    @Override
    public String toString() {
        return "PowerSpec{" +
                "powerConsumption='" + powerConsumption + '\'' +
                ", standbyPower='" + standbyPower + '\'' +
                ", voltageRange='" + voltageRange + '\'' +
                ", frequency='" + frequency + '\'' +
                ", energyStar=" + energyStar +
                '}';
    }
}
