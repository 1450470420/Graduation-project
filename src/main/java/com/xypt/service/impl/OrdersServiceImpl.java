package com.xypt.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xypt.entity.Orders;
import com.xypt.mapper.OrdersMapper;
import com.xypt.service.OrdersService;
import org.springframework.stereotype.Service;
/** Orders Service实现类 */
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {}
