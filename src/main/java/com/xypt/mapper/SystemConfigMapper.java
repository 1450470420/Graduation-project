package com.xypt.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xypt.entity.SystemConfig;
import org.apache.ibatis.annotations.Mapper;
/** SystemConfig Mapper接口，继承BaseMapper获得基础CRUD功能 */
@Mapper
public interface SystemConfigMapper extends BaseMapper<SystemConfig> {}
