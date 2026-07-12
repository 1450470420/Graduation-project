package com.xypt.controller.admin;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xypt.annotation.OperLog;
import com.xypt.common.PageResult;
import com.xypt.common.Result;
import com.xypt.entity.ServiceTicket;
import com.xypt.entity.User;
import com.xypt.service.ServiceTicketService;
import com.xypt.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;
/** 客服工单管理Controller */
@RestController
@RequestMapping("/api/admin/tickets")
public class ServiceTicketController {
    @Autowired ServiceTicketService ticketService;
    @Autowired UserService userService;
    @GetMapping
    public Result<PageResult<Map<String,Object>>> list(@RequestParam(defaultValue="1") long current,
            @RequestParam(defaultValue="10") long size, @RequestParam(required=false) Integer status) {
        Page<ServiceTicket> page = new Page<>(current, size);
        LambdaQueryWrapper<ServiceTicket> w = new LambdaQueryWrapper<>();
        if (status!=null) w.eq(ServiceTicket::getStatus, status);
        w.orderByDesc(ServiceTicket::getCreateTime);
        Page<ServiceTicket> result = ticketService.page(page, w);
        List<Map<String,Object>> records = new ArrayList<>();
        result.getRecords().forEach(t -> {
            Map<String,Object> m = new HashMap<>();
            m.put("id",t.getId()); m.put("title",t.getTitle()); m.put("content",t.getContent());
            m.put("status",t.getStatus()); m.put("reply",t.getReply()); m.put("createTime",t.getCreateTime());
            User user = userService.getById(t.getUserId());
            m.put("username", user!=null?user.getUsername():""); m.put("realName", user!=null?user.getRealName():"");
            records.add(m);
        });
        return Result.success(PageResult.of(records, result.getTotal(), current, size));
    }
    @PutMapping("/{id}/reply")
    @OperLog("回复客服工单")
    public Result<String> reply(@PathVariable Long id, @RequestBody Map<String,String> body) {
        ServiceTicket t = ticketService.getById(id);
        if (t==null) return Result.error("工单不存在");
        t.setStatus(1); t.setReply(body.get("reply")); ticketService.updateById(t);
        return Result.success("回复成功");
    }
    @PutMapping("/{id}/close")
    @OperLog("关闭客服工单")
    public Result<String> close(@PathVariable Long id) {
        ServiceTicket t = ticketService.getById(id);
        if (t==null) return Result.error("工单不存在");
        t.setStatus(2); ticketService.updateById(t);
        return Result.success("已关闭");
    }
}
