package com.xypt.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xypt.entity.CourierApplication;
import org.apache.ibatis.annotations.Mapper;
/** CourierApplication Mapper接口，继承BaseMapper获得基础CRUD功能 */
@Mapper
public interface CourierApplicationMapper extends BaseMapper<CourierApplication> {}
