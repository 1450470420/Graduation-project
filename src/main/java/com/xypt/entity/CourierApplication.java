package com.xypt.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
/** 跑腿员入驻申请实体类 */
@Data @TableName("courier_application")
public class CourierApplication {
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 申请用户ID */
    private Long userId;
    /** 真实姓名 */
    private String realName;
    /** 学号 */
    private String studentId;
    /** 联系电话 */
    private String phone;
    /** 身份证号 */
    private String idCardNo;
    /** 申请备注 */
    private String remark;
    /** 状态：0=待审核 1=已通过 2=已驳回 */
    private Integer status;
    /** 驳回原因 */
    private String rejectReason;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
