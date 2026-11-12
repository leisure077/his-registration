package com.neuedu.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.neuedu.entity.SysPatient;
import com.neuedu.mapper.SysPatientMapper;
import com.neuedu.service.SysPatientService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * 患者信息表 服务层实现。
 *
 * @author wangyu
 * @since 2026-11-09
 */
@Service
public class SysPatientServiceImpl extends ServiceImpl<SysPatientMapper, SysPatient> implements SysPatientService {

    @Override
    public SysPatient getByIdCard(String idCard) {
        if (StringUtils.isBlank(idCard)) {
            return null;
        }
        QueryWrapper wrapper = QueryWrapper.create();
        wrapper.eq("id_card", idCard.trim());
        return this.getOne(wrapper);
    }
}
