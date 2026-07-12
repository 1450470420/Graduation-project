package com.xypt.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
/** 聊天消息实体类 */
@Data @TableName("chat_message")
public class ChatMessage {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long orderId;
    private Long senderId;
    private Long receiverId;
    private String content;
    private Integer isRead;
    private LocalDateTime createTime;
}
