package com.neuedu.controller;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.neuedu.core.ResultCode;
import com.neuedu.vo.Result;
import jakarta.annotation.Resource;
import lombok.Getter;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import com.neuedu.entity.SysDept;
import com.neuedu.service.SysDeptService;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 科室表 控制层。
 *
 * @author wangyu
 * @since 2026-11-04
 */
@RestController
@RequestMapping("/sysDept")
public class SysDeptController {

    @Resource
    private SysDeptService sysDeptService;
    @GetMapping("list")
    Result list (Integer pageNo, Integer pageSize, String name) {
        return Result.success(sysDeptService.list(pageNo, pageSize, name));
    }
    @PostMapping("add")
    Result add(SysDept sysDept) {
        return Result.success(sysDeptService.add(sysDept), "添加科室成功");
    }
    @PostMapping("del")
    Result remove(SysDept sysDept) {
        return Result.success(sysDeptService.del(sysDept), sysDept.getEnabled() ? "启用科室成功" : "删除科室成功");
    }
    @GetMapping("getInfo")
    Result getById(Long id) {
        return Result.success(sysDeptService.getById(id));
    }

    @PostMapping("update")
    Result update(SysDept sysDept) throws InterruptedException {
        return Result.success(sysDeptService.update(sysDept), "修改科室成功");
    }


    @GetMapping("check")
    Result check(String column, String value, Long id) {
        return Result.success(sysDeptService.check(column, value, id));
    }

    @GetMapping("listAll")
    Result listAll() {
        return Result.success(sysDeptService.listAll());
    }
}
