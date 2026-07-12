package com.xypt.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xypt.entity.Message;
import org.apache.ibatis.annotations.Mapper;
/** Message Mapper接口，继承BaseMapper获得基础CRUD功能 */
@Mapper
public interface MessageMapper extends BaseMapper<Message> {}
