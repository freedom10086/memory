package com.tencent.memory.upload;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qcloud.image.ImageClient;
import com.qcloud.image.PornDetectData;
import com.qcloud.image.PornDetectResultList;
import com.qcloud.image.request.PornDetectRequest;
import com.qcloud.image.request.TagDetectRequest;
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
            logger.info("new upload file size {}", size);
        } catch (IOException e) {
            throw new MyException(HttpStatus.BAD_REQUEST, "can not read file byte!");
        }

        if (size == 0) {
            throw new MyException(HttpStatus.BAD_REQUEST, "image size is zero!");
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
            // 鉴黄
            ImageClient imageClient = new ImageClient(String.valueOf(Config.appId),
                    Config.secretId, Config.secretKey);
            PornDetectRequest pornReq = new PornDetectRequest(Config.bucketName, new String[]{s.url});
            String ret = imageClient.pornDetect(pornReq);
            try {
                PornDetectResultList res = new ObjectMapper().readValue(ret, PornDetectResultList.class);
                if (res != null && res.result_list != null && res.result_list.size() == 1) {
                    if (res.result_list.get(0).code == 0) {
                        PornDetectData pornDetectData = res.result_list.get(0).data;
                        if (pornDetectData.result == 1) {
                            throw new MyException(HttpStatus.BAD_REQUEST, "图片涉及色情请重新选择");
                        } else if (pornDetectData.result == 2) {
                            logger.warn("image maybe porn {} confidence", res.result_list.get(0).url,
                                    res.result_list.get(0).data.confidence);
                        }
                        // ok image is ok
                    } else {
                        logger.warn("porn detect error {}", res.result_list.get(0).message);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (ft != null) {
                // 等待上传缩略图完成，如果已完成立即返回
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
