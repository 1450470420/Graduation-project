package com.xypt.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
/** 提现申请实体类 */
@Data @TableName("withdrawal")
public class Withdrawal {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long courierId;
    private BigDecimal amount;
    /** 收款方式：微信/支付宝/银行卡 */
    private String paymentMethod;
    private String accountInfo;
    /** 状态：0=待审核 1=已通过 2=已驳回 */
    private Integer status;
    private String adminRemark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
