package com.tencent.memory.upload;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.StorageClass;
import com.qcloud.cos.region.Region;
import com.tencent.memory.config.Config;
import com.tencent.memory.model.MyException;
import com.tencent.memory.model.UploadResult;
import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.concurrent.Callable;

public class UploadTask implements Callable<UploadResult> {

    private static final Logger logger = LoggerFactory.getLogger(UploadTask.class);

    private byte[] data;
    private String contentType;
    private boolean thumbnail;
    private int maxWidth;
    private int maxHeight;

    public UploadTask(byte[] data, String contentType, boolean thumbnail, int maxWidth, int maxHeight) {
        this.data = data;
        this.thumbnail = thumbnail;
        this.contentType = contentType;
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
    }

    @Override
    public UploadResult call() throws Exception {
        long size = data.length;
        if (thumbnail) {
            double quality = 1.0f;
            if (size >= 10485760) { //10M
                quality = 0.90f;
            } else if (size >= 5242880) { //5M
                quality = 0.92f;
            } else if (size >= 2621440) { //2.5M
                quality = 0.96;
            } else if (size >= 1310720) { // 1.25M
                quality = 0.98;
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try {
                Thumbnails.of(new ByteArrayInputStream(data))
                        .outputQuality(quality)
                        .size(maxWidth, maxHeight)
                        .toOutputStream(outputStream);
            } catch (IOException e) {
                throw new MyException(HttpStatus.BAD_REQUEST, "unsupported image file");
            }
            data = outputStream.toByteArray();
        }

        size = data.length;
        logger.info("upload image size {}", size);
        InputStream inputStream = new ByteArrayInputStream(data);
        LocalDateTime dateTime = LocalDateTime.now();
        String destDir = Config.uploadPrefix + dateTime.getYear() + "/" + dateTime.getMonthValue() + "/" + dateTime.getDayOfMonth() + "/";
        String destFile = destDir + System.currentTimeMillis() + (thumbnail ? "S" : "");

        String etag = doUpload(inputStream, destFile + ".jpg", size);

        UploadResult result = new UploadResult();
        result.url = Config.bucketPathCdnPrefix + destFile.substring(Config.uploadPrefix.length()) + ".jpg";
        result.etag = etag;

        return result;
    }

    // 调用腾讯云sdk上传图片
    private String doUpload(InputStream inputStream, String savePath, long fileSize) {
        // 1 初始化用户身份信息(secretId, secretKey)
        COSCredentials cred = new BasicCOSCredentials(Config.secretId, Config.secretKey);
        // 2 设置bucket的区域, COS地域的简称请参照 https://www.qcloud.com/document/product/436/6224
        ClientConfig clientConfig = new ClientConfig(new Region("ap-guangzhou"));
        // 3 生成cos客户端
        COSClient cosclient = new COSClient(cred, clientConfig);
        // bucket名需包含appid
        String bucketName = Config.bucketNameWithId;

        ObjectMetadata objectMetadata = new ObjectMetadata();
        // 从输入流上传必须制定content length, 否则http客户端可能会缓存所有数据，存在内存OOM的情况
        objectMetadata.setContentLength(fileSize);
        // 默认下载时根据cos路径key的后缀返回响应的contentType, 上传时设置contenttype会覆盖默认值
        objectMetadata.setContentType(contentType);

        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, savePath, inputStream, objectMetadata);
        // 设置存储类型, 默认是标准(Standard), 低频(standard_ia), 近线(nearline)
        putObjectRequest.setStorageClass(StorageClass.Standard);
        try {
            PutObjectResult putObjectResult = cosclient.putObject(putObjectRequest);
            // putobjectResult会返回文件的etag
            String etag = putObjectResult.getETag();
            logger.info("upload success path:{} etag:{}", savePath, etag);
            return etag;
        } catch (CosClientException e) {
            throw new MyException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        } finally {
            // 关闭客户端
            cosclient.shutdown();
        }
    }
}
