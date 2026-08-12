package com.zmr.blogbackend.service;

import com.zmr.blogbackend.entity.PersonalInfo;

import java.util.List;

public interface PersonalInfoService {

    /** 公开列表：仅显示（status=1）且未删除，按 sort_order 升序 */
    List<PersonalInfo> listPublic();

    /** 管理端列表：全部未删除（含隐藏），按 sort_order 升序 */
    List<PersonalInfo> listAdmin();

    PersonalInfo create(PersonalInfo info);

    PersonalInfo update(Long id, PersonalInfo patch);

    void delete(Long id);
}
