package com.neuedu.controller;

import com.neuedu.vo.Result;
import jakarta.annotation.Resource;
import com.neuedu.entity.SysTitle;
import com.neuedu.service.SysTitleService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 职称表 控制层。
 *
 * @author wangyu
 * @since 2026-11-09
 */
@RestController
@RequestMapping("/sysTitle")
public class SysTitleController {

    @Resource
    private SysTitleService sysTitleService;

    @GetMapping("list")
    Result list(Integer pageNo, Integer pageSize, String titleName) {
        return Result.success(sysTitleService.list(pageNo, pageSize, titleName));
    }

    @PostMapping("add")
    Result add(SysTitle sysTitle) {
        return Result.success(sysTitleService.save(sysTitle), "添加职称成功");
    }

    @PostMapping("del")
    Result remove(Long id, Boolean enabled) {
        SysTitle sysTitle = SysTitle.builder()
                .id(id)
                .enabled(enabled)
                .build();
        return Result.success(sysTitleService.updateById(sysTitle), enabled ? "启用职称成功" : "删除职称成功");
    }

    @GetMapping("getInfo")
    Result getById(Long id) {
        return Result.success(sysTitleService.getById(id));
    }

    @PostMapping("update")
    Result update(SysTitle sysTitle) {
        return Result.success(sysTitleService.updateById(sysTitle), "修改职称成功");
    }

    @GetMapping("check")
    Result check(String column, String value, Long id) {
        return Result.success(sysTitleService.check(column, value, id));
    }
    @GetMapping("listAll")
    Result listAll() {
        return Result.success(sysTitleService.listAll());
    }
}
