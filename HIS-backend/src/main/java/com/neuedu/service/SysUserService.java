package com.neuedu.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import com.neuedu.entity.SysUser;

import java.util.List;

/**
 * 医护人员表 服务层。
 *
 * @author wangyu
 * @since 2026-11-09
 */
public interface SysUserService extends IService<SysUser> {
    Page<SysUser> list(Integer pageNo, Integer pageSize, String realName);
    boolean check(String column, String value, Long id);
    boolean add(SysUser sysUser, String password);
    boolean update(SysUser sysUser, String password);
    List<SysUser> getByDept(Long deptId);
    SysUser login(String account, String password);
    boolean register(SysUser sysUser, String password);
}
