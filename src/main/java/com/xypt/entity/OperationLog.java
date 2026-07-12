package com.xypt.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
/** 操作日志实体类（AOP自动记录） */
@Data @TableName("operation_log")
public class OperationLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String operator;
    private String operation;
    private String method;
    private String params;
    private String ip;
    private Integer status;
    private String errorMsg;
    private Long costTime;
    private LocalDateTime createTime;
}
