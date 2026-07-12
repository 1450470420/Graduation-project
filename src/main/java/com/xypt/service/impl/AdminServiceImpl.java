package com.xypt.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xypt.entity.Admin;
import com.xypt.mapper.AdminMapper;
import com.xypt.service.AdminService;
import org.springframework.stereotype.Service;
/** Admin Service实现类 */
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {}
