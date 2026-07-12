package com.xypt.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
/** 取件点实体类 */
@Data @TableName("pickup_point")
public class PickupPoint {
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 取件点名称 */
    private String name;
    /** 类型：快递柜/菜鸟驿站/自提点 */
    private String type;
    /** 所属区域ID */
    private Long areaId;
    /** 详细地址 */
    private String address;
    /** 状态：1=启用 0=禁用 */
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
