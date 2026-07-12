package com.xypt.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
/** 余额变动记录实体类 */
@Data @TableName("balance_record")
public class BalanceRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private BigDecimal amount;
    /** 类型：1=收入 2=支出 3=充值 */
    private Integer type;
    private String remark;
    private Long orderId;
    private LocalDateTime createTime;
}
