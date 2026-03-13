package com.yuezupai.service.impl;

import com.yuezupai.common.exception.BusinessException;
import com.yuezupai.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 本地文件存储实现
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "file.storage", havingValue = "local", matchIfMissing = true)
public class LocalFileServiceImpl implements FileService {

    @Value("${file.local.path}")
    private String basePath;

    @Value("${file.local.url-prefix}")
    private String urlPrefix;

    @Override
    public String upload(MultipartFile file, String type) {
        if (file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }

        // 按日期分目录: 2024/01/15/
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String dirPath = basePath + type + "/" + datePath + "/";

        File dir = new File(dirPath);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new BusinessException("创建目录失败");
        }

        // 生成唯一文件名
        String originalName = file.getOriginalFilename();
        String ext = "";
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf("."));
        }
        String fileName = UUID.randomUUID().toString().replace("-", "") + ext;

        // 保存文件
        try {
            file.transferTo(new File(dirPath + fileName));
        } catch (IOException e) {
            log.error("文件保存失败", e);
            throw new BusinessException("文件上传失败");
        }

        // 返回可访问URL
        String url = urlPrefix + type + "/" + datePath + "/" + fileName;
        log.info("文件上传成功: {}", url);
        return url;
    }
}