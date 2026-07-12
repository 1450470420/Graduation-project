package com.xypt.controller.admin;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xypt.annotation.OperLog;
import com.xypt.common.PageResult;
import com.xypt.common.Result;
import com.xypt.entity.Area;
import com.xypt.service.AreaService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;
/** 校园区域管理Controller */
@RestController
@RequestMapping("/api/admin/areas")
public class AreaController {
    @Autowired AreaService areaService;
    /** 分页查询 */
    @GetMapping
    public Result<PageResult<Area>> list(@RequestParam(defaultValue="1") long current, @RequestParam(defaultValue="10") long size,
            @RequestParam(required=false) String keyword, @RequestParam(required=false) String type) {
        Page<Area> page = new Page<>(current, size);
        LambdaQueryWrapper<Area> w = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(keyword)) w.like(Area::getName, keyword);
        if (StringUtils.isNotBlank(type)) w.eq(Area::getType, type);
        w.orderByAsc(Area::getSortOrder).orderByDesc(Area::getCreateTime);
        return Result.success(PageResult.of(areaService.page(page, w)));
    }
    /** 查询所有启用区域（小程序用） */
    @GetMapping("/all")
    public Result<List<Area>> all() {
        return Result.success(areaService.list(new LambdaQueryWrapper<Area>().eq(Area::getStatus,1).orderByAsc(Area::getSortOrder)));
    }
    /** 新增 */
    @PostMapping
    @OperLog("新增校园区域")
    public Result<String> add(@RequestBody Area area) {
        areaService.save(area);
        return Result.success("添加成功");
    }
    /** 编辑 */
    @PutMapping("/{id}")
    @OperLog("编辑校园区域")
    public Result<String> update(@PathVariable Long id, @RequestBody Area area) {
        area.setId(id); areaService.updateById(area);
        return Result.success("更新成功");
    }
    /** 删除 */
    @DeleteMapping("/{id}")
    @OperLog("删除校园区域")
    public Result<String> delete(@PathVariable Long id) {
        areaService.removeById(id);
        return Result.success("删除成功");
    }
}
