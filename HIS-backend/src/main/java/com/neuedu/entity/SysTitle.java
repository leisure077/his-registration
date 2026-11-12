package com.neuedu.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;

import java.io.Serial;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 职称表 实体类。
 *
 * @author wangyu
 * @since 2026-11-09
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("sys_title")

public class SysTitle implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 职称名称：主任医师/副主任医师/主治医师/住院医师
     */
    private String titleName;

    /**
     * 挂号级别：专家号/副主任医师号/主治医师号/普通号
     */
    private String regLevel;

    /**
     * 挂号费（元）
     */
    private BigDecimal regFee;

    /**
     * 说明
     */
    private String description;

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
