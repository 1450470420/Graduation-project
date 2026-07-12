package com.xypt.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
/** 跑腿员偏好设置实体类 */
@Data @TableName("courier_preference")
public class CourierPreference {
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 跑腿员用户ID */
    private Long courierId;
    /** 接单区域ID列表（逗号分隔） */
    private String areaIds;
    /** 接单服务类型（逗号分隔） */
    private String serviceTypes;
    /** 接单开始时间 */
    private String timeStart;
    /** 接单结束时间 */
    private String timeEnd;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
