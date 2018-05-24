package com.qcloud.cos.transfer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.qcloud.cos.model.ResponseHeaderOverrides;

/**
 * An opaque token that holds some private state and can be used to resume a
 * paused download operation.
 */
public final class PersistableDownload extends PersistableTransfer {

    static final String TYPE = "download";

    @JsonProperty
    private final String pauseType = TYPE;

    /** The bucket name in Qcloud COS from where the object has to be downloaded. */
    @JsonProperty
    private final String bucketName;

    /** The name of the object in Qcloud COS that has to be downloaded. */
    @JsonProperty
    private final String key;

    /** The version id of the object in Qcloud COS to download. */
    @JsonProperty
    private final String versionId;

    /** Optional member indicating the byte range of data to retrieve */
    @JsonProperty
    private final long[] range;

    /**
     * Optional field that overrides headers on the response.
     */
    @JsonProperty
    private final ResponseHeaderOverrides responseHeaders;

    /**
     * File where the downloaded data is written.
     */
    @JsonProperty
    private final String file;

    public PersistableDownload() {
        this(null, null, null, null, null, null);
    }

    public PersistableDownload(
            @JsonProperty(value = "bucketName") String bucketName,
            @JsonProperty(value = "key") String key,
            @JsonProperty(value = "versionId") String versionId,
            @JsonProperty(value = "range") long[] range,
            @JsonProperty(value = "responseHeaders") ResponseHeaderOverrides responseHeaders,
            @JsonProperty(value = "file") String file) {
        this.bucketName = bucketName;
        this.key = key;
        this.versionId = versionId;
        this.range = range  == null ? null : range.clone();
        this.responseHeaders = responseHeaders;
        this.file = file;
    }

    /**
     * Returns the name of the bucket.
     */
    String getBucketName() {
        return bucketName;
    }

    /**
     * Returns the name of the object.
     */
    String getKey() {
        return key;
    }

    /**
     * Returns the version id of the object.
     */
    String getVersionId() {
        return versionId;
    }

    /**
     * Returns the byte range of the object to download.
     */
    long[] getRange() {
        return range  == null ? null : range.clone();
    }

    /**
     * Returns the optional response headers.
     */
    ResponseHeaderOverrides getResponseHeaders() {
        return responseHeaders;
    }

    /**
     * Returns the file where the object is to be downloaded.
     */
    String getFile() {
        return file;
    }

    String getPauseType() {
        return pauseType;
    }
}