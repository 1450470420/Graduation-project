package com.xypt.controller.admin;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xypt.annotation.OperLog;
import com.xypt.common.Result;
import com.xypt.entity.SystemConfig;
import com.xypt.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
/** 系统配置管理Controller */
@RestController
@RequestMapping("/api/admin/configs")
public class SystemConfigController {
    @Autowired SystemConfigService configService;
    @GetMapping
    public Result<List<SystemConfig>> list() {
        return Result.success(configService.list(new LambdaQueryWrapper<SystemConfig>().orderByAsc(SystemConfig::getId)));
    }
    @PostMapping
    @OperLog("保存系统配置")
    public Result<String> save(@RequestBody SystemConfig config) {
        SystemConfig existing = configService.getOne(new LambdaQueryWrapper<SystemConfig>().eq(SystemConfig::getConfigKey, config.getConfigKey()));
        if (existing != null) { existing.setConfigValue(config.getConfigValue()); configService.updateById(existing); }
        else { configService.save(config); }
        return Result.success("保存成功");
    }
    @PutMapping("/{id}")
    @OperLog("更新系统配置")
    public Result<String> update(@PathVariable Long id, @RequestBody SystemConfig config) {
        config.setId(id); configService.updateById(config);
        return Result.success("更新成功");
    }
}
