-- =============================================
-- 人脸识别功能 数据库迁移脚本
-- 在已有数据库上执行此脚本即可添加人脸验证所需字段
-- =============================================

USE xypt_db;

-- 用户表增加人脸照片字段
ALTER TABLE `user` ADD COLUMN `face_photo` varchar(500) DEFAULT NULL COMMENT '人脸照片路径（用于取货人脸验证）';

-- 订单表增加人脸验证状态字段
ALTER TABLE `orders` ADD COLUMN `face_verified` tinyint DEFAULT 0 COMMENT '是否已通过人脸验证：0=未验证 1=已验证';
