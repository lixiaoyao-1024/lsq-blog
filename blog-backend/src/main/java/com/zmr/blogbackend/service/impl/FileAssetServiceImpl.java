package com.zmr.blogbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zmr.blogbackend.entity.FileAsset;
import com.zmr.blogbackend.mapper.FileAssetMapper;
import com.zmr.blogbackend.service.FileAssetService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.security.DigestInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class FileAssetServiceImpl implements FileAssetService {

    private static final long MAX_FILE_SIZE = 20L * 1024 * 1024;

    /** 允许上传的文件扩展名（个人博客常见的图片/音频/视频/文档） */
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "jpg", "jpeg", "png", "gif", "webp", "svg", "avif", "ico",
            "mp3", "m4a", "aac", "ogg", "wav", "flac",
            "mp4", "webm",
            "pdf", "md", "txt"
    );

    private final FileAssetMapper fileAssetMapper;

    @Value("${app.upload-dir:./uploads}")
    private String uploadDir;

    public FileAssetServiceImpl(FileAssetMapper fileAssetMapper) {
        this.fileAssetMapper = fileAssetMapper;
    }

    @Override
    public FileAsset upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "上传文件为空");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "文件大小超过限制（20MB）");
        }

        String originalName = file.getOriginalFilename();
        String extension = extensionOf(originalName);
        if (extension == null || !ALLOWED_EXTENSIONS.contains(extension)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "不支持的文件类型");
        }

        String storageName = UUID.randomUUID().toString().replace("-", "") + "." + extension;
        Path directory = uploadDirectory();
        try {
            Files.createDirectories(directory);
        } catch (IOException exception) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "无法创建上传目录", exception);
        }

        Path target = directory.resolve(storageName);
        try {
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "保存文件失败", exception);
        }

        FileAsset asset = new FileAsset();
        asset.setOriginalName(originalName);
        asset.setStorageName(storageName);
        asset.setUrl("/uploads/" + storageName);
        asset.setMimeType(file.getContentType());
        asset.setFileSize(file.getSize());
        asset.setStorageType("local");
        asset.setSha256(sha256Of(target));
        asset.setDeleted(0);
        fileAssetMapper.insert(asset);
        return asset;
    }

    @Override
    public List<FileAsset> listRecent() {
        return fileAssetMapper.selectList(new LambdaQueryWrapper<FileAsset>()
                .eq(FileAsset::getDeleted, 0)
                .orderByDesc(FileAsset::getCreateTime)
                .last("LIMIT 50"));
    }

    @Override
    public void delete(Long id) {
        FileAsset asset = fileAssetMapper.selectById(id);
        if (asset == null || asset.getDeleted() != null && asset.getDeleted() == 1) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "文件不存在");
        }
        asset.setDeleted(1);
        fileAssetMapper.updateById(asset);
        try {
            Files.deleteIfExists(uploadDirectory().resolve(asset.getStorageName()));
        } catch (IOException exception) {
            // 磁盘文件清理失败不阻塞删除，仅忽略
        }
    }

    private Path uploadDirectory() {
        return Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    private static String extensionOf(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return null;
        }
        int dot = fileName.lastIndexOf('.');
        if (dot < 0 || dot == fileName.length() - 1) {
            return null;
        }
        return fileName.substring(dot + 1).toLowerCase(Locale.ROOT);
    }

    private static String sha256Of(Path target) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException exception) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "计算文件校验和失败", exception);
        }
        try (InputStream in = new DigestInputStream(Files.newInputStream(target), digest)) {
            byte[] buffer = new byte[8192];
            while (in.read(buffer) != -1) {
                // 逐块读取以推进摘要计算
            }
        } catch (IOException exception) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "计算文件校验和失败", exception);
        }
        return HexFormat.of().formatHex(digest.digest());
    }
}
