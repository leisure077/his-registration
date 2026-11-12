package com.neuedu.controller;

import com.neuedu.entity.SysPatient;
import com.neuedu.entity.SysRegistration;
import com.neuedu.service.SysRegistrationService;
import com.neuedu.vo.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 挂号记录表 控制层（患者信息由挂号服务统一处理，不单独提供接口）。
 *
 * @author wangyu
 * @since 2026-11-09
 */
@RestController
@RequestMapping("/sysRegistration")
public class SysRegistrationController {

    @Resource
    private SysRegistrationService sysRegistrationService;

    @GetMapping("list")
    Result list(Integer pageNo, Integer pageSize, String patientName, String regDate, String status) {
        return Result.success(sysRegistrationService.list(pageNo, pageSize, patientName, regDate, status));
    }

    /**
     * 根据身份证号识别患者是否存在，存在则返回患者信息用于前端回填。
     */
    @GetMapping("patient")
    Result patient(String idCard) {
        return Result.success(sysRegistrationService.getPatientByIdCard(idCard));
    }

    /**
     * 挂号：患者信息与挂号信息一并提交，患者已存在则只保存挂号记录。
     */
    @PostMapping("add")
    Result add(SysRegistration registration, SysPatient patient) {
        boolean success = sysRegistrationService.add(registration, patient);
        return success ? Result.success(true, "挂号成功") : Result.fail("患者姓名不能为空");
    }

    @GetMapping("getInfo")
    Result getById(Long id) {
        return Result.success(sysRegistrationService.getById(id));
    }

    /**
     * 完成就诊
     */
    @PostMapping("finish")
    Result finish(Long id) {
        return Result.success(sysRegistrationService.updateStatus(id, "2"), "完成就诊成功");
    }

    /**
     * 取消挂号
     */
    @PostMapping("cancel")
    Result cancel(Long id) {
        return Result.success(sysRegistrationService.updateStatus(id, "3"), "取消挂号成功");
    }
}
