
-- 创建数据库
CREATE DATABASE IF NOT EXISTS xypt_db DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE xypt_db;

-- =============================================
-- 1. 管理员表
-- =============================================
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码（明文）',
  `real_name` varchar(50) DEFAULT NULL COMMENT '真实姓名',
  `role` varchar(20) DEFAULT 'admin' COMMENT '角色：admin=普通管理员, super_admin=超级管理员',
  `status` tinyint DEFAULT 1 COMMENT '状态：1=正常 0=禁用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员表';

-- =============================================
-- 2. 用户表（学生和跑腿员共用）
-- =============================================
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码（明文）',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `real_name` varchar(50) DEFAULT NULL COMMENT '真实姓名',
  `student_id` varchar(30) DEFAULT NULL COMMENT '学号',
  `avatar` varchar(500) DEFAULT NULL COMMENT '头像路径',
  `dorm_address` varchar(200) DEFAULT NULL COMMENT '宿舍地址',
  `user_type` tinyint DEFAULT 1 COMMENT '用户类型：1=学生 2=跑腿员',
  `status` tinyint DEFAULT 1 COMMENT '状态：1=正常 2=封禁',
  `reputation` int DEFAULT 100 COMMENT '信誉分（满分100）',
  `balance` decimal(10,2) DEFAULT 0.00 COMMENT '账户余额',
  `total_earnings` decimal(10,2) DEFAULT 0.00 COMMENT '总收益（跑腿员专用）',
  `is_certified` tinyint DEFAULT 0 COMMENT '是否实名认证：0=否 1=是',
  `courier_status` tinyint DEFAULT 0 COMMENT '跑腿员审核状态：0=未申请 1=待审核 2=已通过 3=已驳回',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `face_photo` varchar(500) DEFAULT NULL COMMENT '人脸照片路径（用于取货人脸验证）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表（学生和跑腿员）';

-- =============================================
-- 3. 跑腿员入驻申请表
-- =============================================
DROP TABLE IF EXISTS `courier_application`;
CREATE TABLE `courier_application` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '申请用户ID',
  `real_name` varchar(50) DEFAULT NULL COMMENT '真实姓名',
  `student_id` varchar(30) DEFAULT NULL COMMENT '学号',
  `phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `id_card_no` varchar(30) DEFAULT NULL COMMENT '身份证号',
  `remark` varchar(500) DEFAULT NULL COMMENT '申请备注',
  `status` tinyint DEFAULT 0 COMMENT '状态：0=待审核 1=已通过 2=已驳回',
  `reject_reason` varchar(500) DEFAULT NULL COMMENT '驳回原因',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='跑腿员入驻申请表';

-- =============================================
-- 4. 跑腿员偏好设置表
-- =============================================
DROP TABLE IF EXISTS `courier_preference`;
CREATE TABLE `courier_preference` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `courier_id` bigint NOT NULL COMMENT '跑腿员用户ID',
  `area_ids` varchar(500) DEFAULT NULL COMMENT '接单区域ID列表（逗号分隔）',
  `service_types` varchar(200) DEFAULT NULL COMMENT '接单服务类型（逗号分隔）',
  `time_start` varchar(10) DEFAULT NULL COMMENT '接单开始时间（如08:00）',
  `time_end` varchar(10) DEFAULT NULL COMMENT '接单结束时间（如22:00）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_courier_id` (`courier_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='跑腿员偏好设置表';

-- =============================================
-- 5. 校园区域表
-- =============================================
DROP TABLE IF EXISTS `area`;
CREATE TABLE `area` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(100) NOT NULL COMMENT '区域名称',
  `type` varchar(20) DEFAULT NULL COMMENT '区域类型：campus=校区 dorm=宿舍楼 teaching=教学楼 other=其他',
  `parent_id` bigint DEFAULT 0 COMMENT '父区域ID（0=顶级校区）',
  `status` tinyint DEFAULT 1 COMMENT '状态：1=启用 0=禁用',
  `sort_order` int DEFAULT 0 COMMENT '排序权重',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='校园区域表';

-- =============================================
-- 6. 取件点表
-- =============================================
DROP TABLE IF EXISTS `pickup_point`;
CREATE TABLE `pickup_point` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(100) NOT NULL COMMENT '取件点名称',
  `type` varchar(50) DEFAULT NULL COMMENT '类型：快递柜/菜鸟驿站/自提点',
  `area_id` bigint DEFAULT NULL COMMENT '所属区域ID',
  `address` varchar(200) DEFAULT NULL COMMENT '详细地址',
  `status` tinyint DEFAULT 1 COMMENT '状态：1=启用 0=禁用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='取件点表';

-- =============================================
-- 7. 用户常用地址表
-- =============================================
DROP TABLE IF EXISTS `user_address`;
CREATE TABLE `user_address` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `label` varchar(50) DEFAULT NULL COMMENT '地址标签（宿舍/教室/食堂等）',
  `address` varchar(200) NOT NULL COMMENT '详细地址',
  `is_default` tinyint DEFAULT 0 COMMENT '是否默认地址：0=否 1=是',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户常用地址表';

-- =============================================
-- 8. 订单表
-- =============================================
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_no` varchar(50) NOT NULL COMMENT '订单编号',
  `student_id` bigint NOT NULL COMMENT '下单学生ID',
  `courier_id` bigint DEFAULT NULL COMMENT '接单跑腿员ID',
  `service_type` varchar(20) DEFAULT NULL COMMENT '服务类型：代取快递/代买/代送',
  `pickup_address` varchar(300) DEFAULT NULL COMMENT '取件地址',
  `delivery_address` varchar(300) DEFAULT NULL COMMENT '送件地址',
  `item_info` varchar(500) DEFAULT NULL COMMENT '物品信息描述',
  `reward` decimal(10,2) DEFAULT NULL COMMENT '赏金金额（元）',
  `note` varchar(500) DEFAULT NULL COMMENT '备注信息',
  `reserve_time` datetime DEFAULT NULL COMMENT '预约取件时间',
  `status` tinyint DEFAULT 0 COMMENT '订单状态：0=待接单 1=待取件 2=配送中 3=已送达 4=已完成 5=已取消',
  `cancel_reason` varchar(500) DEFAULT NULL COMMENT '取消原因',
  `distance` decimal(10,2) DEFAULT NULL COMMENT '配送距离（km）',
  `grab_time` datetime DEFAULT NULL COMMENT '接单时间',
  `complete_time` datetime DEFAULT NULL COMMENT '完成时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `face_verified` tinyint DEFAULT 0 COMMENT '是否已通过人脸验证：0=未验证 1=已验证',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- =============================================
-- 9. 评价表
-- =============================================
DROP TABLE IF EXISTS `evaluation`;
CREATE TABLE `evaluation` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `student_id` bigint NOT NULL COMMENT '评价学生ID',
  `courier_id` bigint NOT NULL COMMENT '被评价跑腿员ID',
  `rating` tinyint DEFAULT NULL COMMENT '星级评分：1-5星',
  `content` varchar(500) DEFAULT NULL COMMENT '文字评价内容',
  `images` varchar(1000) DEFAULT NULL COMMENT '评价图片路径（逗号分隔）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '评价时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单评价表';

-- =============================================
-- 10. 退款申请表
-- =============================================
DROP TABLE IF EXISTS `order_refund`;
CREATE TABLE `order_refund` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` bigint NOT NULL COMMENT '关联订单ID',
  `student_id` bigint NOT NULL COMMENT '申请退款学生ID',
  `reason` varchar(500) DEFAULT NULL COMMENT '退款原因',
  `status` tinyint DEFAULT 0 COMMENT '状态：0=待审核 1=已通过 2=已驳回',
  `admin_remark` varchar(500) DEFAULT NULL COMMENT '管理员处理备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '处理时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退款申请表';

-- =============================================
-- 11. 投诉表
-- =============================================
DROP TABLE IF EXISTS `order_complaint`;
CREATE TABLE `order_complaint` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` bigint NOT NULL COMMENT '关联订单ID',
  `student_id` bigint NOT NULL COMMENT '投诉学生ID',
  `type` varchar(50) DEFAULT NULL COMMENT '投诉类型：超时/服务差/丢件/其他',
  `content` varchar(500) DEFAULT NULL COMMENT '投诉详情内容',
  `images` varchar(1000) DEFAULT NULL COMMENT '凭证图片路径（逗号分隔）',
  `status` tinyint DEFAULT 0 COMMENT '状态：0=待处理 1=处理中 2=已处理',
  `result` varchar(500) DEFAULT NULL COMMENT '处理结果说明',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '投诉时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '处理时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单投诉表';

-- =============================================
-- 12. 提现申请表
-- =============================================
DROP TABLE IF EXISTS `withdrawal`;
CREATE TABLE `withdrawal` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `courier_id` bigint NOT NULL COMMENT '申请提现跑腿员ID',
  `amount` decimal(10,2) DEFAULT NULL COMMENT '提现金额（元）',
  `payment_method` varchar(50) DEFAULT NULL COMMENT '收款方式：微信/支付宝/银行卡',
  `account_info` varchar(200) DEFAULT NULL COMMENT '收款账户信息',
  `status` tinyint DEFAULT 0 COMMENT '状态：0=待审核 1=已通过 2=已驳回',
  `admin_remark` varchar(500) DEFAULT NULL COMMENT '管理员审核备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '审核时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='提现申请表';

-- =============================================
-- 13. 公告表
-- =============================================
DROP TABLE IF EXISTS `announcement`;
CREATE TABLE `announcement` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` varchar(200) NOT NULL COMMENT '公告标题',
  `content` text DEFAULT NULL COMMENT '公告详细内容',
  `target` varchar(20) DEFAULT 'all' COMMENT '发布对象：all=全部 student=学生 courier=跑腿员',
  `status` tinyint DEFAULT 1 COMMENT '状态：1=已发布 0=已下架',
  `admin_id` bigint DEFAULT NULL COMMENT '发布管理员ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告表';

-- =============================================
-- 14. 消息通知表
-- =============================================
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '接收用户ID',
  `title` varchar(200) DEFAULT NULL COMMENT '消息标题',
  `content` varchar(500) DEFAULT NULL COMMENT '消息内容',
  `type` varchar(20) DEFAULT NULL COMMENT '消息类型：order=订单通知 announcement=公告 system=系统通知',
  `is_read` tinyint DEFAULT 0 COMMENT '是否已读：0=未读 1=已读',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息通知表';

-- =============================================
-- 15. 客服工单表
-- =============================================
DROP TABLE IF EXISTS `service_ticket`;
CREATE TABLE `service_ticket` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '提交用户ID',
  `title` varchar(200) DEFAULT NULL COMMENT '工单标题',
  `content` varchar(1000) DEFAULT NULL COMMENT '问题描述',
  `status` tinyint DEFAULT 0 COMMENT '状态：0=待处理 1=处理中 2=已关闭',
  `reply` varchar(1000) DEFAULT NULL COMMENT '客服回复内容',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '处理时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客服工单表';

-- =============================================
-- 16. 系统配置表
-- =============================================
DROP TABLE IF EXISTS `system_config`;
CREATE TABLE `system_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `config_key` varchar(100) NOT NULL COMMENT '配置键名',
  `config_value` varchar(500) DEFAULT NULL COMMENT '配置值',
  `config_desc` varchar(200) DEFAULT NULL COMMENT '配置项说明',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

-- =============================================
-- 17. 操作日志表（AOP记录）
-- =============================================
DROP TABLE IF EXISTS `operation_log`;
CREATE TABLE `operation_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `operator` varchar(50) DEFAULT NULL COMMENT '操作者用户名',
  `operation` varchar(100) DEFAULT NULL COMMENT '操作描述',
  `method` varchar(200) DEFAULT NULL COMMENT '请求方法路径',
  `params` text DEFAULT NULL COMMENT '请求参数（JSON）',
  `ip` varchar(50) DEFAULT NULL COMMENT '请求IP地址',
  `status` tinyint DEFAULT 1 COMMENT '操作状态：1=成功 0=失败',
  `error_msg` varchar(1000) DEFAULT NULL COMMENT '失败错误信息',
  `cost_time` bigint DEFAULT NULL COMMENT '执行耗时（毫秒）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- =============================================
-- 18. 信誉分记录表
-- =============================================
DROP TABLE IF EXISTS `reputation_record`;
CREATE TABLE `reputation_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `change_score` int DEFAULT NULL COMMENT '变动分数（正数=加分，负数=扣分）',
  `reason` varchar(200) DEFAULT NULL COMMENT '变动原因说明',
  `type` tinyint DEFAULT NULL COMMENT '类型：1=加分 2=扣分',
  `operator` varchar(50) DEFAULT NULL COMMENT '操作者（system=系统 管理员用户名）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '记录时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='信誉分记录表';

-- =============================================
-- 19. 聊天消息表
-- =============================================
DROP TABLE IF EXISTS `chat_message`;
CREATE TABLE `chat_message` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` bigint NOT NULL COMMENT '关联订单ID',
  `sender_id` bigint NOT NULL COMMENT '发送者用户ID',
  `receiver_id` bigint NOT NULL COMMENT '接收者用户ID',
  `content` varchar(1000) DEFAULT NULL COMMENT '消息内容',
  `is_read` tinyint DEFAULT 0 COMMENT '是否已读：0=未读 1=已读',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单内聊天消息表';

-- =============================================
-- 20. 余额变动记录表
-- =============================================
DROP TABLE IF EXISTS `balance_record`;
CREATE TABLE `balance_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `amount` decimal(10,2) DEFAULT NULL COMMENT '变动金额',
  `type` tinyint DEFAULT NULL COMMENT '类型：1=收入 2=支出 3=充值',
  `remark` varchar(200) DEFAULT NULL COMMENT '变动说明',
  `order_id` bigint DEFAULT NULL COMMENT '关联订单ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '记录时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='余额变动记录表';

-- =============================================
-- 插入模拟数据
-- =============================================

-- =============================================
-- 21. 新增：用户表增加人脸照片字段（用于取货防冒领验证）
-- =============================================
ALTER TABLE `user` ADD COLUMN IF NOT EXISTS `face_photo` varchar(500) DEFAULT NULL COMMENT '人脸照片路径（用于取货人脸验证）';

-- =============================================
-- 22. 新增：订单表增加人脸验证状态字段
-- =============================================
ALTER TABLE `orders` ADD COLUMN IF NOT EXISTS `face_verified` tinyint DEFAULT 0 COMMENT '是否已通过人脸验证：0=未验证 1=已验证';

-- 管理员数据
INSERT INTO `admin` (`id`, `username`, `password`, `real_name`, `role`, `status`, `create_time`) VALUES
(1, 'admin', '123456', '超级管理员', 'super_admin', 1, '2026-01-01 08:00:00'),
(2, 'manager', '123456', '运营管理员', 'admin', 1, '2026-01-02 09:00:00'),
(3, 'support', '123456', '客服管理员', 'admin', 1, '2026-01-03 10:00:00');

-- 学生用户数据
INSERT INTO `user` (`id`, `username`, `password`, `phone`, `real_name`, `student_id`, `avatar`, `dorm_address`, `user_type`, `status`, `reputation`, `balance`, `total_earnings`, `is_certified`, `courier_status`, `create_time`) VALUES
(1, 'student001', '123456', '13800001001', '张小明', '2022010101', '/upload/avatar/student001.jpg', '学生公寓1号楼301室', 1, 1, 100, 50.00, 0.00, 1, 0, '2026-01-05 10:00:00'),
(2, 'student002', '123456', '13800001002', '李小红', '2022010102', '/upload/avatar/student002.jpg', '学生公寓2号楼402室', 1, 1, 98, 30.00, 0.00, 1, 0, '2026-01-06 11:00:00'),
(3, 'student003', '123456', '13800001003', '王小强', '2022010103', '/upload/avatar/student003.jpg', '学生公寓3号楼205室', 1, 1, 95, 0.00, 0.00, 0, 0, '2026-01-07 14:00:00'),
(4, 'student004', '123456', '13800001004', '赵小美', '2022010104', '/upload/avatar/student004.jpg', '学生公寓4号楼108室', 1, 1, 100, 80.00, 0.00, 1, 0, '2026-01-08 09:30:00'),
(5, 'student005', '123456', '13800001005', '刘小伟', '2022010105', '/upload/avatar/student005.jpg', '学生公寓5号楼501室', 1, 1, 92, 20.00, 0.00, 1, 0, '2026-01-10 16:00:00'),
(6, 'student006', '123456', '13800001006', '陈小芳', '2022010106', '/upload/avatar/student006.jpg', '女生公寓1号楼302室', 1, 1, 100, 0.00, 0.00, 0, 0, '2026-01-12 10:00:00'),
(7, 'student007', '123456', '13800001007', '杨小超', '2022010107', '/upload/avatar/student007.jpg', '学生公寓6号楼404室', 1, 1, 88, 15.00, 0.00, 1, 0, '2026-01-15 11:30:00'),
(8, 'student008', '123456', '13800001008', '周小燕', '2022010108', '/upload/avatar/student008.jpg', '女生公寓2号楼201室', 1, 2, 60, 0.00, 0.00, 0, 0, '2026-01-18 09:00:00'),
-- 跑腿员用户数据
(11, 'courier001', '123456', '13900001001', '孙志远', '2021010201', '/upload/avatar/courier001.jpg', '学生公寓7号楼601室', 2, 1, 100, 320.50, 1580.00, 1, 2, '2026-01-03 08:00:00'),
(12, 'courier002', '123456', '13900001002', '钱大海', '2021010202', '/upload/avatar/courier002.jpg', '学生公寓8号楼702室', 2, 1, 98, 158.00, 2340.00, 1, 2, '2026-01-04 09:00:00'),
(13, 'courier003', '123456', '13900001003', '吴建国', '2020010301', '/upload/avatar/courier003.jpg', '学生公寓9号楼303室', 2, 1, 95, 89.00, 890.00, 1, 2, '2026-01-05 10:00:00'),
(14, 'courier004', '123456', '13900001004', '郑文博', '2020010302', '/upload/avatar/courier004.jpg', '学生公寓10号楼405室', 2, 1, 90, 45.00, 560.00, 1, 2, '2026-01-08 14:00:00'),
(15, 'courier005', '123456', '13900001005', '王海涛', '2021010205', '/upload/avatar/courier005.jpg', '学生公寓11号楼201室', 2, 1, 100, 0.00, 0.00, 1, 1, '2026-02-01 10:00:00');

-- 跑腿员入驻申请
INSERT INTO `courier_application` (`id`, `user_id`, `real_name`, `student_id`, `phone`, `id_card_no`, `remark`, `status`, `reject_reason`, `create_time`) VALUES
(1, 11, '孙志远', '2021010201', '13900001001', '320100200101015678', '本人诚信守时，有丰富跑腿经验，希望加入平台', 1, NULL, '2026-01-03 08:00:00'),
(2, 12, '钱大海', '2021010202', '13900001002', '310100200202024567', '有电动车，可快速配送，服务质量有保障', 1, NULL, '2026-01-04 09:00:00'),
(3, 13, '吴建国', '2020010301', '13900001003', '440100200303035678', '时间充裕，责任心强，欢迎考察', 1, NULL, '2026-01-05 10:00:00'),
(4, 14, '郑文博', '2020010302', '13900001004', '330100200104044567', '校内跑腿3年经验，口碑好', 1, NULL, '2026-01-08 14:00:00'),
(5, 15, '王海涛', '2021010205', '13900001005', '110100200205055678', '新手申请，认真负责，希望通过审核', 0, NULL, '2026-02-01 10:00:00'),
(6, 7, '杨小超', '2022010107', '13800001007', '420100200306064567', '申请成为跑腿员，课余时间较多', 2, '材料不完整，学号信息有误，请重新申请', '2026-01-20 16:00:00');

-- 跑腿员偏好设置
INSERT INTO `courier_preference` (`id`, `courier_id`, `area_ids`, `service_types`, `time_start`, `time_end`, `create_time`) VALUES
(1, 11, '1,2,3', '代取快递,代买,代送', '08:00', '22:00', '2026-01-04 09:00:00'),
(2, 12, '1,2', '代取快递,代送', '09:00', '21:00', '2026-01-05 10:00:00'),
(3, 13, '2,3,4', '代取快递,代买', '10:00', '20:00', '2026-01-06 11:00:00'),
(4, 14, '1,3', '代买,代送', '14:00', '22:00', '2026-01-09 08:00:00');

-- 校园区域数据
INSERT INTO `area` (`id`, `name`, `type`, `parent_id`, `status`, `sort_order`, `create_time`) VALUES
(1, '东校区', 'campus', 0, 1, 1, '2026-01-01 08:00:00'),
(2, '西校区', 'campus', 0, 1, 2, '2026-01-01 08:00:00'),
(3, '南校区', 'campus', 0, 1, 3, '2026-01-01 08:00:00'),
(4, '北校区', 'campus', 0, 1, 4, '2026-01-01 08:00:00'),
-- 东校区宿舍楼
(5, '学生公寓1号楼', 'dorm', 1, 1, 1, '2026-01-01 08:00:00'),
(6, '学生公寓2号楼', 'dorm', 1, 1, 2, '2026-01-01 08:00:00'),
(7, '学生公寓3号楼', 'dorm', 1, 1, 3, '2026-01-01 08:00:00'),
(8, '女生公寓1号楼', 'dorm', 1, 1, 4, '2026-01-01 08:00:00'),
-- 东校区教学楼
(9, '图书馆', 'teaching', 1, 1, 1, '2026-01-01 08:00:00'),
(10, '第一教学楼', 'teaching', 1, 1, 2, '2026-01-01 08:00:00'),
(11, '第二教学楼', 'teaching', 1, 1, 3, '2026-01-01 08:00:00'),
-- 西校区
(12, '学生公寓7号楼', 'dorm', 2, 1, 1, '2026-01-01 08:00:00'),
(13, '学生公寓8号楼', 'dorm', 2, 1, 2, '2026-01-01 08:00:00'),
(14, '实验楼A栋', 'teaching', 2, 1, 1, '2026-01-01 08:00:00'),
(15, '体育馆', 'other', 2, 1, 1, '2026-01-01 08:00:00'),
-- 南校区
(16, '学生公寓9号楼', 'dorm', 3, 1, 1, '2026-01-01 08:00:00'),
(17, '学生公寓10号楼', 'dorm', 3, 1, 2, '2026-01-01 08:00:00'),
(18, '研究生楼', 'dorm', 3, 1, 3, '2026-01-01 08:00:00'),
(19, '行政楼', 'other', 3, 1, 1, '2026-01-01 08:00:00');

-- 取件点数据
INSERT INTO `pickup_point` (`id`, `name`, `type`, `area_id`, `address`, `status`, `create_time`) VALUES
(1, '东校区菜鸟驿站', '菜鸟驿站', 1, '东校区南门旁菜鸟驿站', 1, '2026-01-01 08:00:00'),
(2, '东校区快递柜A', '快递柜', 5, '学生公寓1号楼一楼大厅', 1, '2026-01-01 08:00:00'),
(3, '东校区快递柜B', '快递柜', 8, '女生公寓1号楼门口', 1, '2026-01-01 08:00:00'),
(4, '图书馆快递自提点', '自提点', 9, '图书馆一楼东侧', 1, '2026-01-01 08:00:00'),
(5, '西校区菜鸟驿站', '菜鸟驿站', 2, '西校区正门左侧菜鸟驿站', 1, '2026-01-01 08:00:00'),
(6, '西校区快递柜', '快递柜', 12, '学生公寓7号楼旁', 1, '2026-01-01 08:00:00'),
(7, '南校区驿站', '菜鸟驿站', 3, '南校区后门菜鸟驿站', 1, '2026-01-01 08:00:00'),
(8, '南校区快递柜', '快递柜', 16, '学生公寓9号楼门口', 1, '2026-01-01 08:00:00'),
(9, '顺丰速运取件点', '自提点', 1, '东校区东门快递集中点', 1, '2026-01-01 08:00:00'),
(10, '北校区驿站', '菜鸟驿站', 4, '北校区主楼旁', 1, '2026-01-01 08:00:00');

-- 用户常用地址
INSERT INTO `user_address` (`id`, `user_id`, `label`, `address`, `is_default`, `create_time`) VALUES
(1, 1, '宿舍', '东校区学生公寓1号楼301室', 1, '2026-01-05 10:30:00'),
(2, 1, '教室', '第一教学楼302教室', 0, '2026-01-06 08:00:00'),
(3, 2, '宿舍', '东校区学生公寓2号楼402室', 1, '2026-01-06 11:30:00'),
(4, 2, '图书馆', '图书馆三楼自习室', 0, '2026-01-07 09:00:00'),
(5, 3, '宿舍', '东校区学生公寓3号楼205室', 1, '2026-01-07 14:30:00'),
(6, 4, '宿舍', '东校区学生公寓4号楼108室', 1, '2026-01-08 10:00:00'),
(7, 5, '宿舍', '东校区学生公寓5号楼501室', 1, '2026-01-10 16:30:00');

-- 订单数据（覆盖2026-01-01到2026-03-09）
INSERT INTO `orders` (`id`, `order_no`, `student_id`, `courier_id`, `service_type`, `pickup_address`, `delivery_address`, `item_info`, `reward`, `note`, `status`, `grab_time`, `complete_time`, `create_time`) VALUES
(1, 'ORD20260105001', 1, 11, '代取快递', '东校区菜鸟驿站', '学生公寓1号楼301室', '京东快递，单号JD123456789', 3.00, '轻拿轻放，谢谢', 4, '2026-01-05 10:15:00', '2026-01-05 10:45:00', '2026-01-05 10:00:00'),
(2, 'ORD20260106001', 2, 12, '代取快递', '东校区菜鸟驿站', '学生公寓2号楼402室', '天猫超市，单号TM987654321', 2.50, '', 4, '2026-01-06 11:20:00', '2026-01-06 11:50:00', '2026-01-06 11:00:00'),
(3, 'ORD20260107001', 3, 11, '代买', '学校西门附近超市', '学生公寓3号楼205室', '买2瓶矿泉水，1包薯片', 5.00, '超市有就行，品牌不限', 4, '2026-01-07 14:15:00', '2026-01-07 15:00:00', '2026-01-07 14:00:00'),
(4, 'ORD20260108001', 4, 12, '代送', '学生公寓4号楼108室', '图书馆三楼阅览室', '帮送一本书，书放在门口', 4.00, '书放在宿舍门口', 4, '2026-01-08 09:40:00', '2026-01-08 10:10:00', '2026-01-08 09:30:00'),
(5, 'ORD20260110001', 5, 13, '代取快递', '西校区菜鸟驿站', '学生公寓5号楼501室', '顺丰快递，电话取件', 3.50, '', 4, '2026-01-10 16:20:00', '2026-01-10 17:00:00', '2026-01-10 16:00:00'),
(6, 'ORD20260112001', 1, 11, '代买', '学校北门便利店', '学生公寓1号楼301室', '买一瓶农夫山泉，一个面包', 6.00, '买大瓶的', 4, '2026-01-12 08:15:00', '2026-01-12 08:50:00', '2026-01-12 08:00:00'),
(7, 'ORD20260115001', 2, 12, '代取快递', '南校区驿站', '学生公寓2号楼402室', '拼多多快递，件很多', 5.00, '共3件包裹', 4, '2026-01-15 13:20:00', '2026-01-15 14:00:00', '2026-01-15 13:00:00'),
(8, 'ORD20260118001', 4, 13, '代取快递', '东校区快递柜A', '学生公寓4号楼108室', '快递柜取件码：558892', 2.00, '取件码已发短信', 4, '2026-01-18 10:10:00', '2026-01-18 10:40:00', '2026-01-18 10:00:00'),
(9, 'ORD20260120001', 6, 11, '代买', '学校超市', '女生公寓1号楼302室', '买一盒卫生巾，品牌护舒宝', 8.00, '买夜用的', 4, '2026-01-20 15:20:00', '2026-01-20 16:00:00', '2026-01-20 15:00:00'),
(10, 'ORD20260122001', 3, 14, '代送', '学生公寓3号楼205室', '第二教学楼201教室', '帮送作业本，老师要求今天交', 5.00, '很紧急', 4, '2026-01-22 08:10:00', '2026-01-22 08:35:00', '2026-01-22 08:00:00'),
(11, 'ORD20260125001', 1, 12, '代取快递', '东校区菜鸟驿站', '第一教学楼302教室', '亚马逊快递，书籍', 3.00, '在上课，送到教室门口', 4, '2026-01-25 09:15:00', '2026-01-25 09:50:00', '2026-01-25 09:00:00'),
(12, 'ORD20260201001', 5, 11, '代买', '学校正门附近奶茶店', '学生公寓5号楼501室', '买一杯珍珠奶茶，不加糖', 10.00, '少冰', 4, '2026-02-01 14:20:00', '2026-02-01 15:00:00', '2026-02-01 14:00:00'),
(13, 'ORD20260205001', 2, 13, '代取快递', '西校区菜鸟驿站', '学生公寓2号楼402室', '中通快递，衣物包裹', 4.00, '', 4, '2026-02-05 11:20:00', '2026-02-05 12:00:00', '2026-02-05 11:00:00'),
(14, 'ORD20260210001', 4, 14, '代买', '学校食堂', '学生公寓4号楼108室', '买一份红烧肉饭，一个汤', 7.00, '多放点汤', 4, '2026-02-10 12:10:00', '2026-02-10 12:40:00', '2026-02-10 12:00:00'),
(15, 'ORD20260215001', 1, 11, '代取快递', '东校区快递柜B', '学生公寓1号楼301室', '快递柜取件码：114567', 2.50, '', 4, '2026-02-15 16:20:00', '2026-02-15 16:50:00', '2026-02-15 16:00:00'),
(16, 'ORD20260220001', 6, 12, '代送', '女生公寓1号楼302室', '图书馆一楼', '帮还图书馆的书，共2本', 3.00, '帮放还书槽就行', 4, '2026-02-20 10:10:00', '2026-02-20 10:35:00', '2026-02-20 10:00:00'),
(17, 'ORD20260225001', 3, 13, '代取快递', '东校区菜鸟驿站', '学生公寓3号楼205室', '德邦快递，较重', 6.00, '比较重，需要能拿动的', 4, '2026-02-25 14:20:00', '2026-02-25 15:10:00', '2026-02-25 14:00:00'),
(18, 'ORD20260301001', 5, 11, '代买', '学校超市', '学生公寓5号楼501室', '买泡面3桶，零食随意', 8.00, '快到考试周了，准备存货', 4, '2026-03-01 09:20:00', '2026-03-01 10:00:00', '2026-03-01 09:00:00'),
(19, 'ORD20260303001', 7, 14, '代取快递', '南校区驿站', '学生公寓6号楼404室', '京东快递，数码产品', 5.00, '小心轻放', 4, '2026-03-03 15:20:00', '2026-03-03 16:00:00', '2026-03-03 15:00:00'),
(20, 'ORD20260305001', 4, 12, '代取快递', '西校区快递柜', '学生公寓4号楼108室', '韵达快递，取件码：225891', 2.00, '', 4, '2026-03-05 10:20:00', '2026-03-05 10:50:00', '2026-03-05 10:00:00'),
-- 进行中订单
(21, 'ORD20260307001', 1, 11, '代取快递', '东校区菜鸟驿站', '第一教学楼302教室', '拼多多快递，单号PDD111222333', 3.00, '快上课了，尽快送来', 2, '2026-03-07 08:10:00', NULL, '2026-03-07 08:00:00'),
(22, 'ORD20260308001', 2, 13, '代买', '学校超市', '学生公寓2号楼402室', '买一包方便面', 4.00, '', 1, '2026-03-08 10:05:00', NULL, '2026-03-08 10:00:00'),
-- 待接单订单
(23, 'ORD20260309001', 3, NULL, '代取快递', '东校区快递柜A', '学生公寓3号楼205室', '菜鸟快递，取件码：889901', 2.50, '', 0, NULL, NULL, '2026-03-09 09:00:00'),
(24, 'ORD20260309002', 6, NULL, '代买', '学校食堂', '女生公寓1号楼302室', '买一份西红柿鸡蛋饭', 6.00, '不辣', 0, NULL, NULL, '2026-03-09 11:00:00'),
-- 已取消订单
(25, 'ORD20260115002', 7, NULL, '代送', '学生公寓6号楼404室', '行政楼102', '帮送文件', 5.00, '', 5, NULL, NULL, '2026-03-09 11:00:00');

-- 评价数据
INSERT INTO `evaluation` (`id`, `order_id`, `student_id`, `courier_id`, `rating`, `content`, `images`, `create_time`) VALUES
(1, 1, 1, 11, 5, '送得很快，态度好，下次还找他！', '', '2026-01-05 11:00:00'),
(2, 2, 2, 12, 5, '非常及时，很专业', '', '2026-01-06 12:00:00'),
(3, 3, 3, 11, 4, '速度挺快的，东西都买对了', '', '2026-01-07 15:30:00'),
(4, 4, 4, 12, 5, '态度很好，很快就送到了', '', '2026-01-08 10:30:00'),
(5, 5, 5, 13, 4, '还不错，就是稍微慢了一点', '', '2026-01-10 17:30:00'),
(6, 6, 1, 11, 5, '买的东西完全符合要求，赞！', '', '2026-01-12 09:00:00'),
(7, 7, 2, 12, 5, '好评，东西都带来了', '', '2026-01-15 14:30:00'),
(8, 8, 4, 13, 4, '挺好的', '', '2026-01-18 11:00:00'),
(9, 9, 6, 11, 5, '很细心，买对了东西，赞', '', '2026-01-20 16:30:00'),
(10, 10, 3, 14, 5, '超级快！准时送到了', '', '2026-01-22 09:00:00'),
(11, 11, 1, 12, 4, '送到了教室，不错', '', '2026-01-25 10:00:00'),
(12, 12, 5, 11, 5, '好喝，没有撒，很棒', '', '2026-02-01 15:30:00'),
(13, 13, 2, 13, 4, '还可以', '', '2026-02-05 12:30:00'),
(14, 14, 4, 14, 5, '送餐很快，还热乎着呢', '', '2026-02-10 13:00:00'),
(15, 15, 1, 11, 5, '一如既往的好服务', '', '2026-02-15 17:00:00'),
(16, 16, 6, 12, 5, '还图书馆超快，满意', '', '2026-02-20 11:00:00'),
(17, 17, 3, 13, 3, '快递比较重，等了挺久的，但最终送到了', '', '2026-02-25 15:30:00'),
(18, 18, 5, 11, 5, '买的东西齐全，推荐', '', '2026-03-01 10:30:00'),
(19, 19, 7, 14, 5, '轻拿轻放，很专业', '', '2026-03-03 16:30:00'),
(20, 20, 4, 12, 4, '还好', '', '2026-03-05 11:00:00');

-- 退款申请数据
INSERT INTO `order_refund` (`id`, `order_id`, `student_id`, `reason`, `status`, `admin_remark`, `create_time`) VALUES
(1, 25, 7, '跑腿员超时未接单，我自己去取了', 1, '已核实，全额退款', '2026-03-09 12:00:00'),
(2, 17, 3, '快递送晚了影响我的使用', 2, '经查实跑腿员已按时送达，不符合退款条件', '2026-02-25 16:00:00');

-- 投诉数据
INSERT INTO `order_complaint` (`id`, `order_id`, `student_id`, `type`, `content`, `images`, `status`, `result`, `create_time`) VALUES
(1, 17, 3, '超时', '跑腿员配送时间超过了预期很多，影响了我的正常使用', '', 2, '经核实，因快递较重，跑腿员已尽力。给予该学生5信誉分补偿，此次不计入跑腿员不良记录', '2026-02-25 16:30:00'),
(2, 22, 2, '其他', '跑腿员接单后迟迟没有出发取货，已等待超过30分钟', '', 1, NULL, '2026-03-08 10:30:00');

-- 提现申请数据
INSERT INTO `withdrawal` (`id`, `courier_id`, `amount`, `payment_method`, `account_info`, `status`, `admin_remark`, `create_time`) VALUES
(1, 11, 200.00, '微信', '微信号：sunzhiyuan_wx', 1, '已打款，请注意查收', '2026-01-20 10:00:00'),
(2, 12, 150.00, '支付宝', '支付宝账号：qiandasea@163.com', 1, '已通过支付宝转账', '2026-01-25 11:00:00'),
(3, 11, 300.00, '微信', '微信号：sunzhiyuan_wx', 1, '已打款', '2026-02-15 09:00:00'),
(4, 13, 80.00, '银行卡', '建设银行：6217 0000 1234 5678（吴建国）', 1, '已转账，1-3个工作日到账', '2026-02-20 14:00:00'),
(5, 12, 200.00, '支付宝', '支付宝账号：qiandasea@163.com', 0, NULL, '2026-03-05 10:00:00'),
(6, 14, 100.00, '微信', '微信号：zhengwenbo_wx', 2, '账户信息有误，请核实后重新提交', '2026-03-07 09:00:00');

-- 公告数据
INSERT INTO `announcement` (`id`, `title`, `content`, `target`, `status`, `admin_id`, `create_time`) VALUES
(1, '校园跑腿平台正式上线通知', '亲爱的同学们，校园跑腿服务平台正式上线啦！平台提供代取快递、代买、代送等服务，方便大家的校园生活。欢迎大家注册使用！有任何问题请联系客服。', 'all', 1, 1, '2026-01-01 08:00:00'),
(2, '跑腿员招募令：加入我们赚取兼职收入', '招募在校跑腿员！要求：1.在校学生；2.有充裕时间；3.诚实守信。加入后可灵活接单，月入300-1000元。立即申请入驻吧！', 'student', 1, 1, '2026-01-05 10:00:00'),
(3, '平台规则说明：信誉分制度', '为维护平台生态，我们建立了信誉分制度。完成订单+2分，好评+1分，差评-3分，投诉成立-10分。信誉分低于60分将暂停服务。', 'all', 1, 1, '2026-01-08 09:00:00'),
(4, '春节假期服务调整公告', '春节期间（1月28日-2月4日）平台服务可能减少，请提前安排取件计划。节后正常运营，祝大家新年快乐！', 'all', 1, 2, '2026-01-20 14:00:00'),
(5, '跑腿员月度业绩评选活动', '为激励优秀跑腿员，2月份起开展月度评选活动，第一名奖励200元，第二三名各100元，优秀跑腿员还有专属标识。', 'courier', 1, 1, '2026-02-01 09:00:00'),
(6, '平台安全提醒：谨防诈骗', '近期有不法分子冒充平台客服诈骗，请同学们注意：平台不会要求提供密码、银行卡号等敏感信息。如有疑问请通过官方渠道联系客服。', 'all', 1, 2, '2026-02-10 10:00:00'),
(7, '3月份订单大促活动', '3月份起，发单赏金满5元随机立减0.5-1元，欢迎大家多多使用平台服务！活动截止3月31日。', 'student', 1, 1, '2026-03-01 09:00:00'),
(8, '系统维护公告', '3月10日凌晨2:00-4:00进行系统维护，届时服务暂时不可用，请提前安排。维护期间的已接单订单不受影响。', 'all', 0, 2, '2026-03-08 16:00:00');

-- 消息通知数据
INSERT INTO `message` (`id`, `user_id`, `title`, `content`, `type`, `is_read`, `create_time`) VALUES
(1, 1, '订单已完成', '您的订单ORD20260105001已完成配送，请确认收货并评价跑腿员', 'order', 1, '2026-01-05 10:45:00'),
(2, 11, '您有新订单', '学生张小明发布了新订单，赏金3.00元，快去抢单吧！', 'order', 1, '2026-01-05 10:00:00'),
(3, 2, '订单已完成', '您的订单ORD20260106001已完成，感谢使用校园跑腿服务', 'order', 1, '2026-01-06 11:50:00'),
(4, 1, '平台公告', '校园跑腿平台正式上线通知', 'announcement', 1, '2026-01-01 08:00:00'),
(5, 2, '平台公告', '校园跑腿平台正式上线通知', 'announcement', 1, '2026-01-01 08:00:00'),
(6, 11, '提现审核通过', '您的提现申请（200元）已通过审核，款项已打入微信账户，请注意查收', 'system', 1, '2026-01-20 14:00:00'),
(7, 12, '提现审核通过', '您的提现申请（150元）已通过，已转账至支付宝账号', 'system', 1, '2026-01-25 15:00:00'),
(8, 1, '订单待接单', '您的订单ORD20260309001已发布，等待跑腿员接单', 'order', 0, '2026-03-09 09:00:00'),
(9, 11, '订单进行中', '您接取的订单ORD20260307001正在配送中，请及时送达', 'order', 1, '2026-03-07 08:10:00');

-- 客服工单数据
INSERT INTO `service_ticket` (`id`, `user_id`, `title`, `content`, `status`, `reply`, `create_time`) VALUES
(1, 3, '我的订单被取消了，赏金怎么处理', '我发布了一个代买订单，跑腿员接单后突然取消了，我的赏金还在吗？订单号ORD20260115002', 2, '您好，经核实该订单已退款，赏金原路返回，请查看账户余额。感谢您的使用！', '2026-01-16 10:00:00'),
(2, 7, '跑腿员联系不上怎么办', '我发布了送件订单，跑腿员接单后一直不接电话，已经等了一个多小时', 2, '您好，我们已联系到跑腿员，确认是手机没电。跑腿员将在15分钟内到达，如还有问题请继续联系我们。', '2026-01-15 15:00:00'),
(3, 5, '充值遇到问题', '我想给账户充值但是不知道怎么操作', 2, '您好，目前充值功能正在开发中，敬请期待。如有其他问题请继续咨询。', '2026-02-08 11:00:00'),
(4, 4, '评价时图片上传失败', '我想给订单上传评价图片，但一直提示上传失败', 1, NULL, '2026-03-01 09:00:00'),
(5, 1, '如何设置常用地址', '我想保存几个常用地址方便下单，怎么设置', 0, NULL, '2026-03-09 10:00:00');

-- 系统配置数据
INSERT INTO `system_config` (`id`, `config_key`, `config_value`, `config_desc`) VALUES
                                                                                    (1,  'commission_rate',           '0.05',               '平台佣金比例（5%）'),
                                                                                    (2,  'order_timeout_minutes',     '30',                  '订单超时未接单时间（分钟）'),
                                                                                    (3,  'min_reward',                '1.00',                '最低赏金金额（元）'),
                                                                                    (4,  'max_reward',                '50.00',               '最高赏金金额（元）'),
                                                                                    (5,  'reputation_complete_add',   '2',                   '完成订单加分'),
                                                                                    (6,  'reputation_good_review_add','1',                   '好评加分'),
                                                                                    (7,  'reputation_bad_review_deduct','3',                 '差评扣分'),
                                                                                    (8,  'reputation_complaint_deduct','10',                 '投诉成立扣分'),
                                                                                    (9,  'reputation_min_to_receive', '60',                  '最低接单信誉分'),
                                                                                    (10, 'min_withdrawal',            '50.00',               '最低提现金额（元）'),
                                                                                    (11, 'platform_name',             '校园跑腿服务平台',    '平台名称'),
                                                                                    (12, 'customer_service_phone',    '400-888-9999',        '客服电话'),
                                                                                    (13, 'cancel_rule',               '已接单后取消需等待管理员审核', '取消订单规则说明');

-- 操作日志数据
INSERT INTO `operation_log` (`id`, `operator`, `operation`, `method`, `params`, `ip`, `status`, `cost_time`, `create_time`) VALUES
(1, 'admin', '管理员登录', 'POST /api/admin/login', '{"username":"admin"}', '192.168.1.1', 1, 120, '2026-01-01 08:00:00'),
(2, 'admin', '审核跑腿员申请通过', 'PUT /api/admin/courier-applications/1/approve', '{"id":1}', '192.168.1.1', 1, 89, '2026-01-04 10:00:00'),
(3, 'admin', '审核跑腿员申请通过', 'PUT /api/admin/courier-applications/2/approve', '{"id":2}', '192.168.1.1', 1, 76, '2026-01-05 09:00:00'),
(4, 'admin', '发布公告', 'POST /api/admin/announcements', '{"title":"跑腿员招募令"}', '192.168.1.1', 1, 145, '2026-01-05 10:00:00'),
(5, 'manager', '审核退款申请', 'PUT /api/admin/refunds/2/reject', '{"id":2,"remark":"不符合退款条件"}', '192.168.1.100', 1, 98, '2026-02-26 10:00:00'),
(6, 'admin', '审核提现申请', 'PUT /api/admin/withdrawals/1/approve', '{"id":1}', '192.168.1.1', 1, 112, '2026-01-21 10:00:00'),
(7, 'support', '关闭客服工单', 'PUT /api/admin/tickets/1/close', '{"id":1}', '192.168.1.200', 1, 67, '2026-01-16 14:00:00'),
(8, 'admin', '调整用户信誉分', 'PUT /api/admin/reputation/adjust', '{"userId":8,"score":-20,"reason":"违规行为"}', '192.168.1.1', 1, 156, '2026-01-19 09:00:00'),
(9, 'admin', '封禁用户账号', 'PUT /api/admin/users/8/ban', '{"id":8}', '192.168.1.1', 1, 89, '2026-01-19 09:10:00'),
(10, 'admin', '处理投诉', 'PUT /api/admin/complaints/1/handle', '{"id":1,"result":"已处理"}', '192.168.1.1', 1, 134, '2026-02-26 11:00:00');

-- 信誉分记录
INSERT INTO `reputation_record` (`id`, `user_id`, `change_score`, `reason`, `type`, `operator`, `create_time`) VALUES
(1, 1, 2, '完成订单ORD20260105001', 1, 'system', '2026-01-05 10:45:00'),
(2, 11, 2, '完成订单ORD20260105001', 1, 'system', '2026-01-05 10:45:00'),
(3, 11, 1, '订单ORD20260105001获得好评', 1, 'system', '2026-01-05 11:00:00'),
(4, 2, 2, '完成订单ORD20260106001', 1, 'system', '2026-01-06 11:50:00'),
(5, 8, -20, '违规操作，刷单行为', 2, 'admin', '2026-01-19 09:00:00'),
(6, 3, -3, '订单ORD20260225001 获得差评（3星）', 2, 'system', '2026-02-25 15:30:00'),
(7, 11, 2, '完成订单ORD20260301001', 1, 'system', '2026-03-01 10:00:00');

-- 聊天消息数据
INSERT INTO `chat_message` (`id`, `order_id`, `sender_id`, `receiver_id`, `content`, `is_read`, `create_time`) VALUES
(1, 21, 1, 11, '你好，我的快递在东校区菜鸟驿站，取件码是445678', 1, '2026-03-07 08:01:00'),
(2, 21, 11, 1, '好的，我马上去取，大概10分钟到', 1, '2026-03-07 08:05:00'),
(3, 21, 1, 11, '谢谢，我在第一教学楼302教室等你', 1, '2026-03-07 08:06:00'),
(4, 21, 11, 1, '收到！已取到快递，正在送来', 1, '2026-03-07 08:20:00'),
(5, 22, 2, 13, '麻烦买一包统一方便面，红烧牛肉味的', 1, '2026-03-08 10:01:00'),
(6, 22, 13, 2, '好的，我马上去超市买', 1, '2026-03-08 10:06:00');

-- 余额记录数据
INSERT INTO `balance_record` (`id`, `user_id`, `amount`, `type`, `remark`, `order_id`, `create_time`) VALUES
(1, 11, 3.00, 1, '订单ORD20260105001完成收益', 1, '2026-01-05 10:45:00'),
(2, 12, 2.50, 1, '订单ORD20260106001完成收益', 2, '2026-01-06 11:50:00'),
(3, 11, 5.00, 1, '订单ORD20260107001完成收益', 3, '2026-01-07 15:00:00'),
(4, 12, 4.00, 1, '订单ORD20260108001完成收益', 4, '2026-01-08 10:10:00'),
(5, 13, 3.50, 1, '订单ORD20260110001完成收益', 5, '2026-01-10 17:00:00'),
(6, 11, 200.00, 2, '提现申请（提现到微信）', NULL, '2026-01-20 10:00:00'),
(7, 12, 150.00, 2, '提现申请（提现到支付宝）', NULL, '2026-01-25 11:00:00'),
(8, 11, 300.00, 2, '提现申请（提现到微信）', NULL, '2026-02-15 09:00:00'),
(9, 13, 80.00, 2, '提现申请（提现到银行卡）', NULL, '2026-02-20 14:00:00'),
(10, 11, 8.00, 1, '订单ORD20260301001完成收益', 18, '2026-03-01 10:00:00');
