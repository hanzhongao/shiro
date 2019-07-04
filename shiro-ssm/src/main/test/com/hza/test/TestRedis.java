package com.hza.test;

import com.hza.ssm.util.cache.RedisCache;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(locations = "classpath:spring/spring-test.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class TestRedis {
    @Autowired
    private RedisCache<Object,Object> retryRedisCache ;

    @Test
    public void TestGet() {
        System.out.println(this.retryRedisCache.get("admin"));
    }

}
