package com.xypt.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xypt.entity.PickupPoint;
import org.apache.ibatis.annotations.Mapper;
/** PickupPoint Mapper接口，继承BaseMapper获得基础CRUD功能 */
@Mapper
public interface PickupPointMapper extends BaseMapper<PickupPoint> {}
