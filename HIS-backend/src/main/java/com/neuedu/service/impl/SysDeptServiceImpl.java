package com.neuedu.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.neuedu.entity.SysDept;
import com.neuedu.mapper.SysDeptMapper;
import com.neuedu.service.SysDeptService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 科室表 服务层实现。
 *
 * @author wangyu
 * @since 2026-11-04
 */
@Service
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept>  implements SysDeptService{

    @Override
    public Page<SysDept> list(Integer pageNo, Integer pageSize, String name) {
        QueryWrapper queryWrapper = new QueryWrapper();
        // 如果name没有值，就不按条件查，认为查询全部 没有值 的三种请求   null "" "    "
        if (StringUtils.isNotBlank(name)) {
            queryWrapper.like("dept_name", name.trim());
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
    @Cacheable(value = "system", key = "'deptlist'")
    public List<SysDept> listAll() {
        QueryWrapper wrapper = QueryWrapper.create();
        wrapper.eq("enabled", true);
        return this.list(wrapper);
    }

    @Override
    @CacheEvict(value = "system", key = "'deptlist'")
    public boolean add(SysDept sysDept) {
        return this.save(sysDept);
    }

    @Override
    @CacheEvict(value = "system", key = "'deptlist'")
    public boolean update(SysDept sysDept) {
        return this.updateById(sysDept);
    }

    @Override
    @CacheEvict(value = "system", key = "'deptlist'")
    public boolean del(SysDept sysDept) {
        return this.updateById(sysDept);
    }
}
