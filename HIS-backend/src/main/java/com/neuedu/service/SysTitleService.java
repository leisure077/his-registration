package com.neuedu.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import com.neuedu.entity.SysTitle;

import java.util.List;

/**
 * 职称表 服务层。
 *
 * @author wangyu
 * @since 2026-11-09
 */
public interface SysTitleService extends IService<SysTitle> {
    Page<SysTitle> list(Integer pageNo, Integer pageSize, String titleName);
    boolean check(String column, String value, Long id);
    List<SysTitle> listAll();
}
