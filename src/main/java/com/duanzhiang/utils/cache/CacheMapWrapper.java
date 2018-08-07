package com.duanzhiang.utils.cache;

import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

//增加缓存雪崩支持
public class CacheMapWrapper<K,V> {
    private static Logger LOGGER = LoggerFactory.getLogger(CacheMapWrapper.class);

    private final Map<K, V> data = new ConcurrentHashMap<K, V>();
    private final Map<K, Long> keysatime = new ConcurrentHashMap<K, Long>();
    private final Map<K, Long> keysutime = new ConcurrentHashMap<K, Long>();
    private Lock lock = new ReentrantLock();
    private volatile long atime = System.currentTimeMillis();
    private volatile long utime = -1;
    private long expire = 365 * 24 * 60 * 60 * 1000;// 缓存过期的时间，超过这个时间会主动清空缓存,默认时间很长基本等于不会主动失效。
    private long stale = 5 * 1000;// 缓存更新的时候，并发的线程使用过期数据的最长时间。默认5秒.

    //TODO:JMX
    private AtomicLong totalCount = new AtomicLong(0);
    private AtomicLong missConnt = new AtomicLong(0);

    /**
     *
     * @param expire
     *            缓存过期的时间，超过这个时间会主动清空缓存.
     */
    public CacheMapWrapper(long expire) {
        this.expire = expire;
    }

    /**
     *
     * @param expire
     *            缓存过期的时间，超过这个时间会主动清空缓存.
     * @param stale
     *            缓存更新的时候，并发的线程使用过期数据的时间。默认5秒.
     */
    public CacheMapWrapper(long expire, long stale) {
        this.expire = expire;
        this.stale = stale;
    }

    public CacheMapWrapper() {
    }

    public Map<K, V> getAll() {
        long now = System.currentTimeMillis();

        boolean flag = false;
        lock.lock();
        try {
            if (((atime + stale) < now) || (atime < utime)) {
                atime = now + stale;
                flag = true;
            }
        } finally {
            lock.unlock();
        }

        if (flag) {
            LOGGER.warn(Thread.currentThread().getId() + " going to flush the cache map.");
            return null;
        }

        return data;
    }

    public void setAll(Map<K, V> newMap) {
        data.clear();
        data.putAll(newMap);
        long now = System.currentTimeMillis();
        atime = now;
        utime = now;
    }

    public V get(K key) {
        V value = data.get(key);
        if (value == null) {
            return null;
        }

        long now = System.currentTimeMillis();
        boolean flag = false;
        lock.lock();
        try {
            Long katime = keysatime.get(key);
            Long kutime = keysutime.get(key);
            if (katime == null || kutime == null) {
                flag = true;
            } else if (katime < kutime || atime < utime || (katime + expire) < now || (atime + expire) < now) {
                atime = katime = now + stale;// delay 5 seconds
                keysatime.put(key, katime);
                flag = true;
            }
        } finally {
            lock.unlock();
        }

        totalCount.addAndGet(1);
        if (flag) {
            missConnt.addAndGet(1);
            LOGGER.warn(Thread.currentThread().getName() + " going to flush the cache key.");
            return null;
        }

        return value;
    }

    public void put(K key, V value) {
        data.put(key, value);
        long now = System.currentTimeMillis();
        keysatime.put(key, now);
        keysutime.put(key, now);
    }

    public void remove(K key) {
        lock.lock();
        try {
            keysutime.put(key, System.currentTimeMillis());
        } finally {
            lock.unlock();
        }
    }

    public void update() {
        lock.lock();
        try {
            utime = System.currentTimeMillis();
        } finally {
            lock.unlock();
        }

    }

    public void clear() {
        LOGGER.warn(Thread.currentThread().getName() + " clear the cache.");
        update();
    }
}
