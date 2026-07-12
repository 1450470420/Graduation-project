package com.xypt.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xypt.entity.Evaluation;
import com.xypt.mapper.EvaluationMapper;
import com.xypt.service.EvaluationService;
import org.springframework.stereotype.Service;
/** Evaluation Service实现类 */
@Service
public class EvaluationServiceImpl extends ServiceImpl<EvaluationMapper, Evaluation> implements EvaluationService {}
