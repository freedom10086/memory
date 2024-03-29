package com.qcloud.cos.transfer;

import com.qcloud.cos.event.ProgressListenerChain;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;

import java.util.Collection;
import java.util.Collections;

/**
 * Multiple file upload when uploading an entire directory.
 */
public class MultipleFileUploadImpl extends MultipleFileTransfer<Upload> implements MultipleFileUpload {

    private final String keyPrefix;
    private final String bucketName;

    public MultipleFileUploadImpl(String description, TransferProgress transferProgress,
            ProgressListenerChain progressListenerChain, String keyPrefix, String bucketName, Collection<? extends Upload> subTransfers) {
        super(description, transferProgress, progressListenerChain, subTransfers);
        this.keyPrefix = keyPrefix;
        this.bucketName = bucketName;
    }

    /**
     * Returns the key prefix of the virtual directory being uploaded to.
     */
    public String getKeyPrefix() {
        return keyPrefix;
    }

    /**
     * Returns the name of the bucket to which files are uploaded.
     */
    public String getBucketName() {
        return bucketName;
    }

    /**
     * Waits for this transfer to complete. This is a blocking call; the current
     * thread is suspended until this transfer completes.
     *
     * @throws CosClientException
     *             If any errors were encountered in the client while making the
     *             request or handling the response.
     * @throws CosServiceException
     *             If any errors occurred in Qcloud COS while processing the
     *             request.
     * @throws InterruptedException
     *             If this thread is interrupted while waiting for the transfer
     *             to complete.
     */
    @Override
    public void waitForCompletion()
            throws CosClientException, CosServiceException, InterruptedException {
        if (subTransfers.isEmpty())
            return;
        super.waitForCompletion();
    }

    /* (non-Javadoc)
     * @see com.qcloud.cos.transfer.MultipleFileUpload#getSubTransfers()
     */
    @Override
    public Collection<? extends Upload> getSubTransfers() {
        return Collections.unmodifiableCollection(subTransfers);
    }

}