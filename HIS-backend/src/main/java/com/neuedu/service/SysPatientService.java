package com.neuedu.service;

import com.mybatisflex.core.service.IService;
import com.neuedu.entity.SysPatient;

/**
 * 患者信息表 服务层。
 *
 * @author wangyu
 * @since 2026-11-09
 */
public interface SysPatientService extends IService<SysPatient> {

    /**
     * 根据身份证号查询患者，不存在返回 null。
     */
    SysPatient getByIdCard(String idCard);
}
