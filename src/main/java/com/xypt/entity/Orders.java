package com.xypt.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
/** 订单实体类 */
@Data @TableName("orders")
public class Orders {
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 订单编号 */
    private String orderNo;
    /** 下单学生ID */
    private Long studentId;
    /** 接单跑腿员ID */
    private Long courierId;
    /** 服务类型：代取快递/代买/代送 */
    private String serviceType;
    /** 取件地址 */
    private String pickupAddress;
    /** 送件地址 */
    private String deliveryAddress;
    /** 物品信息 */
    private String itemInfo;
    /** 赏金金额 */
    private BigDecimal reward;
    /** 备注 */
    private String note;
    /** 预约时间 */
    private LocalDateTime reserveTime;
    /** 状态：0=待接单 1=待取件 2=配送中 3=已送达 4=已完成 5=已取消 */
    private Integer status;
    /** 取消原因 */
    private String cancelReason;
    /** 配送距离km */
    private BigDecimal distance;
    /** 取件地址纬度 */
    private Double pickupLat;
    /** 取件地址经度 */
    private Double pickupLng;
    /** 送达地址纬度 */
    private Double deliveryLat;
    /** 送达地址经度 */
    private Double deliveryLng;
    /** 接单时间 */
    private LocalDateTime grabTime;
    /** 完成时间 */
    private LocalDateTime completeTime;
    /** 是否已通过人脸验证：0=未验证 1=已验证 */
    private Integer faceVerified;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
