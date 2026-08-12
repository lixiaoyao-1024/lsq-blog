package com.zmr.blogbackend.controller;

import com.zmr.blogbackend.entity.PersonalInfo;
import com.zmr.blogbackend.service.PersonalInfoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/personal-info")
public class PersonalInfoController {

    private final PersonalInfoService personalInfoService;

    public PersonalInfoController(PersonalInfoService personalInfoService) {
        this.personalInfoService = personalInfoService;
    }

    /** 公开列表：仅展示 status=1 的条目 */
    @GetMapping
    public List<PersonalInfo> listPublic() {
        return personalInfoService.listPublic();
    }

    /** 管理端列表：含隐藏条目 */
    @GetMapping("/admin")
    public List<PersonalInfo> listAdmin() {
        return personalInfoService.listAdmin();
    }

    /** 新增条目 */
    @PostMapping
    public PersonalInfo create(@RequestBody PersonalInfo info) {
        return personalInfoService.create(info);
    }

    /** 更新条目 */
    @PutMapping("/{id}")
    public PersonalInfo update(@PathVariable Long id, @RequestBody PersonalInfo info) {
        return personalInfoService.update(id, info);
    }

    /** 软删除 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        personalInfoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
