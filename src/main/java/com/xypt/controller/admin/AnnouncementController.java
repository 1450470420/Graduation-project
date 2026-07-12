package com.xypt.controller.admin;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xypt.annotation.OperLog;
import com.xypt.common.PageResult;
import com.xypt.common.Result;
import com.xypt.entity.Announcement;
import com.xypt.service.AnnouncementService;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
/** 公告管理Controller */
@RestController
@RequestMapping("/api/admin/announcements")
public class AnnouncementController {
    @Autowired AnnouncementService announcementService;
    @GetMapping
    public Result<PageResult<Announcement>> list(@RequestParam(defaultValue="1") long current,
            @RequestParam(defaultValue="10") long size, @RequestParam(required=false) String keyword,
            @RequestParam(required=false) Integer status, @RequestParam(required=false) String target) {
        Page<Announcement> page = new Page<>(current, size);
        LambdaQueryWrapper<Announcement> w = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(keyword)) w.like(Announcement::getTitle, keyword);
        if (status != null) w.eq(Announcement::getStatus, status);
        if (StringUtils.isNotBlank(target)) w.eq(Announcement::getTarget, target);
        w.orderByDesc(Announcement::getCreateTime);
        return Result.success(PageResult.of(announcementService.page(page, w)));
    }
    @PostMapping
    @OperLog("发布公告")
    public Result<String> add(@RequestBody Announcement a, HttpSession session) {
        Long adminId = (Long) session.getAttribute("adminId");
        a.setAdminId(adminId);
        announcementService.save(a);
        return Result.success("发布成功");
    }
    @PutMapping("/{id}")
    @OperLog("编辑公告")
    public Result<String> update(@PathVariable Long id, @RequestBody Announcement a) {
        a.setId(id); announcementService.updateById(a);
        return Result.success("更新成功");
    }
    @DeleteMapping("/{id}")
    @OperLog("删除公告")
    public Result<String> delete(@PathVariable Long id) { announcementService.removeById(id); return Result.success("删除成功"); }
    @PutMapping("/{id}/offline")
    @OperLog("下架公告")
    public Result<String> offline(@PathVariable Long id) {
        Announcement a = announcementService.getById(id);
        if (a!=null) { a.setStatus(0); announcementService.updateById(a); }
        return Result.success("已下架");
    }
}
