package com.neuedu.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import com.neuedu.entity.SysPatient;
import com.neuedu.entity.SysRegistration;
import com.neuedu.vo.RegistrationVO;

/**
 * 挂号记录表 服务层。
 *
 * @author wangyu
 * @since 2026-11-09
 */
public interface SysRegistrationService extends IService<SysRegistration> {

    /**
     * 分页查询挂号记录（含患者、科室、医生、职称信息）。
     */
    Page<RegistrationVO> list(Integer pageNo, Integer pageSize, String patientName, String regDate, String status);

    /**
     * 挂号：患者不存在时与挂号记录一起保存，已存在则直接复用患者档案。
     */
    boolean add(SysRegistration registration, SysPatient patient);

    /**
     * 根据身份证号查询患者信息，用于前端自动回填。
     */
    SysPatient getPatientByIdCard(String idCard);

    /**
     * 修改挂号状态。
     */
    boolean updateStatus(Long id, String status);
}
