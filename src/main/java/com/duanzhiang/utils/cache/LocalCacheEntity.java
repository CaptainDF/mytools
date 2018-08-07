package com.duanzhiang.utils.cache;

import java.io.Serializable;

public class LocalCacheEntity implements Serializable {

    private static final long serialVersionUID = -3971709196436977492L;
    private final int DEFUALT_VALIDITY_TIME = 20;// 默认过期时间 20秒

    private String cacheKey; // 缓存key
    private Object cacheContent; // 缓存内容
    private int validityTime;// 有效期时长，单位：秒
    private long timeoutStamp;// 过期时间戳

    private LocalCacheEntity() {
        this.timeoutStamp = System.currentTimeMillis() + DEFUALT_VALIDITY_TIME * 1000;
        this.validityTime = DEFUALT_VALIDITY_TIME;
    }

    /**
     * @param cacheKey
     * @param cacheContent
     */
    public LocalCacheEntity(String cacheKey, Object cacheContent) {
        this();
        this.cacheKey = cacheKey;
        this.cacheContent = cacheContent;
    }

    public String getCacheKey() {
        return cacheKey;
    }

    public void setCacheKey(String cacheKey) {
        this.cacheKey = cacheKey;
    }

    public long getTimeoutStamp() {
        return timeoutStamp;
    }

    public void setTimeoutStamp(long timeoutStamp) {
        this.timeoutStamp = timeoutStamp;
    }

    public int getValidityTime() {
        return validityTime;
    }

    public void setValidityTime(int validityTime) {
        this.validityTime = validityTime;
    }

    public Object getCacheContent() {
        return cacheContent;
    }

    public void setCacheContent(Object cacheContent) {
        this.cacheContent = cacheContent;
    }
}
