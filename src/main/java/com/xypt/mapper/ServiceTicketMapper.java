package com.xypt.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xypt.entity.ServiceTicket;
import org.apache.ibatis.annotations.Mapper;
/** ServiceTicket Mapper接口，继承BaseMapper获得基础CRUD功能 */
@Mapper
public interface ServiceTicketMapper extends BaseMapper<ServiceTicket> {}
