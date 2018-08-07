package com.duanzhiang.utils.cache;

import java.util.concurrent.ConcurrentHashMap;

//concurrentHashMap实现简易本地缓存
public class ConcurrentHashMapTest {

    private static ConcurrentHashMap<String, String> cacheMap = new ConcurrentHashMap<>();
    /**
     * 获取缓存的对象
     *
     */
    public static String getCache(String account) {
        account = getCacheKey(account);
        // 如果缓冲中有该账号，则返回value
        if (cacheMap.containsKey(account)) {
            return cacheMap.get(account);
        }
        // 如果缓存中没有该账号，把该帐号对象缓存到concurrentHashMap中
        initCache(account);
        return cacheMap.get(account);
    }

    /**
     * 初始化缓存
     */
    private static void initCache(String account) {
        // 一般是进行数据库查询，将查询的结果进行缓存
        cacheMap.put(account, "18013093863");
    }

    /**
     * 拼接一个缓存key
     */
    private static String getCacheKey(String account) {
//注意多线程场景下，区分不同线程缓存，于线程id进行关联
        return Thread.currentThread().getId() + "-" + account;
    }

    /**
     * 移除缓存信息
     */
    public static void removeCache(String account) {
        cacheMap.remove(getCacheKey(account));
    }
}
