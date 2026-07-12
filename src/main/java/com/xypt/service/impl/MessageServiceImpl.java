package com.xypt.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xypt.entity.Message;
import com.xypt.mapper.MessageMapper;
import com.xypt.service.MessageService;
import org.springframework.stereotype.Service;
/** Message Service实现类 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {}
