package com.xypt.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xypt.entity.User;
import com.xypt.mapper.UserMapper;
import com.xypt.service.UserService;
import org.springframework.stereotype.Service;
/** User Service实现类 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {}
