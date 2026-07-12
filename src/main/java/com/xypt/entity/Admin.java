package com.xypt.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理员实体类
 * 对应数据库 admin 表
 */
@Data
@TableName("admin")
public class Admin {

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户名 */
    private String username;

    /** 密码（明文） */
    private String password;

    /** 真实姓名 */
    private String realName;

    /** 角色：admin=普通管理员, super_admin=超级管理员 */
    private String role;

    /** 状态：1=正常 0=禁用 */
    private Integer status;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
