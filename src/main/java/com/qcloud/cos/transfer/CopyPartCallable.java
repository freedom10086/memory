package com.qcloud.cos.transfer;

import com.qcloud.cos.COS;
import com.qcloud.cos.model.CopyPartRequest;
import com.qcloud.cos.model.PartETag;

import java.util.concurrent.Callable;

/**
 * An implementation of the Callable interface responsible for carrying out the
 * Copy part requests.
 *
 */
public class CopyPartCallable implements Callable<PartETag> {

    /** Reference to the COS client object used for initiating copy part request.*/
    private final COS cos;
    /** Copy part request to be initiated.*/
    private final CopyPartRequest request;

    public CopyPartCallable(COS cos, CopyPartRequest request) {
        this.cos = cos;
        this.request = request;
    }

    public PartETag call() throws Exception {
        return cos.copyPart(request).getPartETag();
    }
}