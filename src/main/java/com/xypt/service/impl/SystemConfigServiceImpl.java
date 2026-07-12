package com.xypt.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xypt.entity.SystemConfig;
import com.xypt.mapper.SystemConfigMapper;
import com.xypt.service.SystemConfigService;
import org.springframework.stereotype.Service;
/** SystemConfig Service实现类 */
@Service
public class SystemConfigServiceImpl extends ServiceImpl<SystemConfigMapper, SystemConfig> implements SystemConfigService {}
