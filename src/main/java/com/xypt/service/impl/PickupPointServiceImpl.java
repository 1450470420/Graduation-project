package com.xypt.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xypt.entity.PickupPoint;
import com.xypt.mapper.PickupPointMapper;
import com.xypt.service.PickupPointService;
import org.springframework.stereotype.Service;
/** PickupPoint Service实现类 */
@Service
public class PickupPointServiceImpl extends ServiceImpl<PickupPointMapper, PickupPoint> implements PickupPointService {}
