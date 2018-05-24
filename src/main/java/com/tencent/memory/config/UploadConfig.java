package com.tencent.memory.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("storage")
public class UploadConfig {

    /**
     * Folder location for storing files
     */
    private String location;
    /**
     * 是否允许生成缩略图
     */
    private boolean enableThumbnail;

    /**
     *  需要生成缩略图的界限
     */
    private long thumbnailLimitSize;

    /**
     *  缩略图大小
     */
    private int maxThumbnailWidth;
    private int maxThumbnailHeight;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isEnableThumbnail() {
        return enableThumbnail;
    }

    public void setEnableThumbnail(boolean enableThumbnail) {
        this.enableThumbnail = enableThumbnail;
    }

    public long getThumbnailLimitSize() {
        return thumbnailLimitSize;
    }

    public void setThumbnailLimitSize(long thumbnailLimitSize) {
        this.thumbnailLimitSize = thumbnailLimitSize;
    }

    public int getMaxThumbnailWidth() {
        return maxThumbnailWidth;
    }

    public void setMaxThumbnailWidth(int maxThumbnailWidth) {
        this.maxThumbnailWidth = maxThumbnailWidth;
    }

    public int getMaxThumbnailHeight() {
        return maxThumbnailHeight;
    }

    public void setMaxThumbnailHeight(int maxThumbnailHeight) {
        this.maxThumbnailHeight = maxThumbnailHeight;
    }
}
