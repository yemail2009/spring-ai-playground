package com.ai.dataCleansing.repository;

import org.apache.ibatis.jdbc.SQL;
import org.springframework.data.repository.query.Param;

import java.util.List;

public class TVSpecificationSqlProvider {

    /**
     * 根据型号更新的动态SQL
     */
    public String updateByModelNumber(@Param("entity") TVSpecificationEntity entity) {
        return new SQL() {{
            UPDATE("tv_specification");

            // 动态设置要更新的字段
            if (entity.getBrand() != null) {
                SET("brand = #{entity.brand}");
            }
            if (entity.getSeries() != null) {
                SET("series = #{entity.series}");
            }
            if (entity.getScreenSize() != null) {
                SET("screen_size = #{entity.screenSize}");
            }
            if (entity.getScreenResolution() != null) {
                SET("screen_resolution = #{entity.screenResolution}");
            }
            if (entity.getDisplayType() != null) {
                SET("display_type = #{entity.displayType}");
            }
            if (entity.getRefreshRate() != null) {
                SET("refresh_rate = #{entity.refreshRate}");
            }
            if (entity.getHdrSupport() != null) {
                SET("hdr_support = #{entity.hdrSupport}");
            }
            if (entity.getColorTechnology() != null) {
                SET("color_technology = #{entity.colorTechnology}");
            }
            if (entity.getContrastRatio() != null) {
                SET("contrast_ratio = #{entity.contrastRatio}");
            }
            if (entity.getViewingAngle() != null) {
                SET("viewing_angle = #{entity.viewingAngle}");
            }
            if (entity.getBrightness() != null) {
                SET("brightness = #{entity.brightness}");
            }
//            if (entity.getHdiPorts() != null) {
//                SET("hdi_ports = #{entity.hdiPorts}");
//            }
            if (entity.getUsbPorts() != null) {
                SET("usb_ports = #{entity.usbPorts}");
            }
            if (entity.getWifi() != null) {
                SET("wifi = #{entity.wifi}");
            }
            if (entity.getBluetooth() != null) {
                SET("bluetooth = #{entity.bluetooth}");
            }
            if (entity.getEthernet() != null) {
                SET("ethernet = #{entity.ethernet}");
            }
            if (entity.getAdditionalFeatures() != null) {
                SET("additional_features = #{entity.additionalFeatures}");
            }

            SET("update_time = NOW()");
            WHERE("model_number = #{entity.modelNumber}");
            WHERE("is_deleted = 0");
        }}.toString();
    }

    /**
     * 批量插入的SQL
     */
    public String insertBatch(@Param("list") List<TVSpecificationEntity> entities) {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO tv_specification (");
        sql.append("id, model_number, brand, series, screen_size, screen_resolution, ");
        sql.append("display_type, refresh_rate, hdr_support, color_technology, ");
        sql.append("contrast_ratio, viewing_angle, brightness, audio_output_power, ");
        sql.append("speaker_configuration, hdi_ports, usb_ports, wifi, bluetooth, ");
        sql.append("ethernet, power_consumption, standby_power, width, height, ");
        sql.append("depth, weight, additional_features, data_source, data_quality_score, ");
        sql.append("processing_status, create_time, update_time, created_by, updated_by");
        sql.append(") VALUES ");

        for (int i = 0; i < entities.size(); i++) {
            TVSpecificationEntity entity = entities.get(i);
            if (i > 0) sql.append(", ");

            sql.append("(");
            sql.append(entity.getId() != null ? entity.getId() : "NULL").append(", ");
            sql.append("'").append(escapeSql(entity.getModelNumber())).append("', ");
            sql.append("'").append(escapeSql(entity.getBrand())).append("', ");
            sql.append("'").append(escapeSql(entity.getSeries())).append("', ");
            sql.append(entity.getScreenSize() != null ? entity.getScreenSize() : "NULL").append(", ");
            sql.append("'").append(escapeSql(entity.getScreenResolution())).append("', ");
            sql.append("'").append(escapeSql(entity.getDisplayType())).append("', ");
            sql.append("'").append(escapeSql(entity.getRefreshRate())).append("', ");
            sql.append(entity.getHdrSupport() != null ? entity.getHdrSupport() : "NULL").append(", ");
            sql.append("'").append(escapeSql(entity.getColorTechnology())).append("', ");
            sql.append(entity.getContrastRatio() != null ? entity.getContrastRatio() : "NULL").append(", ");
            sql.append("'").append(escapeSql(entity.getViewingAngle())).append("', ");
            sql.append("'").append(escapeSql(entity.getBrightness())).append("', ");
            sql.append("'").append(escapeSql(entity.getAudioOutputPower())).append("', ");
            sql.append("'").append(escapeSql(entity.getSpeakerConfiguration())).append("', ");
//            sql.append(entity.getHdiPorts() != null ? entity.getHdiPorts() : "NULL").append(", ");
            sql.append(entity.getUsbPorts() != null ? entity.getUsbPorts() : "NULL").append(", ");
            sql.append(entity.getWifi() != null ? entity.getWifi() : "NULL").append(", ");
            sql.append(entity.getBluetooth() != null ? entity.getBluetooth() : "NULL").append(", ");
            sql.append(entity.getEthernet() != null ? entity.getEthernet() : "NULL").append(", ");
            sql.append("'").append(escapeSql(entity.getPowerConsumption())).append("', ");
            sql.append("'").append(escapeSql(entity.getStandbyPower())).append("', ");
            sql.append("'").append(escapeSql(entity.getWidth())).append("', ");
            sql.append("'").append(escapeSql(entity.getHeight())).append("', ");
            sql.append("'").append(escapeSql(entity.getDepth())).append("', ");
            sql.append("'").append(escapeSql(entity.getWeight())).append("', ");
            sql.append("'").append(escapeSql(entity.getAdditionalFeatures())).append("', ");
            sql.append("'").append(escapeSql(entity.getDataSource())).append("', ");
            sql.append(entity.getDataQualityScore() != null ? entity.getDataQualityScore() : "NULL").append(", ");
            sql.append("'").append(escapeSql(entity.getProcessingStatus())).append("', ");
            sql.append("NOW(), NOW(), ");
            sql.append("'").append(escapeSql(entity.getCreatedBy())).append("', ");
            sql.append("'").append(escapeSql(entity.getUpdatedBy())).append("'");
            sql.append(")");
        }

        return sql.toString();
    }

    private String escapeSql(String str) {
        if (str == null) return "";
        return str.replace("'", "''");
    }
}
