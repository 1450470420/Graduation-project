package com.xypt.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xypt.entity.User;
import org.apache.ibatis.annotations.Mapper;
/** User Mapper接口，继承BaseMapper获得基础CRUD功能 */
@Mapper
public interface UserMapper extends BaseMapper<User> {}
