package com.xypt.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xypt.entity.UserAddress;
import com.xypt.mapper.UserAddressMapper;
import com.xypt.service.UserAddressService;
import org.springframework.stereotype.Service;
/** UserAddress Service实现类 */
@Service
public class UserAddressServiceImpl extends ServiceImpl<UserAddressMapper, UserAddress> implements UserAddressService {}
