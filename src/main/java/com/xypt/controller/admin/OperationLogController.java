package com.xypt.controller.admin;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xypt.common.PageResult;
import com.xypt.common.Result;
import com.xypt.entity.OperationLog;
import com.xypt.service.OperationLogService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
/** 操作日志查看Controller */
@RestController
@RequestMapping("/api/admin/logs")
public class OperationLogController {
    @Autowired OperationLogService logService;
    @GetMapping
    public Result<PageResult<OperationLog>> list(@RequestParam(defaultValue="1") long current,
            @RequestParam(defaultValue="10") long size, @RequestParam(required=false) String operator,
            @RequestParam(required=false) Integer status) {
        Page<OperationLog> page = new Page<>(current, size);
        LambdaQueryWrapper<OperationLog> w = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(operator)) w.like(OperationLog::getOperator, operator);
        if (status != null) w.eq(OperationLog::getStatus, status);
        w.orderByDesc(OperationLog::getCreateTime);
        return Result.success(PageResult.of(logService.page(page, w)));
    }
}
