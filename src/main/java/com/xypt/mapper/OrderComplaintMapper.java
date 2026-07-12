package com.xypt.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xypt.entity.OrderComplaint;
import org.apache.ibatis.annotations.Mapper;
/** OrderComplaint Mapper接口，继承BaseMapper获得基础CRUD功能 */
@Mapper
public interface OrderComplaintMapper extends BaseMapper<OrderComplaint> {}
