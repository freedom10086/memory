package com.qcloud.cos.transfer;

import com.qcloud.cos.event.ProgressListenerChain;
import com.qcloud.cos.event.TransferStateChangeListener;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.exception.PauseException;
import com.qcloud.cos.model.UploadResult;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


public class UploadImpl extends AbstractTransfer implements Upload {
    private boolean isResumeableMultipartUploadAfterFailed = false;
    private PersistableUpload persistableUploadInfo = null;

    public UploadImpl(String description, TransferProgress transferProgressInternalState,
            ProgressListenerChain progressListenerChain, TransferStateChangeListener listener) {
        super(description, transferProgressInternalState, progressListenerChain, listener);
    }

    /**
     * Waits for this upload to complete and returns the result of this upload. This is a blocking
     * call. Be prepared to handle errors when calling this method. Any errors that occurred during
     * the asynchronous transfer will be re-thrown through this method.
     *
     * @return The result of this transfer.
     *
     * @throws CosClientException If any errors were encountered in the client while making the
     *         request or handling the response.
     * @throws CosServiceException If any errors occurred in Qcloud COS while processing the
     *         request.
     * @throws InterruptedException If this thread is interrupted while waiting for the upload to
     *         complete.
     */
    public UploadResult waitForUploadResult()
            throws CosClientException, CosServiceException, InterruptedException {
        try {
            UploadResult result = null;
            while (!monitor.isDone() || result == null) {
                Future<?> f = monitor.getFuture();
                result = (UploadResult) f.get();
            }
            return result;
        } catch (ExecutionException e) {
            rethrowExecutionException(e);
            return null;
        }
    }

    @Override
    public PersistableUpload pause() throws PauseException {
        PauseResult<PersistableUpload> pauseResult = pause(true);
        if (pauseResult.getPauseStatus() != PauseStatus.SUCCESS) {
            throw new PauseException(pauseResult.getPauseStatus());
        }
        return pauseResult.getInfoToResume();
    }

    /**
     * Tries to pause and return the information required to resume the upload operation.
     */
    private PauseResult<PersistableUpload> pause(final boolean forceCancelTransfers)
            throws CosClientException {
        UploadMonitor uploadMonitor = (UploadMonitor) monitor;
        return uploadMonitor.pause(forceCancelTransfers);
    }

    @Override
    public PauseResult<PersistableUpload> tryPause(boolean forceCancelTransfers) {
        return pause(forceCancelTransfers);
    }

    @Override
    public void abort() {
        UploadMonitor uploadMonitor = (UploadMonitor) monitor;
        uploadMonitor.performAbort();
    }

    @Override
    public boolean isResumeableMultipartUploadAfterFailed() {
        return this.isResumeableMultipartUploadAfterFailed;
    }

    @Override
    public PersistableUpload getResumeableMultipartUploadId() {
        if (this.isResumeableMultipartUploadAfterFailed) {
            return persistableUploadInfo;
        } else {
            return null;
        }
    }

    PersistableUpload getPersistableUploadInfo() {
        return persistableUploadInfo;
    }

    void setPersistableUploadInfo(PersistableUpload persistableUploadInfo) {
        this.persistableUploadInfo = persistableUploadInfo;
    }

    void setResumeableMultipartUploadAfterFailed(boolean isResumeableMultipartUploadAfterFailed) {
        this.isResumeableMultipartUploadAfterFailed = isResumeableMultipartUploadAfterFailed;
    }
}
