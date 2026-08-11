package com.zmr.blogbackend.controller;

import com.zmr.blogbackend.entity.FileAsset;
import com.zmr.blogbackend.service.FileAssetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/files")
public class FileAssetController {

    private final FileAssetService fileAssetService;

    public FileAssetController(FileAssetService fileAssetService) {
        this.fileAssetService = fileAssetService;
    }

    /**
     * 上传文件（图片/音频/视频/文档，单个最大 20MB）。
     * 当前为公开接口，后续接入管理后台与鉴权后应收紧。
     */
    @PostMapping("/upload")
    public FileAsset upload(@RequestParam("file") MultipartFile file) {
        return fileAssetService.upload(file);
    }

    @GetMapping
    public List<FileAsset> list() {
        return fileAssetService.listRecent();
    }

    /** 软删除文件资源并移除磁盘文件 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        fileAssetService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
