package com.neuedu.controller;

import com.mybatisflex.core.paginate.Page;
import com.neuedu.vo.Result;
import jakarta.annotation.Resource;
import com.neuedu.entity.SysUser;
import com.neuedu.service.SysUserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 医护人员表 控制层。
 *
 * @author wangyu
 * @since 2026-11-09
 */
@RestController
@RequestMapping("/sysUser")
public class SysUserController {

    @Resource
    private SysUserService sysUserService;

    @GetMapping("list")
    Result list(Integer pageNo, Integer pageSize, String realName) {
        return Result.success(sysUserService.list(pageNo, pageSize, realName));
    }

    @PostMapping("add")
    Result add(SysUser sysUser, String password) {
        return Result.success(sysUserService.add(sysUser, password), "添加医护人员成功");
    }

    @PostMapping("del")
    Result remove(Long id, Boolean enabled) {
        SysUser sysUser = SysUser.builder()
                .id(id)
                .enabled(enabled)
                .build();
        return Result.success(sysUserService.updateById(sysUser), enabled ? "启用医护人员成功" : "删除医护人员成功");
    }

    @GetMapping("getInfo")
    Result getById(Long id) {
        return Result.success(sysUserService.getById(id));
    }

    @PostMapping("update")
    Result update(SysUser sysUser, String password) {
        return Result.success(sysUserService.update(sysUser, password), "修改医护人员成功");
    }

    @GetMapping("check")
    Result check(String column, String value, Long id) {
        return Result.success(sysUserService.check(column, value, id));
    }
    @GetMapping("getByDept")
    Result getByDept(Long deptId) {
        return Result.success(sysUserService.getByDept(deptId));
    }

    @PostMapping("login")
    Result login(String account, String password) {
        SysUser sysUser = sysUserService.login(account, password);
        if (sysUser == null) {
            return Result.fail("账号或密码错误");
        }
        return Result.success(sysUser, "登录成功");
    }

    @PostMapping("register")
    Result register(SysUser sysUser, String password) {
        if (!sysUserService.register(sysUser, password)) {
            return Result.fail("注册失败，请检查姓名、工号或手机号是否已被使用");
        }
        return Result.success(true, "注册成功");
    }
}
