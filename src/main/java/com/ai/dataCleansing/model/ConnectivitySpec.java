package com.ai.dataCleansing.model;

public class ConnectivitySpec {
    private Integer hdmiPorts;
    private Integer usbPorts;
    private Boolean wifi;
    private Boolean bluetooth;
    private Boolean ethernet;

    // 构造函数
    public ConnectivitySpec() {
        this.wifi = false;
        this.bluetooth = false;
        this.ethernet = false;
    }

    // Getter和Setter
    public Integer getHdmiPorts() { return hdmiPorts; }
    public void setHdmiPorts(Integer hdmiPorts) { this.hdmiPorts = hdmiPorts; }

    public Integer getUsbPorts() { return usbPorts; }
    public void setUsbPorts(Integer usbPorts) { this.usbPorts = usbPorts; }

    public Boolean getWifi() { return wifi; }
    public void setWifi(Boolean wifi) { this.wifi = wifi != null ? wifi : false; }

    public Boolean getBluetooth() { return bluetooth; }
    public void setBluetooth(Boolean bluetooth) { this.bluetooth = bluetooth != null ? bluetooth : false; }

    public Boolean getEthernet() { return ethernet; }
    public void setEthernet(Boolean ethernet) { this.ethernet = ethernet != null ? ethernet : false; }
}
