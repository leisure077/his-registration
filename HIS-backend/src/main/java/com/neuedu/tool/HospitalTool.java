package com.neuedu.tool;

import com.mybatisflex.core.query.QueryWrapper;
import com.neuedu.entity.SysDept;
import com.neuedu.entity.SysPatient;
import com.neuedu.entity.SysRegistration;
import com.neuedu.entity.SysTitle;
import com.neuedu.entity.SysUser;
import com.neuedu.service.SysDeptService;
import com.neuedu.service.SysPatientService;
import com.neuedu.service.SysRegistrationService;
import com.neuedu.service.SysTitleService;
import com.neuedu.service.SysUserService;
import dev.langchain4j.agent.tool.Tool;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 提供给 AI 助手的只读工具，避免大模型直接操作数据库。
 */
@Component
public class HospitalTool {

    /**
     * 症状关键词 -> 科室名称
     */
    private static final String[][] SYMPTOM_RULES = {
            {"心|胸|悸|血压|冠心", "心内科"},
            {"咳|痰|喘|呼吸|肺|气短", "呼吸内科"},
            {"胃|腹|消化|恶心|呕吐|腹泻|便秘|反酸", "消化内科"},
            {"头晕|头痛|眩晕|麻木|失眠|抽搐", "神经内科"},
            {"肾|尿|水肿|腰酸", "肾内科"},
            {"外伤|骨折|扭伤|关节|腰腿痛", "骨科"},
            {"阑尾|疝|肿块|甲状腺|乳腺", "普外科"},
            {"眼|视力|视物模糊|眼痛", "眼科"},
            {"月经|怀孕|产检|妇科|白带", "妇产科"},
            {"小儿|儿童|宝宝|孩子", "儿科"},
            {"高热|昏迷|出血|剧痛|休克", "急诊科"}
    };

    @Resource
    private SysDeptService sysDeptService;

    @Resource
    private SysUserService sysUserService;

    @Resource
    private SysTitleService sysTitleService;

    @Resource
    private SysRegistrationService sysRegistrationService;

    @Resource
    private SysPatientService sysPatientService;

    @Tool("查询医院当前开放的全部科室，返回科室名称和编码")
    public String listDept() {
        List<SysDept> depts = sysDeptService.list(QueryWrapper.create()
                .eq("enabled", true)
                .orderBy("id", true));
        if (depts.isEmpty()) {
            return "暂无开放科室";
        }
        return depts.stream()
                .map(d -> d.getDeptName() + "(" + d.getDeptCode() + ")")
                .collect(Collectors.joining("、"));
    }

    @Tool("根据症状描述推荐挂号科室，参数 symptom 是用户描述的症状原文，例如“胸闷心慌”")
    public String recommendDept(String symptom) {
        if (StringUtils.isBlank(symptom)) {
            return "请描述具体症状，我再帮你推荐科室。";
        }
        for (String[] rule : SYMPTOM_RULES) {
            for (String keyword : rule[0].split("\\|")) {
                if (symptom.contains(keyword)) {
                    return "根据症状建议挂【" + rule[1] + "】。医院当前开放科室：" + listDept();
                }
            }
        }
        return "没有匹配到合适的科室，建议咨询导诊台。医院当前开放科室：" + listDept();
    }

    @Tool("查询指定科室的医生名单，返回医生姓名、职称和挂号费。参数 deptName 是科室名称，例如“心内科”")
    public String listDoctor(String deptName) {
        if (StringUtils.isBlank(deptName)) {
            return "请提供科室名称，例如“心内科”。";
        }
        List<SysDept> depts = sysDeptService.list(QueryWrapper.create()
                .like("dept_name", deptName.trim())
                .eq("enabled", true));
        if (depts.isEmpty()) {
            return "没有找到科室：" + deptName + "。当前开放科室：" + listDept();
        }
        StringBuilder result = new StringBuilder();
        for (SysDept dept : depts) {
            List<SysUser> doctors = sysUserService.list(QueryWrapper.create()
                    .eq("dept_id", dept.getId())
                    .eq("enabled", true));
            String doctorInfo = doctors.stream()
                    .filter(d -> d.getEmployeeNo() != null && d.getEmployeeNo().startsWith("D"))
                    .map(this::formatDoctor)
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.joining("；"));
            result.append(dept.getDeptName())
                    .append("：")
                    .append(doctorInfo.isEmpty() ? "暂未排班医生" : doctorInfo)
                    .append("。");
        }
        return result.toString();
    }

    @Tool("根据挂号单号查询挂号状态，参数 regNo 是挂号单号")
    public String getRegistration(String regNo) {
        if (StringUtils.isBlank(regNo)) {
            return "请提供挂号单号。";
        }
        List<SysRegistration> records = sysRegistrationService.list(QueryWrapper.create()
                .eq("reg_no", regNo.trim()));
        if (records.isEmpty()) {
            return "没有找到挂号单号 " + regNo + " 的记录，请核对单号。";
        }
        SysRegistration record = records.get(0);
        SysPatient patient = record.getPatientId() == null ? null : sysPatientService.getById(record.getPatientId());
        SysDept dept = record.getDeptId() == null ? null : sysDeptService.getById(record.getDeptId());
        SysUser doctor = record.getDoctorId() == null ? null : sysUserService.getById(record.getDoctorId());
        return "挂号单号：" + record.getRegNo()
                + "，患者：" + (patient == null ? "未知" : patient.getPatientName())
                + "，科室：" + (dept == null ? "未知" : dept.getDeptName())
                + "，医生：" + (doctor == null ? "未知" : doctor.getRealName())
                + "，就诊日期：" + (record.getRegDate() == null ? "未知" : record.getRegDate())
                + "，挂号费：" + record.getRegFee() + "元"
                + "，状态：" + statusText(record.getStatus());
    }

    private String formatDoctor(SysUser doctor) {
        if (doctor.getTitleId() == null) {
            return "";
        }
        SysTitle title = sysTitleService.getById(doctor.getTitleId());
        if (title == null) {
            return "";
        }
        return doctor.getRealName() + "（" + title.getTitleName() + "，挂号费" + title.getRegFee() + "元）";
    }

    private String statusText(String status) {
        if (status == null) {
            return "未知";
        }
        return switch (status) {
            case "1" -> "待就诊";
            case "2" -> "已就诊";
            case "3" -> "已取消";
            default -> "未知";
        };
    }
}
