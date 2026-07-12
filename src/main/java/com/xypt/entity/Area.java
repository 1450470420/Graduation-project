package com.xypt.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
/** 校园区域实体类 */
@Data @TableName("area")
public class Area {
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 区域名称 */
    private String name;
    /** 区域类型：campus/dorm/teaching/other */
    private String type;
    /** 父区域ID（0=顶级）*/
    private Long parentId;
    /** 状态：1=启用 0=禁用 */
    private Integer status;
    /** 排序权重 */
    private Integer sortOrder;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
