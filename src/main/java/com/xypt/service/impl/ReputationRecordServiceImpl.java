package com.xypt.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xypt.entity.ReputationRecord;
import com.xypt.mapper.ReputationRecordMapper;
import com.xypt.service.ReputationRecordService;
import org.springframework.stereotype.Service;
/** ReputationRecord Service实现类 */
@Service
public class ReputationRecordServiceImpl extends ServiceImpl<ReputationRecordMapper, ReputationRecord> implements ReputationRecordService {}
