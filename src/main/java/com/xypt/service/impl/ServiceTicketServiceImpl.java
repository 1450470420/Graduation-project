package com.xypt.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xypt.entity.ServiceTicket;
import com.xypt.mapper.ServiceTicketMapper;
import com.xypt.service.ServiceTicketService;
import org.springframework.stereotype.Service;
/** ServiceTicket Service实现类 */
@Service
public class ServiceTicketServiceImpl extends ServiceImpl<ServiceTicketMapper, ServiceTicket> implements ServiceTicketService {}
