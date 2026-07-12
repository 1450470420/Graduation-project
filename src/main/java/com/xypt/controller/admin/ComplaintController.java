package com.xypt.controller.admin;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xypt.annotation.OperLog;
import com.xypt.common.PageResult;
import com.xypt.common.Result;
import com.xypt.entity.OrderComplaint;
import com.xypt.entity.Orders;
import com.xypt.entity.User;
import com.xypt.service.OrderComplaintService;
import com.xypt.service.OrdersService;
import com.xypt.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;
/** 投诉管理Controller */
@RestController
@RequestMapping("/api/admin/complaints")
public class ComplaintController {
    @Autowired OrderComplaintService complaintService;
    @Autowired OrdersService ordersService;
    @Autowired UserService userService;
    @GetMapping
    public Result<PageResult<Map<String,Object>>> list(@RequestParam(defaultValue="1") long current,
            @RequestParam(defaultValue="10") long size, @RequestParam(required=false) Integer status) {
        Page<OrderComplaint> page = new Page<>(current, size);
        LambdaQueryWrapper<OrderComplaint> w = new LambdaQueryWrapper<>();
        if (status != null) w.eq(OrderComplaint::getStatus, status);
        w.orderByDesc(OrderComplaint::getCreateTime);
        Page<OrderComplaint> result = complaintService.page(page, w);
        List<Map<String,Object>> records = new ArrayList<>();
        result.getRecords().forEach(c -> {
            Map<String,Object> m = new HashMap<>();
            m.put("id",c.getId()); m.put("type",c.getType()); m.put("content",c.getContent());
            m.put("images",c.getImages()); m.put("status",c.getStatus()); m.put("result",c.getResult()); m.put("createTime",c.getCreateTime());
            Orders order = ordersService.getById(c.getOrderId());
            m.put("orderNo", order!=null?order.getOrderNo():"");
            User student = userService.getById(c.getStudentId());
            m.put("studentName", student!=null?student.getRealName():"");
            records.add(m);
        });
        return Result.success(PageResult.of(records, result.getTotal(), current, size));
    }
    @PutMapping("/{id}/handle")
    @OperLog("处理投诉")
    public Result<String> handle(@PathVariable Long id, @RequestBody Map<String,Object> body) {
        OrderComplaint c = complaintService.getById(id);
        if (c==null) return Result.error("投诉不存在");
        c.setStatus(2); c.setResult(body.getOrDefault("result","").toString());
        complaintService.updateById(c);
        return Result.success("处理完成");
    }
}
