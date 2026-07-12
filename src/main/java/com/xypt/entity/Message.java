package com.xypt.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
/** 消息通知实体类 */
@Data @TableName("message")
public class Message {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String title;
    private String content;
    /** 类型：order/announcement/system */
    private String type;
    /** 是否已读：0=未读 1=已读 */
    private Integer isRead;
    private LocalDateTime createTime;
}
