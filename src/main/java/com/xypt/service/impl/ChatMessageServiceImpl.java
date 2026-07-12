package com.xypt.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xypt.entity.ChatMessage;
import com.xypt.mapper.ChatMessageMapper;
import com.xypt.service.ChatMessageService;
import org.springframework.stereotype.Service;
/** ChatMessage Service实现类 */
@Service
public class ChatMessageServiceImpl extends ServiceImpl<ChatMessageMapper, ChatMessage> implements ChatMessageService {}
