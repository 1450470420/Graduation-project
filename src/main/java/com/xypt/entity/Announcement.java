package com.xypt.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
/** 公告实体类 */
@Data @TableName("announcement")
public class Announcement {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String content;
    /** 发布对象：all/student/courier */
    private String target;
    /** 状态：1=发布 0=下架 */
    private Integer status;
    private Long adminId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
