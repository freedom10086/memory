package com.qcloud.cos.internal;

import java.io.File;
import java.io.FileInputStream;

import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.SSECustomerKey;
import com.qcloud.cos.model.UploadPartRequest;
import com.qcloud.cos.transfer.TransferManager;
import com.qcloud.cos.transfer.TransferManagerUtils;

/**
 * Factory for creating all the individual UploadPartRequest objects for a
 * multipart upload.
 * <p>
 * This allows us to delay creating each UploadPartRequest until we're ready for
 * it, instead of immediately creating thousands of UploadPartRequest objects
 * for each large upload, when we won't need most of those request objects for a
 * while.
 */
public class UploadPartRequestFactory {
    private final String bucketName;
    private final String key;
    private final String uploadId;
    private final long optimalPartSize;
    private final File file;
    private final PutObjectRequest origReq;
    private int partNumber = 1;
    private long offset = 0;
    private long remainingBytes;
    private SSECustomerKey sseCustomerKey;
    private final int totalNumberOfParts;

    /**
     * Wrapped to provide necessary mark-and-reset support for the underlying
     * input stream. In particular, it provides support for unlimited
     * mark-and-reset if the underlying stream is a {@link FileInputStream}.
     */
    private ReleasableInputStream wrappedStream;

    public UploadPartRequestFactory(PutObjectRequest origReq, String uploadId, long optimalPartSize) {
        this.origReq = origReq;
        this.uploadId = uploadId;
        this.optimalPartSize = optimalPartSize;
        this.bucketName = origReq.getBucketName();
        this.key = origReq.getKey();
        this.file = TransferManagerUtils.getRequestFile(origReq);
        this.remainingBytes = TransferManagerUtils.getContentLength(origReq);
        this.sseCustomerKey = origReq.getSSECustomerKey();
        this.totalNumberOfParts = (int) Math.ceil((double) this.remainingBytes
                / this.optimalPartSize);
        if (origReq.getInputStream() != null) {
            wrappedStream = ReleasableInputStream.wrap(origReq.getInputStream());
        }
    }

    public synchronized boolean hasMoreRequests() {
        return (remainingBytes > 0);
    }

    public synchronized UploadPartRequest getNextUploadPartRequest() {
        long partSize = Math.min(optimalPartSize, remainingBytes);
        boolean isLastPart = (remainingBytes - partSize <= 0);

        UploadPartRequest req = null;
        if (wrappedStream != null) {
            req = new UploadPartRequest()
                .withBucketName(bucketName)
                .withKey(key)
                .withUploadId(uploadId)
                .withInputStream(new InputSubstream(wrappedStream, 0, partSize, isLastPart))
                .withPartNumber(partNumber++)
                .withPartSize(partSize);
        } else {
            req = new UploadPartRequest()
                .withBucketName(bucketName)
                .withKey(key)
                .withUploadId(uploadId)
                .withFile(file)
                .withFileOffset(offset)
                .withPartNumber(partNumber++)
                .withPartSize(partSize);
        }
        TransferManager.appendMultipartUserAgent(req);

        if (sseCustomerKey != null) req.setSSECustomerKey(sseCustomerKey);

        offset += partSize;
        remainingBytes -= partSize;

        req.setLastPart(isLastPart);

        req.withGeneralProgressListener(origReq.getGeneralProgressListener())
           ;
        req.getRequestClientOptions().setReadLimit(origReq.getReadLimit());
        return req;
    }

    public int getTotalNumberOfParts() {
        return totalNumberOfParts;
    }

}
