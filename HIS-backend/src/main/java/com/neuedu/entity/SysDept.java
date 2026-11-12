package com.neuedu.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;

import java.io.Serial;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 科室表 实体类。
 *
 * @author wangyu
 * @since 2026-11-04
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("sys_dept")

public class SysDept implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 科室名称
     */
    private String deptName;

    /**
     * 科室编码，唯一
     */
    private String deptCode;

    /**
     * 科室类型: 1-临床科室 2-行政科室 3-医技科室
     */
    private String deptType;

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
