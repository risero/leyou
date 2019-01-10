package com.leyou.demo.redisdemo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

/**
 * template是数据结构做了划分，得到对应的operation对象进行操作
 * redisTemplate.opsForHash().;
 *
 * 另一种方式，因为每次操作都要指定key很烦，所以可以使用boundHashOps("key")，一开始就指定key，后面只需要操作数据
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisDemoApplicationTests {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    public void contextLoads() {
        redisTemplate.opsForValue().set("test", "hello world");
        String ret = redisTemplate.opsForValue().get("test");
        System.out.println(ret);

        BoundHashOperations<String, String, String> hashops = redisTemplate.boundHashOps("user:123");
        hashops.put("name", "龙哥");
        hashops.put("age", "17");

        System.out.println(hashops.get("name"));
        System.out.println(hashops.get("age"));
        System.out.println(hashops.keys()); // 获取所有key
        System.out.println(hashops.values());  // 获取所有值
        Map<String, String> map = hashops.entries(); //获取hash的所有数据
        System.out.println(map);
    }
}

