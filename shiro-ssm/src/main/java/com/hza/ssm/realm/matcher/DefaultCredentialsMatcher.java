package com.hza.ssm.realm.matcher;

import com.hza.ssm.util.EncryptUtil;
import com.hza.ssm.util.cache.RedisCache;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;
import org.apache.shiro.cache.CacheManager;

import java.util.concurrent.atomic.AtomicInteger;

public class DefaultCredentialsMatcher extends SimpleCredentialsMatcher {
    private RedisCache<Object,Object> retryCache ;
    private String retryCacheName = "retryCache" ;
    private CacheManager cacheManager ;
    private long expire = 50L ; // 失效时间
    private int max = 5 ; // 尝试次数

    /**
     * 实现用户登录次数的计数统计的处理操作，其中将以用户id作为key
     * @param mid 要保存在Redis数据库中的key数据
     */
    private void retry(String mid) {
        // 每一次的计数都应该从之前计数的基础之上处理
        AtomicInteger num = (AtomicInteger) this.retryCache.get(mid);
        if (num == null) {
            num = new AtomicInteger(1) ; // 开始计数
        }
        if (num.incrementAndGet() > this.max) { // 达到最大尝试次数
            this.retryCache.putEx(mid,num,this.expire) ; // 锁定
            throw new ExcessiveAttemptsException("尝试次数过多，稍后再试");
        }
        this.retryCache.put(mid,num) ; // 继续计数
    }

    /**
     * 登录完成之后进行统计计数的解锁
     * @param mid
     */
    private void unlock(String mid) {
        this.retryCache.remove(mid) ;
    }

    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        String mid = (String) token.getPrincipal() ;
        this.retry(mid);
        // 1 通过token获取用户输入密码
        Object tokenPassword = EncryptUtil.encode(super.toString(token.getCredentials())) ;
        // 2 通过token获取正确的密码
        Object infoPassword = super.getCredentials(info) ;
        boolean flag =  super.equals(tokenPassword, infoPassword);
        if (flag) {
            this.unlock(mid);
        }
        return flag ;
    }

    public void setRetryCacheName(String retryCacheName) {
        this.retryCacheName = retryCacheName;
    }

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
        this.retryCache = (RedisCache<Object, Object>) this.cacheManager.getCache(this.retryCacheName);
    }

    public void setExpire(long expire) {
        this.expire = expire;
    }

    public void setMax(int max) {
        this.max = max;
    }
}
