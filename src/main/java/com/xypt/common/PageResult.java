package com.xypt.common;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import java.util.List;

/**
 * 分页结果封装类
 * 封装MyBatis-Plus的分页查询结果
 */
@Data
public class PageResult<T> {

    /** 当前页码 */
    private long current;

    /** 每页大小 */
    private long size;

    /** 总记录数 */
    private long total;

    /** 总页数 */
    private long pages;

    /** 数据列表 */
    private List<T> records;

    /**
     * 从MyBatis-Plus的Page对象转换
     */
    public static <T> PageResult<T> of(Page<T> page) {
        PageResult<T> result = new PageResult<>();
        result.setCurrent(page.getCurrent());
        result.setSize(page.getSize());
        result.setTotal(page.getTotal());
        result.setPages(page.getPages());
        result.setRecords(page.getRecords());
        return result;
    }

    /**
     * 手动构建分页结果
     */
    public static <T> PageResult<T> of(List<T> records, long total, long current, long size) {
        PageResult<T> result = new PageResult<>();
        result.setRecords(records);
        result.setTotal(total);
        result.setCurrent(current);
        result.setSize(size);
        result.setPages((total + size - 1) / size);
        return result;
    }
}
