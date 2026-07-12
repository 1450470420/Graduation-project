package com.xypt.controller.admin;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xypt.annotation.OperLog;
import com.xypt.common.PageResult;
import com.xypt.common.Result;
import com.xypt.entity.Orders;
import com.xypt.entity.User;
import com.xypt.service.OrdersService;
import com.xypt.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
/** 订单管理Controller */
@RestController
@RequestMapping("/api/admin/orders")
public class OrderManageController {
    @Autowired OrdersService ordersService;
    @Autowired UserService userService;
    @GetMapping
    public Result<PageResult<Map<String,Object>>> list(@RequestParam(defaultValue="1") long current,
            @RequestParam(defaultValue="10") long size, @RequestParam(required=false) String keyword,
            @RequestParam(required=false) Integer status, @RequestParam(required=false) String serviceType,
            @RequestParam(required=false) String startDate, @RequestParam(required=false) String endDate) {
        Page<Orders> page = new Page<>(current, size);
        LambdaQueryWrapper<Orders> w = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(keyword)) w.like(Orders::getOrderNo, keyword).or().like(Orders::getItemInfo,keyword);
        if (status != null) w.eq(Orders::getStatus, status);
        if (StringUtils.isNotBlank(serviceType)) w.eq(Orders::getServiceType, serviceType);
        // 日期范围筛选
        if (StringUtils.isNotBlank(startDate)) w.ge(Orders::getCreateTime, LocalDate.parse(startDate).atStartOfDay());
        if (StringUtils.isNotBlank(endDate)) w.le(Orders::getCreateTime, LocalDate.parse(endDate).atTime(23,59,59));
        w.orderByDesc(Orders::getCreateTime);
        Page<Orders> result = ordersService.page(page, w);
        List<Map<String,Object>> records = new ArrayList<>();
        result.getRecords().forEach(o -> {
            Map<String,Object> m = new HashMap<>(Map.of("id",o.getId(),"orderNo",o.getOrderNo(),"serviceType",o.getServiceType()!=null?o.getServiceType():"",
                "pickupAddress",o.getPickupAddress()!=null?o.getPickupAddress():"","deliveryAddress",o.getDeliveryAddress()!=null?o.getDeliveryAddress():"",
                "itemInfo",o.getItemInfo()!=null?o.getItemInfo():"","reward",o.getReward(),"status",o.getStatus(),"createTime",o.getCreateTime()));
            User student = userService.getById(o.getStudentId());
            m.put("studentName", student!=null?student.getRealName():"");
            m.put("studentUsername", student!=null?student.getUsername():"");
            if (o.getCourierId()!=null) {
                User courier = userService.getById(o.getCourierId());
                m.put("courierName", courier!=null?courier.getRealName():""); m.put("courierUsername", courier!=null?courier.getUsername():"");
            }
            records.add(m);
        });
        return Result.success(PageResult.of(records, result.getTotal(), current, size));
    }
    @GetMapping("/{id}")
    public Result<Map<String,Object>> detail(@PathVariable Long id) {
        Orders o = ordersService.getById(id);
        if (o==null) return Result.error("订单不存在");
        Map<String,Object> m = new HashMap<>();
        m.put("id",o.getId()); m.put("orderNo",o.getOrderNo()); m.put("serviceType",o.getServiceType());
        m.put("pickupAddress",o.getPickupAddress()); m.put("deliveryAddress",o.getDeliveryAddress());
        m.put("itemInfo",o.getItemInfo()); m.put("reward",o.getReward()); m.put("note",o.getNote()); 
        m.put("status",o.getStatus()); m.put("cancelReason",o.getCancelReason());
        m.put("createTime",o.getCreateTime()); m.put("grabTime",o.getGrabTime()); m.put("completeTime",o.getCompleteTime());
        User student = userService.getById(o.getStudentId());
        m.put("studentName", student!=null?student.getRealName():"");
        m.put("studentPhone", student!=null?student.getPhone():"");
        if (o.getCourierId()!=null) {
            User courier = userService.getById(o.getCourierId());
            m.put("courierName", courier!=null?courier.getRealName():""); m.put("courierPhone", courier!=null?courier.getPhone():"");
        }
        return Result.success(m);
    }
    @PutMapping("/{id}/cancel")
    @OperLog("强制取消订单")
    public Result<String> cancel(@PathVariable Long id, @RequestBody Map<String,String> body) {
        Orders o = ordersService.getById(id);
        if (o==null) return Result.error("订单不存在");
        o.setStatus(5); o.setCancelReason("管理员强制取消："+body.getOrDefault("reason",""));
        ordersService.updateById(o);
        return Result.success("已取消");
    }
}



