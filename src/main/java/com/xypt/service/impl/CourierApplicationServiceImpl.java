package com.xypt.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xypt.entity.CourierApplication;
import com.xypt.mapper.CourierApplicationMapper;
import com.xypt.service.CourierApplicationService;
import org.springframework.stereotype.Service;
/** CourierApplication Service实现类 */
@Service
public class CourierApplicationServiceImpl extends ServiceImpl<CourierApplicationMapper, CourierApplication> implements CourierApplicationService {}
