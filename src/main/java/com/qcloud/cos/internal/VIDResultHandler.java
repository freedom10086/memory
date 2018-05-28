package com.qcloud.cos.internal;

import com.qcloud.cos.Headers;
import com.qcloud.cos.http.CosHttpResponse;

import java.util.Map;

public class VIDResultHandler<T extends VIDResult> implements HeaderHandler<T> {
    @Override
    public void handle(T result, CosHttpResponse response) {
        Map<String, String> responseHeaderMap = response.getHeaders();
        result.setRequestId(responseHeaderMap.get(Headers.REQUEST_ID));
        result.setDateStr(responseHeaderMap.get(Headers.DATE));
    }
}
