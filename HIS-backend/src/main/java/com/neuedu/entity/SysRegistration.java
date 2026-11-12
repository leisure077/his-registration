package com.neuedu.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 挂号记录表 实体类。
 *
 * @author wangyu
 * @since 2026-11-09
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("sys_registration")
public class SysRegistration implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 挂号单号（唯一）
     */
    private String regNo;

    /**
     * 患者ID（关联 sys_patient.id）
     */
    private Long patientId;

    /**
     * 就诊科室ID（关联 sys_dept.id）
     */
    private Long deptId;

    /**
     * 就诊医生ID（关联 sys_user.id）
     */
    private Long doctorId;

    /**
     * 挂号职称ID（关联 sys_title.id）
     */
    private Long titleId;

    /**
     * 挂号费（挂号时从职称表快照）
     */
    private BigDecimal regFee;

    /**
     * 就诊日期
     */
    private LocalDate regDate;

    /**
     * 就诊时段: 1-上午 2-下午
     */
    private String regPeriod;

    /**
     * 挂号类型: 1-普通挂号 2-预约挂号
     */
    private String regType;

    /**
     * 状态: 1-待就诊 2-已就诊 3-已取消
     */
    private String status;

    /**
     * 备注
     */
    private String remark;

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
