package com.zmr.blogbackend.service;

import com.zmr.blogbackend.entity.FileAsset;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileAssetService {

    FileAsset upload(MultipartFile file);

    List<FileAsset> listRecent();

    /** 软删除文件资源（deleted=1）并移除磁盘文件，不存在时返回 404 */
    void delete(Long id);
}
