package com.neuedu.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serial;
import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 患者信息表 实体类。
 *
 * @author wangyu
 * @since 2026-11-09
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("sys_patient")
public class SysPatient implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Id(keyType = KeyType.Auto)
    private Long id;

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
