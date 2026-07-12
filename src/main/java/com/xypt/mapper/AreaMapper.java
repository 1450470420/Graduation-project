package com.xypt.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xypt.entity.Area;
import org.apache.ibatis.annotations.Mapper;
/** Area Mapper接口，继承BaseMapper获得基础CRUD功能 */
@Mapper
public interface AreaMapper extends BaseMapper<Area> {}
