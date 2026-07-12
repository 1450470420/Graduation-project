package com.xypt.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xypt.entity.ChatMessage;
import org.apache.ibatis.annotations.Mapper;
/** ChatMessage Mapper接口，继承BaseMapper获得基础CRUD功能 */
@Mapper
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {}
