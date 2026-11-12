package com.neuedu.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.neuedu.entity.SysDept;
import com.neuedu.entity.SysPatient;
import com.neuedu.entity.SysRegistration;
import com.neuedu.entity.SysTitle;
import com.neuedu.entity.SysUser;
import com.neuedu.mapper.SysRegistrationMapper;
import com.neuedu.service.SysDeptService;
import com.neuedu.service.SysPatientService;
import com.neuedu.service.SysRegistrationService;
import com.neuedu.service.SysTitleService;
import com.neuedu.service.SysUserService;
import com.neuedu.vo.RegistrationVO;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 挂号记录表 服务层实现。
 *
 * @author wangyu
 * @since 2026-11-09
 */
@Service
public class SysRegistrationServiceImpl extends ServiceImpl<SysRegistrationMapper, SysRegistration>
        implements SysRegistrationService {

    @Resource
    private SysPatientService sysPatientService;

    @Resource
    private SysDeptService sysDeptService;

    @Resource
    private SysUserService sysUserService;

    @Resource
    private SysTitleService sysTitleService;

    @Override
    public Page<RegistrationVO> list(Integer pageNo, Integer pageSize, String patientName, String regDate, String status) {
        int no = pageNo == null ? 1 : pageNo;
        int size = pageSize == null ? 10 : pageSize;

        QueryWrapper wrapper = QueryWrapper.create();
        // 按患者姓名查询时，先查出患者ID集合，再过滤挂号记录
        if (StringUtils.isNotBlank(patientName)) {
            QueryWrapper patientWrapper = QueryWrapper.create();
            patientWrapper.like("patient_name", patientName.trim());
            List<SysPatient> patients = sysPatientService.list(patientWrapper);
            if (patients.isEmpty()) {
                Page<RegistrationVO> empty = new Page<>(no, size, 0);
                empty.setRecords(Collections.emptyList());
                return empty;
            }
            wrapper.in("patient_id", patients.stream().map(SysPatient::getId).collect(Collectors.toList()));
        }
        if (StringUtils.isNotBlank(regDate)) {
            wrapper.eq("reg_date", LocalDate.parse(regDate.trim()));
        }
        if (StringUtils.isNotBlank(status)) {
            wrapper.eq("status", status.trim());
        }
        wrapper.orderBy("id", false);

        Page<SysRegistration> page = this.page(Page.of(no, size), wrapper);
        List<SysRegistration> records = page.getRecords();

        Page<RegistrationVO> result = new Page<>(page.getPageNumber(), page.getPageSize(), page.getTotalRow());
        if (records == null || records.isEmpty()) {
            result.setRecords(Collections.emptyList());
            return result;
        }

        Map<Long, SysPatient> patientMap = new HashMap<>();
        List<Long> patientIds = records.stream().map(SysRegistration::getPatientId)
                .filter(Objects::nonNull).distinct().collect(Collectors.toList());
        if (!patientIds.isEmpty()) {
            sysPatientService.list(QueryWrapper.create().in("id", patientIds))
                    .forEach(item -> patientMap.put(item.getId(), item));
        }

        Map<Long, SysDept> deptMap = new HashMap<>();
        List<Long> deptIds = records.stream().map(SysRegistration::getDeptId)
                .filter(Objects::nonNull).distinct().collect(Collectors.toList());
        if (!deptIds.isEmpty()) {
            sysDeptService.list(QueryWrapper.create().in("id", deptIds))
                    .forEach(item -> deptMap.put(item.getId(), item));
        }

        Map<Long, SysUser> doctorMap = new HashMap<>();
        List<Long> doctorIds = records.stream().map(SysRegistration::getDoctorId)
                .filter(Objects::nonNull).distinct().collect(Collectors.toList());
        if (!doctorIds.isEmpty()) {
            sysUserService.list(QueryWrapper.create().in("id", doctorIds))
                    .forEach(item -> doctorMap.put(item.getId(), item));
        }

        Map<Long, SysTitle> titleMap = new HashMap<>();
        List<Long> titleIds = records.stream().map(SysRegistration::getTitleId)
                .filter(Objects::nonNull).distinct().collect(Collectors.toList());
        if (!titleIds.isEmpty()) {
            sysTitleService.list(QueryWrapper.create().in("id", titleIds))
                    .forEach(item -> titleMap.put(item.getId(), item));
        }

        List<RegistrationVO> vos = new ArrayList<>(records.size());
        for (SysRegistration record : records) {
            RegistrationVO vo = new RegistrationVO();
            vo.setId(record.getId());
            vo.setRegNo(record.getRegNo());
            vo.setPatientId(record.getPatientId());
            vo.setDeptId(record.getDeptId());
            vo.setDoctorId(record.getDoctorId());
            vo.setTitleId(record.getTitleId());
            vo.setRegFee(record.getRegFee());
            vo.setRegDate(record.getRegDate());
            vo.setRegPeriod(record.getRegPeriod());
            vo.setRegType(record.getRegType());
            vo.setStatus(record.getStatus());
            vo.setRemark(record.getRemark());
            vo.setCreateDate(record.getCreateDate());
            vo.setCreateTime(record.getCreateTime());

            SysPatient patient = patientMap.get(record.getPatientId());
            if (patient != null) {
                vo.setPatientName(patient.getPatientName());
                vo.setGender(patient.getGender());
                vo.setBirthday(patient.getBirthday());
                vo.setPhone(patient.getPhone());
                vo.setIdCard(patient.getIdCard());
                vo.setAddress(patient.getAddress());
            }
            SysDept dept = deptMap.get(record.getDeptId());
            if (dept != null) {
                vo.setDeptName(dept.getDeptName());
            }
            SysUser doctor = doctorMap.get(record.getDoctorId());
            if (doctor != null) {
                vo.setDoctorName(doctor.getRealName());
            }
            SysTitle title = titleMap.get(record.getTitleId());
            if (title != null) {
                vo.setTitleName(title.getTitleName());
            }
            vos.add(vo);
        }
        result.setRecords(vos);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean add(SysRegistration registration, SysPatient patient) {
        Long patientId = null;

        // 身份证号已存在则复用患者档案，不重复保存
        if (patient != null && StringUtils.isNotBlank(patient.getIdCard())) {
            SysPatient exist = sysPatientService.getByIdCard(patient.getIdCard());
            if (exist != null) {
                patientId = exist.getId();
            }
        }

        // 患者不存在则连同挂号记录一起保存
        if (patientId == null) {
            if (patient == null || StringUtils.isBlank(patient.getPatientName())) {
                return false;
            }
            patient.setId(null);
            if (patient.getEnabled() == null) {
                patient.setEnabled(true);
            }
            sysPatientService.save(patient);
            patientId = patient.getId();
        }

        // 职称和挂号费以医生档案为准，挂号费做快照
        SysUser doctor = sysUserService.getById(registration.getDoctorId());
        if (doctor != null) {
            if (registration.getDeptId() == null) {
                registration.setDeptId(doctor.getDeptId());
            }
            registration.setTitleId(doctor.getTitleId());
            SysTitle title = sysTitleService.getById(doctor.getTitleId());
            registration.setRegFee(title != null ? title.getRegFee() : BigDecimal.ZERO);
        }

        registration.setPatientId(patientId);
        if (StringUtils.isBlank(registration.getRegNo())) {
            registration.setRegNo(generateRegNo());
        }
        if (StringUtils.isBlank(registration.getStatus())) {
            registration.setStatus("1");
        }
        return this.save(registration);
    }

    @Override
    public SysPatient getPatientByIdCard(String idCard) {
        return sysPatientService.getByIdCard(idCard);
    }

    @Override
    public boolean updateStatus(Long id, String status) {
        SysRegistration registration = SysRegistration.builder()
                .id(id)
                .status(status)
                .build();
        return this.updateById(registration);
    }

    /**
     * 生成挂号单号：REG + 年月日时分秒毫秒
     */
    private String generateRegNo() {
        return "REG" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
    }
}
