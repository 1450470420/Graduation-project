package com.xypt.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
/** 客服工单实体类 */
@Data @TableName("service_ticket")
public class ServiceTicket {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String title;
    private String content;
    /** 状态：0=待处理 1=处理中 2=已关闭 */
    private Integer status;
    private String reply;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
