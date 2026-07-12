package com.xypt.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xypt.entity.Evaluation;
import org.apache.ibatis.annotations.Mapper;
/** Evaluation Mapper接口，继承BaseMapper获得基础CRUD功能 */
@Mapper
public interface EvaluationMapper extends BaseMapper<Evaluation> {}
