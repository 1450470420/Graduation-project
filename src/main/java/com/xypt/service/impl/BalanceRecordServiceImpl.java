package com.xypt.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xypt.entity.BalanceRecord;
import com.xypt.mapper.BalanceRecordMapper;
import com.xypt.service.BalanceRecordService;
import org.springframework.stereotype.Service;
/** BalanceRecord Service实现类 */
@Service
public class BalanceRecordServiceImpl extends ServiceImpl<BalanceRecordMapper, BalanceRecord> implements BalanceRecordService {}
