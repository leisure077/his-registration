package com.neuedu.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.neuedu.entity.SysTitle;
import com.neuedu.mapper.SysTitleMapper;
import com.neuedu.service.SysTitleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 职称表 服务层实现。
 *
 * @author wangyu
 * @since 2026-11-09
 */
@Service
public class SysTitleServiceImpl extends ServiceImpl<SysTitleMapper, SysTitle> implements SysTitleService {

    @Override
    public Page<SysTitle> list(Integer pageNo, Integer pageSize, String titleName) {
        QueryWrapper queryWrapper = new QueryWrapper();
        // 如果 titleName 没有值，就不按条件查，认为查询全部
        if (StringUtils.isNotBlank(titleName)) {
            queryWrapper.like("title_name", titleName.trim());
        }
        queryWrapper.orderBy("id", false);
        return this.page(Page.of(pageNo, pageSize), queryWrapper);
    }

    @Override
    public boolean check(String column, String value, Long id) {
        QueryWrapper wrapper = QueryWrapper.create();
        wrapper.eq(column, value);
        if (null != id) {
            wrapper.ne("id", id);
        }
        return this.count(wrapper) == 0;
    }

    @Override
    @Cacheable(value = "system", key = "'titlelist'")
    public List<SysTitle> listAll() {
        QueryWrapper wrapper = QueryWrapper.create();
        wrapper.eq("enabled", true);
        return this.list(wrapper);
    }
}
