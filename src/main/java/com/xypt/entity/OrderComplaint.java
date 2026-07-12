package com.xypt.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
/** 投诉实体类 */
@Data @TableName("order_complaint")
public class OrderComplaint {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long orderId;
    private Long studentId;
    /** 投诉类型：超时/服务差/丢件/其他 */
    private String type;
    private String content;
    private String images;
    /** 状态：0=待处理 1=处理中 2=已处理 */
    private Integer status;
    private String result;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
