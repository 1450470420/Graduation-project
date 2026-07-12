package com.xypt.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xypt.entity.CourierPreference;
import com.xypt.mapper.CourierPreferenceMapper;
import com.xypt.service.CourierPreferenceService;
import org.springframework.stereotype.Service;
/** CourierPreference Service实现类 */
@Service
public class CourierPreferenceServiceImpl extends ServiceImpl<CourierPreferenceMapper, CourierPreference> implements CourierPreferenceService {}
