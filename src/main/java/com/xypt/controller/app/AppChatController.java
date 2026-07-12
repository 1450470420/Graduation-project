package com.xypt.controller.app;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xypt.common.Result;
import com.xypt.entity.ChatMessage;
import com.xypt.service.ChatMessageService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/** 订单内聊天接口 */
@RestController
@RequestMapping("/api/app/chat")
public class AppChatController {
    @Autowired ChatMessageService chatMessageService;

    /** 获取聊天记录 GET /api/app/chat/{orderId} */
    @GetMapping("/{orderId}")
    public Result<List<ChatMessage>> history(@PathVariable Long orderId, HttpSession session) {
        if (session.getAttribute("userId")==null) return Result.unauthorized();
        List<ChatMessage> messages = chatMessageService.list(
            new LambdaQueryWrapper<ChatMessage>().eq(ChatMessage::getOrderId,orderId).orderByAsc(ChatMessage::getCreateTime));
        Long userId = (Long) session.getAttribute("userId");
        messages.forEach(m -> { if (!userId.equals(m.getSenderId())&&m.getIsRead()==0) { m.setIsRead(1); chatMessageService.updateById(m); } });
        return Result.success(messages);
    }

    /**
     * 增量拉取新消息（轮询用）
     * GET /api/app/chat/{orderId}/new?lastId=xxx
     * 只返回ID大于lastId的新消息，减少数据传输
     */
    @GetMapping("/{orderId}/new")
    public Result<Map<String, Object>> newMessages(
            @PathVariable Long orderId,
            @RequestParam(required = false, defaultValue = "0") Long lastId,
            HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return Result.unauthorized();

        LambdaQueryWrapper<ChatMessage> wrapper = new LambdaQueryWrapper<ChatMessage>()
                .eq(ChatMessage::getOrderId, orderId)
                .gt(ChatMessage::getId, lastId)
                .orderByAsc(ChatMessage::getCreateTime);
        List<ChatMessage> newMsgs = chatMessageService.list(wrapper);

        newMsgs.forEach(m -> {
            if (!userId.equals(m.getSenderId()) && m.getIsRead() == 0) {
                m.setIsRead(1);
                chatMessageService.updateById(m);
            }
        });

        Map<String, Object> result = new HashMap<>();
        result.put("messages", newMsgs);
        result.put("hasNew", !newMsgs.isEmpty());
        return Result.success(result);
    }

    /** 发送消息 POST /api/app/chat */
    @PostMapping
    public Result<String> send(@RequestBody ChatMessage message, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId==null) return Result.unauthorized();
        message.setSenderId(userId); message.setIsRead(0); message.setCreateTime(LocalDateTime.now());
        chatMessageService.save(message);
        return Result.success("发送成功");
    }
}
