package com.neuedu.vo;

import java.time.LocalDate;

import com.neuedu.entity.SysRegistration;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 挂号记录展示对象：挂号信息 + 患者信息 + 科室/医生/职称名称。
 *
 * @author wangyu
 * @since 2026-11-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RegistrationVO extends SysRegistration {

    /**
     * 患者姓名
     */
    private String patientName;

    /**
     * 性别: 0-未知 1-男 2-女
     */
    private String gender;

    /**
     * 出生日期
     */
    private LocalDate birthday;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 身份证号
     */
    private String idCard;

    /**
     * 家庭住址
     */
    private String address;

    /**
     * 科室名称
     */
    private String deptName;

    /**
     * 医生姓名
     */
    private String doctorName;

    /**
     * 职称名称
     */
    private String titleName;
}
