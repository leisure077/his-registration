package com.neuedu.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import com.neuedu.entity.SysDept;

import java.util.List;

/**
 * 科室表 服务层。
 *
 * @author wangyu
 * @since 2026-11-04
 */
public interface SysDeptService extends IService<SysDept> {
    Page<SysDept> list(Integer pageNo, Integer pageSize, String name);
    boolean check(String column, String value, Long id);
    List<SysDept> listAll();
    boolean add(SysDept sysDept);
    boolean update(SysDept sysDept);
    boolean del(SysDept sysDept);
}
