package com.xypt.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
/** 用户常用地址实体类 */
@Data @TableName("user_address")
public class UserAddress {
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 用户ID */
    private Long userId;
    /** 地址标签 */
    private String label;
    /** 详细地址 */
    private String address;
    /** 是否默认：0=否 1=是 */
    private Integer isDefault;
    private LocalDateTime createTime;
}
