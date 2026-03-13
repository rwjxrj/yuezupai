package com.yuezupai.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件存储接口（后期想切OSS只需加一个实现类）
 */
public interface FileService {
    /**
     * 上传文件
     * @param file 文件
     * @param type 用途：item/review/evidence/avatar/chat
     * @return 可访问的URL
     */
    String upload(MultipartFile file, String type);
}