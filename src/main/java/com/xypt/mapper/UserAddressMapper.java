package com.xypt.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xypt.entity.UserAddress;
import org.apache.ibatis.annotations.Mapper;
/** UserAddress Mapper接口，继承BaseMapper获得基础CRUD功能 */
@Mapper
public interface UserAddressMapper extends BaseMapper<UserAddress> {}
