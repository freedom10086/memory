package com.tencent.memory.controller;

import com.tencent.memory.config.Config;
import com.tencent.memory.model.ApiResult;
import com.tencent.memory.model.ApiResultBuilder;
import com.tencent.memory.upload.CosStorageService;
import com.tencent.memory.upload.StorageFileNotFoundException;
import com.tencent.memory.upload.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 *  图片上传处理
 *  图片大小如果超过设置thumbnailLimitSize的大小会生成缩略图
 *  大小同样见配置maxThumbnailWidth & maxThumbnailHeight,缩略图会有尾缀‘S’不带尾缀访问的就是原图
 */
@RestController
public class UploadController {

    private static final Logger logger = LoggerFactory.getLogger(UploadController.class);

    private final StorageService storageService;

    @Autowired
    public UploadController(@Qualifier("CosStorageService") StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/files")
    @ResponseBody
    public List<String> listUploadedFiles() throws IOException {
        return storageService.loadAll().map(
                path -> MvcUriComponentsBuilder.fromMethodName(UploadController.class,
                        "serveFile", path.getFileName().toString()).build().toString())
                .collect(Collectors.toList());
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @PostMapping("/files")
    @ResponseBody
    public ApiResult<String> handleFileUpload(@RequestParam("file") MultipartFile file) {
        long start = System.currentTimeMillis();
        String path = storageService.store(file);
        logger.info("upload cost: {}ms", System.currentTimeMillis() - start);
        if (storageService instanceof CosStorageService) {
            return new ApiResultBuilder<String>().success(Config.bucketPathCdnPrefix + path + ".jpg").build();
        } else {
            return new ApiResultBuilder<String>().success(file.getOriginalFilename()).build();
        }

    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }
}
