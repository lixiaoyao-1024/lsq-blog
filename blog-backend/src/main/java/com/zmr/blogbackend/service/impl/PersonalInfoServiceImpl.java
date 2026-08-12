package com.zmr.blogbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zmr.blogbackend.entity.PersonalInfo;
import com.zmr.blogbackend.mapper.PersonalInfoMapper;
import com.zmr.blogbackend.service.PersonalInfoService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class PersonalInfoServiceImpl implements PersonalInfoService {

    private static final Set<String> SUPPORTED_VALUE_TYPES = Set.of("text", "link", "email");

    private final PersonalInfoMapper personalInfoMapper;

    public PersonalInfoServiceImpl(PersonalInfoMapper personalInfoMapper) {
        this.personalInfoMapper = personalInfoMapper;
    }

    @Override
    public List<PersonalInfo> listPublic() {
        return personalInfoMapper.selectList(sortedWrapper()
                .eq(PersonalInfo::getStatus, 1));
    }

    @Override
    public List<PersonalInfo> listAdmin() {
        return personalInfoMapper.selectList(sortedWrapper());
    }

    @Override
    public PersonalInfo create(PersonalInfo info) {
        validate(info);
        info.setId(null);
        info.setValueType(normalizeType(info.getValueType()));
        if (info.getSortOrder() == null) {
            info.setSortOrder(0);
        }
        if (info.getStatus() == null) {
            info.setStatus(1);
        }
        info.setDeleted(0);
        personalInfoMapper.insert(info);
        return info;
    }

    @Override
    public PersonalInfo update(Long id, PersonalInfo patch) {
        PersonalInfo existing = requireExisting(id);
        validate(patch);
        existing.setLabel(patch.getLabel());
        existing.setValue(patch.getValue());
        existing.setValueType(normalizeType(patch.getValueType()));
        existing.setSortOrder(patch.getSortOrder() == null ? existing.getSortOrder() : patch.getSortOrder());
        existing.setStatus(patch.getStatus() == null ? existing.getStatus() : patch.getStatus());
        personalInfoMapper.updateById(existing);
        return existing;
    }

    @Override
    public void delete(Long id) {
        PersonalInfo existing = requireExisting(id);
        existing.setDeleted(1);
        personalInfoMapper.updateById(existing);
    }

    private void validate(PersonalInfo info) {
        if (info == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "信息不能为空");
        }
        if (!StringUtils.hasText(info.getLabel())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "条目名称不能为空");
        }
        if (!StringUtils.hasText(info.getValue())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "条目内容不能为空");
        }
        String type = normalizeType(info.getValueType());
        if (!SUPPORTED_VALUE_TYPES.contains(type)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "不支持的渲染类型，仅支持 text / link / email");
        }
    }

    /** 渲染类型转小写；null 或空白时默认 text */
    private String normalizeType(String valueType) {
        if (!StringUtils.hasText(valueType)) {
            return "text";
        }
        return valueType.toLowerCase(Locale.ROOT);
    }

    private PersonalInfo requireExisting(Long id) {
        PersonalInfo existing = personalInfoMapper.selectOne(new LambdaQueryWrapper<PersonalInfo>()
                .eq(PersonalInfo::getId, id)
                .eq(PersonalInfo::getDeleted, 0));
        if (existing == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "个人信息不存在");
        }
        return existing;
    }

    private LambdaQueryWrapper<PersonalInfo> sortedWrapper() {
        return new LambdaQueryWrapper<PersonalInfo>()
                .eq(PersonalInfo::getDeleted, 0)
                .orderByAsc(PersonalInfo::getSortOrder)
                .orderByAsc(PersonalInfo::getId);
    }
}
