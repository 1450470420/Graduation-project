package com.xypt.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户实体类（学生和跑腿员共用）
 * 对应数据库 user 表
 * user_type=1 为学生，user_type=2 为跑腿员
 */
@Data
@TableName("`user`") // user是MySQL保留字，需要加反引号
public class User {

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户名（唯一） */
    private String username;

    /** 密码（明文） */
    private String password;

    /** 手机号 */
    private String phone;

    /** 真实姓名 */
    private String realName;

    /** 学号 */
    private String studentId;

    /** 头像路径 */
    private String avatar;

    /** 宿舍地址 */
    private String dormAddress;

    /** 用户类型：1=学生 2=跑腿员 */
    private Integer userType;

    /** 账号状态：1=正常 2=封禁 */
    private Integer status;

    /** 信誉分（满分100） */
    private Integer reputation;

    /** 账户余额（元） */
    private BigDecimal balance;

    /** 总收益（跑腿员用） */
    private BigDecimal totalEarnings;

    /** 是否实名认证：0=否 1=是 */
    private Integer isCertified;

    /** 跑腿员审核状态：0=未申请 1=待审核 2=已通过 3=已驳回 */
    private Integer courierStatus;

    /** 跑腿员当前纬度（配送中实时上报） */
    private Double currentLat;

    /** 跑腿员当前经度（配送中实时上报） */
    private Double currentLng;

    /** 位置最后上报时间 */
    private LocalDateTime locationUpdateTime;

    /** 人脸照片路径（用于取货人脸验证） */
    private String facePhoto;

    /** 注册时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
