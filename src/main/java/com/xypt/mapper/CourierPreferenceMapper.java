package com.xypt.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xypt.entity.CourierPreference;
import org.apache.ibatis.annotations.Mapper;
/** CourierPreference Mapper接口，继承BaseMapper获得基础CRUD功能 */
@Mapper
public interface CourierPreferenceMapper extends BaseMapper<CourierPreference> {}
