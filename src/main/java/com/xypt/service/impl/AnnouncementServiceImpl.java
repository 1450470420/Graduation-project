package com.xypt.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xypt.entity.Announcement;
import com.xypt.mapper.AnnouncementMapper;
import com.xypt.service.AnnouncementService;
import org.springframework.stereotype.Service;
/** Announcement Service实现类 */
@Service
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, Announcement> implements AnnouncementService {}
