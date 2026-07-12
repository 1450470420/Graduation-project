package com.xypt.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xypt.entity.Withdrawal;
import com.xypt.mapper.WithdrawalMapper;
import com.xypt.service.WithdrawalService;
import org.springframework.stereotype.Service;
/** Withdrawal Service实现类 */
@Service
public class WithdrawalServiceImpl extends ServiceImpl<WithdrawalMapper, Withdrawal> implements WithdrawalService {}
