package com.hza.ssm.util.cache.abs;

import com.mysql.jdbc.TimeUtil;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.util.SerializationUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

public abstract class AbstractRedisCache<K, V> implements Cache<K, V> {

    // 1、此时定义有一个公共的SpringDataRedis的连接工厂接口实例
    private RedisConnectionFactory redisConnectionFactory ;

    /**
     *  2、通过外部传递一个RedisConnectionFactory接口实例
     * @param redisConnectionFactory 设置不为空的RedisConnectionFactory实例
     */
    public void setRedisConnectionFactory(RedisConnectionFactory redisConnectionFactory) {
        this.redisConnectionFactory = redisConnectionFactory;
    }

    /**
     * 3、在Redis操作的时候所有的处理使用了泛型定义，所以为了保证操作的公立性，所有的数据都变为字节数组
     * 本方法是实现对象序列化处理的操作转换
     * @param obj 要转换的对象
     * @return 序列化之后的对象字节数组
     */

    protected byte[] objectToArray(Object obj) { // 实现对象转换为字节数组的操作
        return SerializationUtils.serialize(obj) ; // spring 提供的序列化操作
    }

    /**
     * 4、字节数组与实例化对象之间的反序列化操作
     * @param data 包含有序列化对象信息的二进制数据
     * @return 反序列化后的实例化对象
     */
    protected Object arrayToObject(byte [] data) {
        return SerializationUtils.deserialize(data) ; // 反序列化
    }

    @Override
    public V get(K k) throws CacheException {  // 进行制定key的数据查询
        V resultObject = null;
        RedisConnection connection = null ;
        try {
            connection = this.redisConnectionFactory.getConnection() ;
            resultObject = (V) this.arrayToObject(connection.get(this.objectToArray(k))) ;
        } catch (Exception e) {
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
        return resultObject;
    }

    @Override
    public V put(K k, V v) throws CacheException {
        RedisConnection connection = null ;
        try {
            connection = this.redisConnectionFactory.getConnection() ;
            connection.set(this.objectToArray(k), this.objectToArray(v)) ;
        } catch (Exception e) {

        } finally {
            if (connection != null) {
                connection.close();
            }
        }
        return v;
    }

    /**
     * 设置一个保存时间的数据存储
     * @param k 要保存的数据key
     * @param v 保存数据的value
     * @param unit 处理的时间单元
     * @param expire 失效时间
     * @return 保存后的value数据
     * @throws CacheException 缓存异常
     */
    public V putEx(K k, V v, TimeUnit unit, long expire) throws CacheException {
        RedisConnection connection = null ;
        try {
            connection = this.redisConnectionFactory.getConnection();
            connection.setEx(this.objectToArray(k), TimeUnit.SECONDS.convert(expire, unit), this.objectToArray(v));
        } catch (Exception e) {
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
        return v;
    }
    /**
     * 设置一个保存时间的数据存储
     * @param k 要保存的数据key
     * @param v 保存数据的value
     * @param expire 失效时间（秒）
     * @return 保存后的value数据
     * @throws CacheException 缓存异常
     */
    public V putEx(K k, V v, long expire) throws CacheException {
        return this.putEx(k,v,TimeUnit.SECONDS,expire);
    }
    /**
     * 设置一个保存时间的数据存储
     * @param k 要保存的数据key
     * @param v 保存数据的value
     * @param expire 失效时间（秒）
     * @return 保存后的value数据
     * @throws CacheException 缓存异常
     */
    public V putEx(K k, V v, String expire) throws CacheException {
        return this.putEx(k,v,TimeUnit.SECONDS, Long.parseLong(expire));
    }

    @Override
    public V remove(K k) throws CacheException {
        V resultObject = null ;
        RedisConnection connection = null ;
        try {
            connection = this.redisConnectionFactory.getConnection() ;
            resultObject = this.get(k) ;
            connection.del(this.objectToArray(k)) ;
        } catch (Exception e) {
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
        return resultObject;
    }

    @Override
    public void clear() throws CacheException {
        RedisConnection connection = null ;
        try {
            connection = this.redisConnectionFactory.getConnection() ;
            connection.flushDb();
        } catch (Exception e) {
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    @Override
    public int size() {
        return this.keys().size();
    }

    @Override
    public Set<K> keys() {
        Set<K> allKeys = new HashSet<>() ;
        RedisConnection connection = null ;
        try {
            connection = this.redisConnectionFactory.getConnection() ;
            Set<byte[]> keys = connection.keys(this.objectToArray("*")) ;
            for (byte [] key : keys) {
                allKeys.add((K) this.arrayToObject(key)) ;
            }
        } catch (Exception e) {
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
        return allKeys;
    }

    @Override
    public Collection<V> values() {
        List<V> allValues = new ArrayList<>() ;
        RedisConnection connection = null ;
        try {
            connection = this.redisConnectionFactory.getConnection() ;
            Set<byte[]> keys = connection.keys(this.objectToArray("*")) ;
            for (byte [] key : keys) {
                allValues.add((V) this.arrayToObject(connection.get(key)));
            }
        } catch (Exception e) {

        } finally {
            if (connection != null) {
                connection.close();
            }
        }
        return allValues;
    }
}
