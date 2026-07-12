package com.xypt.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
/** 信誉分记录实体类 */
@Data @TableName("reputation_record")
public class ReputationRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    /** 变动分数（正=加分，负=扣分） */
    private Integer changeScore;
    private String reason;
    /** 类型：1=加分 2=扣分 */
    private Integer type;
    private String operator;
    private LocalDateTime createTime;
}
