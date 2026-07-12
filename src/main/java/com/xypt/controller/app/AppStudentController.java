package com.xypt.controller.app;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xypt.common.Result;
import com.xypt.entity.*;
import com.xypt.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.*;
/** 学生端个人中心、信息管理接口 */
@RestController
@RequestMapping("/api/app/student")
public class AppStudentController {
    @Autowired UserService userService;
    @Autowired ReputationRecordService reputationRecordService;
    @Autowired MessageService messageService;
    @Autowired BalanceRecordService balanceRecordService;
    @Autowired ServiceTicketService ticketService;
    @Autowired EvaluationService evaluationService;
    @Autowired OrderRefundService refundService;
    @Autowired OrderComplaintService complaintService;
    /** 获取个人信息 */
    @GetMapping("/profile")
    public Result<Map<String,Object>> profile(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId==null) return Result.unauthorized();
        User user = userService.getById(userId);
        if (user==null) return Result.error("用户不存在");
        Map<String,Object> m = new HashMap<>();
        m.put("id",user.getId()); m.put("username",user.getUsername()); m.put("realName",user.getRealName());
        m.put("phone",user.getPhone()); m.put("studentId",user.getStudentId()); m.put("avatar",user.getAvatar());
        m.put("dormAddress",user.getDormAddress()); m.put("reputation",user.getReputation()); m.put("balance",user.getBalance());
        m.put("isCertified",user.getIsCertified()); m.put("courierStatus",user.getCourierStatus());
        return Result.success(m);
    }
    /** 更新个人信息 */
    @PutMapping("/profile")
    public Result<String> updateProfile(@RequestBody Map<String,String> body, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId==null) return Result.unauthorized();
        User user = userService.getById(userId);
        if (body.containsKey("realName")) user.setRealName(body.get("realName"));
        if (body.containsKey("studentId")) user.setStudentId(body.get("studentId"));
        if (body.containsKey("phone")) user.setPhone(body.get("phone"));
        if (body.containsKey("dormAddress")) user.setDormAddress(body.get("dormAddress"));
        if (body.containsKey("avatar")) user.setAvatar(body.get("avatar"));
        userService.updateById(user);
        return Result.success("更新成功");
    }
    /** 实名认证 */
    @PostMapping("/certify")
    public Result<String> certify(@RequestBody Map<String,String> body, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId==null) return Result.unauthorized();
        User user = userService.getById(userId);
        user.setRealName(body.get("realName")); user.setStudentId(body.get("studentId")); user.setIsCertified(1);
        userService.updateById(user);
        return Result.success("实名认证成功");
    }
    /** 信誉分记录 */
    @GetMapping("/reputation")
    public Result<Map<String,Object>> reputation(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId==null) return Result.unauthorized();
        User user = userService.getById(userId);
        List<ReputationRecord> records = reputationRecordService.list(
            new LambdaQueryWrapper<ReputationRecord>().eq(ReputationRecord::getUserId,userId).orderByDesc(ReputationRecord::getCreateTime).last("limit 20"));
        Map<String,Object> m = new HashMap<>();
        m.put("score",user.getReputation()); m.put("records",records);
        return Result.success(m);
    }
    /** 余额和余额记录 */
    @GetMapping("/balance")
    public Result<Map<String,Object>> balance(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId==null) return Result.unauthorized();
        User user = userService.getById(userId);
        List<BalanceRecord> records = balanceRecordService.list(
            new LambdaQueryWrapper<BalanceRecord>().eq(BalanceRecord::getUserId,userId).orderByDesc(BalanceRecord::getCreateTime).last("limit 20"));
        Map<String,Object> m = new HashMap<>();
        m.put("balance",user.getBalance()); m.put("records",records);
        return Result.success(m);
    }
    /** 消息列表 */
    @GetMapping("/messages")
    public Result<List<Message>> messages(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId==null) return Result.unauthorized();
        return Result.success(messageService.list(
            new LambdaQueryWrapper<Message>().eq(Message::getUserId,userId).orderByDesc(Message::getCreateTime)));
    }
    /** 标记消息已读 */
    @PutMapping("/messages/{id}/read")
    public Result<String> readMessage(@PathVariable Long id, HttpSession session) {
        Message m = messageService.getById(id);
        if (m!=null) { m.setIsRead(1); messageService.updateById(m); }
        return Result.success("已读");
    }
    /** 提交客服工单 */
    @PostMapping("/tickets")
    public Result<String> submitTicket(@RequestBody Map<String,String> body, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId==null) return Result.unauthorized();
        ServiceTicket ticket = new ServiceTicket();
        ticket.setUserId(userId); ticket.setTitle(body.get("title")); ticket.setContent(body.get("content"));
        ticket.setStatus(0); ticket.setCreateTime(LocalDateTime.now());
        ticketService.save(ticket);
        return Result.success("提交成功，客服将尽快处理");
    }
    /** 我的工单列表 */
    @GetMapping("/tickets")
    public Result<List<ServiceTicket>> tickets(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId==null) return Result.unauthorized();
        return Result.success(ticketService.list(
            new LambdaQueryWrapper<ServiceTicket>().eq(ServiceTicket::getUserId,userId).orderByDesc(ServiceTicket::getCreateTime)));
    }
    /** 我的评价记录 */
    @GetMapping("/evaluations")
    public Result<List<Evaluation>> evaluations(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId==null) return Result.unauthorized();
        return Result.success(evaluationService.list(
            new LambdaQueryWrapper<Evaluation>().eq(Evaluation::getStudentId,userId).orderByDesc(Evaluation::getCreateTime)));
    }
    /** 退款记录 */
    @GetMapping("/refunds")
    public Result<List<OrderRefund>> refunds(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId==null) return Result.unauthorized();
        return Result.success(refundService.list(
            new LambdaQueryWrapper<OrderRefund>().eq(OrderRefund::getStudentId,userId).orderByDesc(OrderRefund::getCreateTime)));
    }
    /** 投诉记录 */
    @GetMapping("/complaints")
    public Result<List<OrderComplaint>> complaints(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId==null) return Result.unauthorized();
        return Result.success(complaintService.list(
            new LambdaQueryWrapper<OrderComplaint>().eq(OrderComplaint::getStudentId,userId).orderByDesc(OrderComplaint::getCreateTime)));
    }
}
