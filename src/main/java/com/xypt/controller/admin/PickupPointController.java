package com.xypt.controller.admin;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xypt.annotation.OperLog;
import com.xypt.common.PageResult;
import com.xypt.common.Result;
import com.xypt.entity.Area;
import com.xypt.entity.PickupPoint;
import com.xypt.service.AreaService;
import com.xypt.service.PickupPointService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;
/** 取件点管理Controller */
@RestController
@RequestMapping("/api/admin/pickup-points")
public class PickupPointController {
    @Autowired PickupPointService pickupPointService;
    @Autowired AreaService areaService;
    @GetMapping
    public Result<PageResult<Map<String,Object>>> list(@RequestParam(defaultValue="1") long current,
            @RequestParam(defaultValue="10") long size, @RequestParam(required=false) String keyword,
            @RequestParam(required=false) String type, @RequestParam(required=false) Long areaId) {
        Page<PickupPoint> page = new Page<>(current, size);
        LambdaQueryWrapper<PickupPoint> w = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(keyword)) w.like(PickupPoint::getName, keyword);
        if (StringUtils.isNotBlank(type)) w.eq(PickupPoint::getType, type);
        if (areaId != null) w.eq(PickupPoint::getAreaId, areaId);
        w.orderByDesc(PickupPoint::getCreateTime);
        Page<PickupPoint> result = pickupPointService.page(page, w);
        List<Map<String,Object>> records = new ArrayList<>();
        result.getRecords().forEach(p -> {
            Map<String,Object> m = new HashMap<>();
            m.put("id",p.getId()); m.put("name",p.getName()); m.put("type",p.getType());
            m.put("address",p.getAddress()); m.put("status",p.getStatus()); m.put("areaId",p.getAreaId());
            m.put("createTime",p.getCreateTime());
            Area area = p.getAreaId()!=null ? areaService.getById(p.getAreaId()) : null;
            m.put("areaName", area!=null ? area.getName() : "");
            records.add(m);
        });
        return Result.success(PageResult.of(records, result.getTotal(), current, size));
    }
    @GetMapping("/all")
    public Result<List<Map<String,Object>>> all() {
        List<PickupPoint> list = pickupPointService.list(new LambdaQueryWrapper<PickupPoint>().eq(PickupPoint::getStatus,1));
        List<Map<String,Object>> result = new ArrayList<>();
        list.forEach(p -> {
            Map<String,Object> m = new HashMap<>();
            m.put("id",p.getId()); m.put("name",p.getName()); m.put("type",p.getType()); m.put("address",p.getAddress());
            Area area = p.getAreaId()!=null ? areaService.getById(p.getAreaId()) : null;
            m.put("areaName", area!=null ? area.getName() : "");
            result.add(m);
        });
        return Result.success(result);
    }
    @PostMapping
    @OperLog("新增取件点")
    public Result<String> add(@RequestBody PickupPoint p) { pickupPointService.save(p); return Result.success("添加成功"); }
    @PutMapping("/{id}")
    @OperLog("编辑取件点")
    public Result<String> update(@PathVariable Long id, @RequestBody PickupPoint p) { p.setId(id); pickupPointService.updateById(p); return Result.success("更新成功"); }
    @DeleteMapping("/{id}")
    @OperLog("删除取件点")
    public Result<String> delete(@PathVariable Long id) { pickupPointService.removeById(id); return Result.success("删除成功"); }
}
