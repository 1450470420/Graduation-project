package com.xypt.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xypt.entity.OrderRefund;
import com.xypt.mapper.OrderRefundMapper;
import com.xypt.service.OrderRefundService;
import org.springframework.stereotype.Service;
/** OrderRefund Service实现类 */
@Service
public class OrderRefundServiceImpl extends ServiceImpl<OrderRefundMapper, OrderRefund> implements OrderRefundService {}
