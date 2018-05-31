package com.tencent.memory.upload;

import com.qcloud.image.ImageClient;
import com.qcloud.image.request.PornDetectRequest;
import com.tencent.memory.config.Config;
import com.tencent.memory.config.UploadConfig;
import com.tencent.memory.model.MyException;
import com.tencent.memory.model.UploadResult;
import com.tencent.memory.service.UploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Stream;

/**
 * 上传文件到腾讯云对象存储 cos
 * 返回CDN分发的地址
 */

@Service("CosStorageService")
public class CosStorageService implements UploadService {

    @Autowired
    private UploadConfig properties;

    private static final ExecutorService executor = Executors.newCachedThreadPool();
    private static final Logger logger = LoggerFactory.getLogger(CosStorageService.class);

    @Override
    public void init() {

    }

    @Override
    public UploadResult store(MultipartFile file) {
        long size = file.getSize();
        String contentType = file.getContentType();
        byte[] data;
        try {
            data = file.getBytes();
        } catch (IOException e) {
            throw new MyException(HttpStatus.BAD_REQUEST, "can not read file byte!");
        }

        return getUploadResult(size, contentType, data);
    }

    @Override
    public UploadResult store(InputStream inputStream) {
        long size;
        String contentType = "image/jpeg";
        byte[] data;
        try {
            data = FileCopyUtils.copyToByteArray(inputStream);
            size = data.length;
        } catch (IOException e) {
            throw new MyException(HttpStatus.BAD_REQUEST, "can not read file byte!");
        }
        return getUploadResult(size, contentType, data);
    }

    private UploadResult getUploadResult(long size, String contentType, byte[] data) {
        // 上传缩略图
        Future<UploadResult> ft = null;
        if (properties.isEnableThumbnail() && size > properties.getThumbnailLimitSize()) {
            ft = executor.submit(new UploadTask(data, contentType, true,
                    properties.getMaxThumbnailWidth(), properties.getMaxThumbnailHeight()));
        }

        // 上传原图
        Future<UploadResult> f = executor.submit(new UploadTask(data, contentType, false,
                properties.getMaxThumbnailWidth(), properties.getMaxThumbnailHeight()));
        try {
            UploadResult s = f.get();
            if (ft != null) {
                s = ft.get();
            }

            return s;
        } catch (InterruptedException | ExecutionException e) {
            throw new MyException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public Stream<Path> loadAll() {
        throw new MyException(HttpStatus.NOT_FOUND, "not supported");
    }

    @Override
    public Path load(String filename) {
        return Paths.get(Config.bucketPathCdnPrefix + filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        throw new MyException(HttpStatus.BAD_REQUEST, "not supported!");
    }

    @Override
    public void deleteAll() {
        throw new MyException(HttpStatus.BAD_REQUEST, "not supported!");
    }

    // 腾讯云鉴黄
    private void pornDetect(String image) {

        String appId = String.valueOf(Config.appId);
        String secretId = Config.secretId;
        String secretKey = Config.secretKey;
        ImageClient imageClient = new ImageClient(appId, secretId, secretKey);

        String ret;
        // 1. url方式
        long start = System.currentTimeMillis();
        String[] pornUrlList = new String[]{image};
        pornUrlList[0] = image;
        PornDetectRequest pornReq = new PornDetectRequest(Config.bucketName, pornUrlList);

        ret = imageClient.pornDetect(pornReq);
        logger.info("porn detect {} cost {}ms", image, (System.currentTimeMillis() - start));
        logger.debug("porn detect result: {}", ret);
        if (ret.contains("\"code\": 0")) {

        }

    }
}
