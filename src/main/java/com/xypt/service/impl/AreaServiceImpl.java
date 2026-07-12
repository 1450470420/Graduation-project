package com.xypt.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xypt.entity.Area;
import com.xypt.mapper.AreaMapper;
import com.xypt.service.AreaService;
import org.springframework.stereotype.Service;
/** Area Service实现类 */
@Service
public class AreaServiceImpl extends ServiceImpl<AreaMapper, Area> implements AreaService {}
