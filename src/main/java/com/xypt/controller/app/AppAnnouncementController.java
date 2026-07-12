package com.xypt.controller.app;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xypt.common.Result;
import com.xypt.entity.Announcement;
import com.xypt.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
/** 小程序端公告列表接口 */
@RestController
@RequestMapping("/api/app/announcements")
public class AppAnnouncementController {
    @Autowired AnnouncementService announcementService;
    @GetMapping
    public Result<List<Announcement>> list(@RequestParam(required=false) String target) {
        LambdaQueryWrapper<Announcement> w = new LambdaQueryWrapper<Announcement>().eq(Announcement::getStatus,1);
        if (target!=null&&!target.isEmpty()) w.and(q->q.eq(Announcement::getTarget,target).or().eq(Announcement::getTarget,"all"));
        w.orderByDesc(Announcement::getCreateTime);
        return Result.success(announcementService.list(w));
    }
    @GetMapping("/{id}")
    public Result<Announcement> detail(@PathVariable Long id) {
        return Result.success(announcementService.getById(id));
    }
}
