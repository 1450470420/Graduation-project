package com.xypt.controller.admin;

import com.xypt.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 文件上传Controller
 * 支持图片上传，文件存储到项目根目录的upload文件夹
 * 路径前缀：/api/upload
 */
@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    /** 上传文件根路径（从application.yml读取） */
    @Value("${upload.path}")
    private String uploadPath;

    /** 文件访问URL前缀 */
    @Value("${upload.url-prefix}")
    private String urlPrefix;

    /**
     * 通用文件上传接口
     * POST /api/upload/file
     * 支持图片（jpg/png/gif/webp）和其他文件
     * 返回文件的访问URL，用于存储到数据库
     */
    @PostMapping("/file")
    public Result<String> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("请选择要上传的文件");
        }

        // 获取原始文件名和扩展名
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        }

        // 验证文件类型
        if (!extension.matches("\\.(jpg|jpeg|png|gif|webp|bmp)")) {
            return Result.error("只支持图片格式：jpg、png、gif、webp");
        }

        // 验证文件大小（最大5MB）
        if (file.getSize() > 5 * 1024 * 1024) {
            return Result.error("文件大小不能超过5MB");
        }

        // 按日期创建目录，避免文件过多在同一目录
        String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String dirPath = uploadPath + dateDir + "/";
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 生成唯一文件名（避免重名）
        String fileName = UUID.randomUUID().toString().replace("-", "") + extension;
        String filePath = dirPath + fileName;

        try {
            // 保存文件到磁盘
            file.transferTo(new File(filePath));

            // 返回可访问的URL（相对路径）
            String fileUrl = "/upload/" + dateDir + "/" + fileName;
            return Result.success("上传成功", fileUrl);

        } catch (IOException e) {
            return Result.error("文件上传失败：" + e.getMessage());
        }
    }

    /**
     * 头像上传接口
     * POST /api/upload/avatar
     * 头像单独存储到avatar目录
     */
    @PostMapping("/avatar")
    public Result<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("请选择头像文件");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        }

        if (!extension.matches("\\.(jpg|jpeg|png|gif|webp)")) {
            return Result.error("头像只支持图片格式");
        }

        String dirPath = uploadPath + "avatar/";
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String fileName = UUID.randomUUID().toString().replace("-", "") + extension;
        try {
            file.transferTo(new File(dirPath + fileName));
            String fileUrl = "/upload/avatar/" + fileName;
            return Result.success("上传成功", fileUrl);
        } catch (IOException e) {
            return Result.error("头像上传失败：" + e.getMessage());
        }
    }
}
