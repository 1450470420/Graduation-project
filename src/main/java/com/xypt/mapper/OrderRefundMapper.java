package com.xypt.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xypt.entity.OrderRefund;
import org.apache.ibatis.annotations.Mapper;
/** OrderRefund Mapper接口，继承BaseMapper获得基础CRUD功能 */
@Mapper
public interface OrderRefundMapper extends BaseMapper<OrderRefund> {}
