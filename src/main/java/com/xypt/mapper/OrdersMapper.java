package com.xypt.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xypt.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
/** Orders Mapper接口，继承BaseMapper获得基础CRUD功能 */
@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {}
