package com.xypt.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
/** 退款申请实体类 */
@Data @TableName("order_refund")
public class OrderRefund {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long orderId;
    private Long studentId;
    private String reason;
    /** 状态：0=待审核 1=已通过 2=已驳回 */
    private Integer status;
    private String adminRemark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
