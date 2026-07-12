package com.xypt.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xypt.entity.Withdrawal;
import org.apache.ibatis.annotations.Mapper;
/** Withdrawal Mapper接口，继承BaseMapper获得基础CRUD功能 */
@Mapper
public interface WithdrawalMapper extends BaseMapper<Withdrawal> {}
