package com.xypt.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xypt.entity.OrderComplaint;
import com.xypt.mapper.OrderComplaintMapper;
import com.xypt.service.OrderComplaintService;
import org.springframework.stereotype.Service;
/** OrderComplaint Service实现类 */
@Service
public class OrderComplaintServiceImpl extends ServiceImpl<OrderComplaintMapper, OrderComplaint> implements OrderComplaintService {}
