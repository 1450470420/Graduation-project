package com.xypt.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
/** 订单评价实体类 */
@Data @TableName("evaluation")
public class Evaluation {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long orderId;
    private Long studentId;
    private Long courierId;
    /** 星级1-5 */
    private Integer rating;
    private String content;
    private String images;
    private LocalDateTime createTime;
}
