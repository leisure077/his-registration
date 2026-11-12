package com.neuedu.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.neuedu.entity.SysUser;
import com.neuedu.mapper.SysUserMapper;
import com.neuedu.service.SysUserService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * 医护人员表 服务层实现。
 *
 * @author wangyu
 * @since 2026-11-09
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Resource
    PasswordEncoder passwordEncoder;
    @Override
    public Page<SysUser> list(Integer pageNo, Integer pageSize, String realName) {
        QueryWrapper queryWrapper = new QueryWrapper();
        // 如果 realName 没有值，就不按条件查，认为查询全部
        if (StringUtils.isNotBlank(realName)) {
            queryWrapper.like("real_name", realName.trim());
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
    public boolean add(SysUser sysUser, String password) {
        String encode = passwordEncoder.encode(password);
        sysUser.setPassword(encode);
        return this.save(sysUser);
    }

    @Override
    public boolean update(SysUser sysUser, String password) {
        if (StringUtils.isBlank(password)) {
            sysUser.setPassword(null);
        }
        return this.updateById(sysUser);
    }

    @Override
    public List<SysUser> getByDept(Long deptId) {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("dept_id", deptId)
                .eq("enabled", true);
        return this.list(wrapper);
    }

    @Override
    public SysUser login(String account, String password) {
        if (StringUtils.isBlank(account) || StringUtils.isBlank(password)) {
            return null;
        }
        String key = account.trim();
        SysUser sysUser = findByColumn("employee_no", key);
        if (sysUser == null) {
            sysUser = findByColumn("phone", key);
        }
        if (sysUser == null) {
            sysUser = findByColumn("username", key);
        }
        if (sysUser == null || !passwordEncoder.matches(password, sysUser.getPassword())) {
            return null;
        }
        SysUser update = SysUser.builder()
                .id(sysUser.getId())
                .loginTime(OffsetDateTime.now())
                .build();
        this.updateById(update);
        sysUser.setLoginTime(update.getLoginTime());
        sysUser.setPassword(null);
        return sysUser;
    }

    @Override
    public boolean register(SysUser sysUser, String password) {
        if (sysUser == null || StringUtils.isAnyBlank(sysUser.getRealName(), password)) {
            return false;
        }
        String employeeNo = StringUtils.trimToNull(sysUser.getEmployeeNo());
        String phone = StringUtils.trimToNull(sysUser.getPhone());
        if (employeeNo == null && phone == null) {
            return false;
        }
        if (employeeNo != null && !check("employee_no", employeeNo, null)) {
            return false;
        }
        if (phone != null && !check("phone", phone, null)) {
            return false;
        }
        sysUser.setPassword(passwordEncoder.encode(password));
        sysUser.setEnabled(true);
        sysUser.setLoginTime(OffsetDateTime.now());
        if (StringUtils.isBlank(sysUser.getGender())) {
            sysUser.setGender("0");
        }
        return this.save(sysUser);
    }

    private SysUser findByColumn(String column, String value) {
        List<SysUser> list = this.list(QueryWrapper.create()
                .eq(column, value)
                .eq("enabled", true));
        return list.isEmpty() ? null : list.get(0);
    }
}
