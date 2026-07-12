package com.xypt.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xypt.entity.ReputationRecord;
import org.apache.ibatis.annotations.Mapper;
/** ReputationRecord Mapper接口，继承BaseMapper获得基础CRUD功能 */
@Mapper
public interface ReputationRecordMapper extends BaseMapper<ReputationRecord> {}
