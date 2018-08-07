//package com.duanzhiang.utils.cache;
//
//import java.util.HashMap;
//import java.util.Timer;
//import java.util.concurrent.ConcurrentHashMap;
//
//public class LocalCacheBinaHandler {
//
//    private static final long SECOND_TIME = 1000;// 默认过期时间 20秒
//    private static final int DEFUALT_VALIDITY_TIME = 20;// 默认过期时间 20秒
//    private static final Timer timer;
//    private static final ConcurrentHashMap<String, byte[]> map;
//
//    static {
//        timer = new Timer();
//        map = new ConcurrentHashMap<String, byte[]>(new HashMap<String, byte[]>(1 << 20));
//    }
//
//    /**
//     * 增加缓存对象(默认失效时间20秒)
//     *
//     * @param key
//     * @param ce
//     */
//    public static void addCache(LocalCacheEntity localCacheEntity) {
//        addCache(localCacheEntity, DEFUALT_VALIDITY_TIME);
//    }
//
//    /**
//     * 增加缓存对象
//     *
//     * @param key
//     * @param ce
//     * @param validityTime
//     *            有效时间
//     */
//    public static synchronized void addCache(LocalCacheEntity localCacheEntity, int validityTime) {
//        map.put(localCacheEntity.getCacheKey(), serialize(localCacheEntity));
//        // 添加过期定时
//        timer.schedule(new TimeoutTimerTask(localCacheEntity.getCacheKey()), validityTime * SECOND_TIME);
//    }
//
//    /**
//     * 获取缓存对象
//     *
//     * @param key
//     * @return
//     */
//    public static synchronized LocalCacheEntity getCache(String key) {
//        Object obj = deSerialize(map.get(key));
//        if (obj instanceof LocalCacheEntity) {
//            return (LocalCacheEntity) obj;
//        }
//        return null;
//    }
//
//    /**
//     * 检查是否含有制定key的缓冲
//     *
//     * @param key
//     * @return
//     */
//    public static synchronized boolean isConcurrent(String key) {
//        return map.containsKey(key);
//    }
//
//    /**
//     * 删除缓存
//     *
//     * @param key
//     */
//    public static synchronized void removeCache(String key) {
//        map.remove(key);
//    }
//
//    /**
//     * 获取缓存大小
//     *
//     * @param key
//     */
//    public static int getCacheSize() {
//        return map.size();
//    }
//
//    /**
//     * 清除全部缓存
//     */
//    public static synchronized void clearCache() {
//        if (null != timer) {
//            timer.cancel();
//        }
//        map.clear();
//        System.out.println("clear cache");
//    }
//
//    /**
//     * 缓存失效清除任务
//     *
//     * @Description: TODO
//     * @author wenminggao
//     * @date 2015年5月27日 下午5:24:40
//     *
//     */
//    static class TimeoutTimerTask extends TimerTask {
//        private String ceKey;
//
//        public TimeoutTimerTask(String key) {
//            this.ceKey = key;
//        }
//
//        @Override
//        public void run() {
//            LocalCacheHandler.removeCache(ceKey);
//            System.out.println("remove : " + ceKey);
//        }
//    }
//
//    /**
//     * 对象序列化
//     *
//     * @param value
//     * @return
//     */
//    public static byte[] serialize(Object value) {
//        try {
//            Kryo kryo = new Kryo();
//            byte[] buffer = new byte[2048];
//            Output output = new Output(buffer);
//            kryo.writeClassAndObject(output, value);
//            return output.toBytes();
//        } catch (Exception e) {
//            return null;
//        }
//    }
//
//    /**
//     * 对象反序列化
//     *
//     * @param value
//     * @return
//     */
//    public static Object deSerialize(byte[] value) {
//        try {
//            Kryo kryo = new Kryo();
//            Input input = new Input(value);
//            return kryo.readClassAndObject(input);
//        } catch (Exception e) {
//            return null;
//        }
//    }
//
//}
