package com.xypt.controller.app;

import com.xypt.common.Result;
import com.xypt.entity.Orders;
import com.xypt.entity.User;
import com.xypt.service.OrdersService;
import com.xypt.service.UserService;
import com.xypt.utils.FaceCompareUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 人脸识别相关接口
 * 用于取货时防冒领的人脸验证功能
 */
@RestController
@RequestMapping("/api/app/face")
public class AppFaceController {

    @Autowired
    private UserService userService;
    @Autowired
    private OrdersService ordersService;

    @Value("${upload.path}")
    private String uploadPath;

    /**
     * 注册人脸照片
     * POST /api/app/face/register
     * 学生拍照上传，保存为人脸基准照片
     */
    @PostMapping("/register")
    public Result<String> registerFace(@RequestParam("file") MultipartFile file, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return Result.unauthorized();

        if (file.isEmpty()) return Result.error("请拍摄人脸照片");

        String originalFilename = file.getOriginalFilename();
        String extension = ".jpg";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        }

        String dirPath = uploadPath + "face/";
        File dir = new File(dirPath);
        if (!dir.exists()) dir.mkdirs();

        String fileName = "face_" + userId + "_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8) + extension;
        String filePath = dirPath + fileName;

        try {
            file.transferTo(new File(filePath));
            String faceUrl = "/upload/face/" + fileName;
            User user = userService.getById(userId);
            user.setFacePhoto(faceUrl);
            userService.updateById(user);
            return Result.success("人脸注册成功", faceUrl);
        } catch (IOException e) {
            return Result.error("人脸照片上传失败");
        }
    }

    /**
     * 获取当前用户人脸注册状态
     * GET /api/app/face/status
     */
    @GetMapping("/status")
    public Result<Map<String, Object>> faceStatus(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return Result.unauthorized();
        User user = userService.getById(userId);
        Map<String, Object> m = new HashMap<>();
        m.put("hasFace", user.getFacePhoto() != null && !user.getFacePhoto().isEmpty());
        m.put("facePhoto", user.getFacePhoto());
        return Result.success(m);
    }

    /**
     * 人脸验证（确认收货时使用）
     * POST /api/app/face/verify
     * 学生拍照后与注册照片进行比对
     * 参数：orderId（订单ID）+ file（实时拍摄的照片）
     */
    @PostMapping("/verify")
    public Result<Map<String, Object>> verifyFace(
            @RequestParam("file") MultipartFile file,
            @RequestParam("orderId") Long orderId,
            HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return Result.unauthorized();

        Orders order = ordersService.getById(orderId);
        if (order == null) return Result.error("订单不存在");
        if (!userId.equals(order.getStudentId())) return Result.error("无权操作此订单");
        if (order.getStatus() != 3) return Result.error("订单状态不正确，无法进行人脸验证");

        User student = userService.getById(userId);
        if (student.getFacePhoto() == null || student.getFacePhoto().isEmpty()) {
            return Result.error("您尚未注册人脸，请先在个人中心完成人脸注册");
        }

        if (file.isEmpty()) return Result.error("请拍摄人脸照片");

        try {
            String registeredPhotoPath = uploadPath + student.getFacePhoto().replace("/upload/", "");
            File registeredFile = new File(registeredPhotoPath);
            if (!registeredFile.exists()) {
                return Result.error("人脸基准照片丢失，请重新注册人脸");
            }

            String tempDir = uploadPath + "face/temp/";
            new File(tempDir).mkdirs();
            String tempFileName = "verify_" + userId + "_" + System.currentTimeMillis() + ".jpg";
            File tempFile = new File(tempDir + tempFileName);
            file.transferTo(tempFile);

            double similarity = FaceCompareUtil.compareFaces(registeredFile, tempFile);

            tempFile.delete();

            Map<String, Object> result = new HashMap<>();
            result.put("similarity", Math.round(similarity * 100));
            result.put("threshold", 70);

            if (similarity * 100 >= 70) {
                order.setFaceVerified(1);
                ordersService.updateById(order);
                result.put("passed", true);
                result.put("message", "人脸验证通过");
                return Result.success("人脸验证通过", result);
            } else {
                result.put("passed", false);
                result.put("message", "人脸验证未通过，请确认是本人操作");
                return Result.success("人脸验证未通过", result);
            }
        } catch (Exception e) {
            return Result.error("人脸验证异常：" + e.getMessage());
        }
    }

    /**
     * 查询订单的人脸验证状态
     * GET /api/app/face/order-status/{orderId}
     */
    @GetMapping("/order-status/{orderId}")
    public Result<Map<String, Object>> orderFaceStatus(@PathVariable Long orderId, HttpSession session) {
        if (session.getAttribute("userId") == null) return Result.unauthorized();
        Orders order = ordersService.getById(orderId);
        if (order == null) return Result.error("订单不存在");
        Map<String, Object> m = new HashMap<>();
        m.put("faceVerified", order.getFaceVerified() != null && order.getFaceVerified() == 1);
        return Result.success(m);
    }
}
