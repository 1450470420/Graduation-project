package com.xypt.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xypt.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;
/** OperationLog Mapper接口，继承BaseMapper获得基础CRUD功能 */
@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLog> {}
