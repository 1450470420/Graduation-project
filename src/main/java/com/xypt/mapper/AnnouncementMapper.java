package com.xypt.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xypt.entity.Announcement;
import org.apache.ibatis.annotations.Mapper;
/** Announcement Mapper接口，继承BaseMapper获得基础CRUD功能 */
@Mapper
public interface AnnouncementMapper extends BaseMapper<Announcement> {}
