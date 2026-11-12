package com.neuedu.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;
import java.time.OffsetDateTime;

import java.io.Serial;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 医护人员表 实体类。
 *
 * @author wangyu
 * @since 2026-11-09
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("sys_user")

public class SysUser implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 真实姓名（医生全部实名）
     */
    private String realName;

    /**
     * 密码（BCrypt加密）
     */
    private String password;

    /**
     * 手机号（可用于登录）
     */
    private String phone;

    /**
     * 邮箱（可用于登录）
     */
    private String email;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 性别: 0-未知 1-男 2-女
     */
    private String gender;

    /**
     * 所属科室ID（关联 sys_dept.id）
     */
    private Long deptId;

    /**
     * 职称ID（关联 sys_title.id）
     */
    private Long titleId;

    /**
     * 工号（唯一）
     */
    private String employeeNo;

    /**
     * 出生日期
     */
    private Date birthday;

    /**
     * 家庭住址
     */
    private String address;

    /**
     * 医生简介（擅长治疗的疾病和症状）
     */
    private String intro;

    /**
     * 最后登录时间
     */
    private OffsetDateTime loginTime;

    /**
     * 密码最后修改时间
     */
    private OffsetDateTime pwdUpdateTime;

    /**
     * 是否启用: true-启用 false-禁用
     */
    private Boolean enabled;

    /**
     * 创建日期
     */
    private Date createDate;

    /**
     * 创建时间
     */
    private Time createTime;

    /**
     * 更新日期
     */
    private Date updateDate;

    /**
     * 更新时间
     */
    private Time updateTime;

}
