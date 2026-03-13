package com.yuezupai.controller;

import com.yuezupai.common.result.R;
import com.yuezupai.service.FileService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/v1/file")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    /**
     * 上传图片
     * POST /v1/file/upload
     * 参数：file(文件) + type(用途: item/review/evidence/avatar/chat)
     */
    @PostMapping("/upload")
    public R<Map<String, String>> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "type", defaultValue = "item") String type) {

        String url = fileService.upload(file, type);
        return R.ok(Map.of("url", url));
    }
}