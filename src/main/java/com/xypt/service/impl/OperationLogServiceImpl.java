package com.xypt.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xypt.entity.OperationLog;
import com.xypt.mapper.OperationLogMapper;
import com.xypt.service.OperationLogService;
import org.springframework.stereotype.Service;
/** OperationLog Service实现类 */
@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements OperationLogService {}
