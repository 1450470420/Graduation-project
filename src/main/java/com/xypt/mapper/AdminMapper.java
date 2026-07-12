package com.xypt.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xypt.entity.Admin;
import org.apache.ibatis.annotations.Mapper;
/** Admin Mapper接口，继承BaseMapper获得基础CRUD功能 */
@Mapper
public interface AdminMapper extends BaseMapper<Admin> {}
