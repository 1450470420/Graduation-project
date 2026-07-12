package com.xypt.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xypt.entity.BalanceRecord;
import org.apache.ibatis.annotations.Mapper;
/** BalanceRecord Mapper接口，继承BaseMapper获得基础CRUD功能 */
@Mapper
public interface BalanceRecordMapper extends BaseMapper<BalanceRecord> {}
