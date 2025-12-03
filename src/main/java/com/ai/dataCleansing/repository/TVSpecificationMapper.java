package com.ai.dataCleansing.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.UpdateProvider;
import org.mapstruct.Mapper;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface TVSpecificationMapper extends BaseMapper<TVSpecificationEntity> {

    /**
     * 根据型号查询
     */
    @Select("SELECT * FROM tv_specification WHERE model_number = #{modelNumber} AND is_deleted = 0")
    TVSpecificationEntity selectByModelNumber(@Param("modelNumber") String modelNumber);

    /**
     * 根据品牌查询
     */
    @Select("SELECT * FROM tv_specification WHERE brand = #{brand} AND is_deleted = 0 ORDER BY create_time DESC")
    List<TVSpecificationEntity> selectByBrand(@Param("brand") String brand);

    /**
     * 根据尺寸范围查询
     */
    @Select("SELECT * FROM tv_specification WHERE screen_size BETWEEN #{minSize} AND #{maxSize} AND is_deleted = 0")
    List<TVSpecificationEntity> selectByScreenSizeRange(@Param("minSize") Double minSize,
                                                        @Param("maxSize") Double maxSize);

    /**
     * 检查型号是否存在
     */
    @Select("SELECT COUNT(*) FROM tv_specification WHERE model_number = #{modelNumber} AND is_deleted = 0")
    boolean existsByModelNumber(@Param("modelNumber") String modelNumber);

    /**
     * 批量插入
     */
    int insertBatch(@Param("list") List<TVSpecificationEntity> entities);

    /**
     * 根据型号更新
     */
    @UpdateProvider(type = TVSpecificationSqlProvider.class, method = "updateByModelNumber")
    int updateByModelNumber(@Param("entity") TVSpecificationEntity entity);

    /**
     * 逻辑删除
     */
    @Update("UPDATE tv_specification SET is_deleted = 1, update_time = NOW() WHERE id = #{id}")
    int logicDelete(@Param("id") Long id);

    /**
     * 根据数据质量分数查询
     */
    @Select("SELECT * FROM tv_specification WHERE data_quality_score >= #{minScore} AND is_deleted = 0")
    List<TVSpecificationEntity> selectByQualityScore(@Param("minScore") Integer minScore);

    /**
     * 查询需要重新处理的数据
     */
    @Select("SELECT * FROM tv_specification WHERE processing_status IN ('FAILED', 'NEEDS_REVIEW') AND is_deleted = 0")
    List<TVSpecificationEntity> selectNeedReprocessing();

    /**
     * 统计各品牌数量
     */
    @Select("SELECT brand, COUNT(*) as count FROM tv_specification WHERE is_deleted = 0 GROUP BY brand")
    List<Map<String, Object>> countByBrand();
}
