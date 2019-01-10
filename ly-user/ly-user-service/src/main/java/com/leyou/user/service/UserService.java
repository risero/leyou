package com.leyou.user.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.NumberUtils;
import com.leyou.user.mapper.UserMapper;
import com.leyou.user.pojo.User;
import com.leyou.user.utills.CodecUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * author 暗氵愧
 * HostName dell
 * Date 2018/12/27 20:22
 */
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String KEY_PREFIX = "user:verify:phone:";

    /**
     * 实现用户数据的校验，主要包括对：
     *  手机号、用户名的唯一性校验。
     *
     * @param data
     * @param type
     * @return
     */
    public Boolean checkData(String data, Integer type) {
        // 判断数据类型
        User record = new User();
        switch(type){
            case 1 :
                record.setUsername(data);
                break;
            case 2:
                record.setPhone(data);
                break; // 一定要有break;
            default:
                throw new LyException(ExceptionEnum.INVALID_USER_DATA_TYPE);
        }
        // 校验数据
        return userMapper.selectCount(record) == 0; // 可用
    }

    /**
     * 根据用户输入的手机号，生成随机验证码，长度为6位，纯数字。
     * 并且调用短信服务，发送验证码到用户手机。
     *
     * @param phone
     */
    public void sendCode(String phone) {
        // TODO 完成剩下的操作，验证码怎么解决，短信内容的定义
        Map msg = new HashMap<>();
        // 生成验证码
        String key = KEY_PREFIX + phone; // 验证码的key
        String code = NumberUtils.generateCode(6); // 生成6为随机数的验证码
        int outTime = 5; // 验证码过期时间为5分钟
        String content = "您的注册验证码是" + code + "，在" + outTime + "分钟内输入有效。如非本人操作请忽略此短信。";

        msg.put("code", code);
        msg.put("phone", phone);
        msg.put("content", content);

        // 发送验证码
        amqpTemplate.convertAndSend("ly.sms.exchange", "sms.verify.code", msg);

        // 保存验证码
        redisTemplate.opsForValue().set(key, code, outTime, TimeUnit.MINUTES);
    }

    /**
     * 用户注册
     *
     * @param user
     * @param code
     */
    public void register(User user, String code) {
        // 从redis中获取验证码
        String cacheCode = redisTemplate.opsForValue().get(KEY_PREFIX + user.getPhone());

        // 校验验证码
        if(!cacheCode.equals(code)){
            throw new LyException(ExceptionEnum.INVALID_VERIFY_CODE);
        }

        // 生成盐，这里使用工具类生成盐，其实是UUID
        String salt = CodecUtils.generateSalt(); // 保证盐随机，不要求唯一
        // 保存生成的盐，盐是随机的，不保存无法对密码进行校验
        user.setSalt(salt);

        // 对密码进行加密，返回加密后的密码
        user.setPassword(CodecUtils.md5Hex(user.getPassword(), salt));
        user.setCreated(new Date());

        // 写入数据库
        userMapper.insert(user);
    }

    /**
     * 用户登录
     *
     * @param username
     * @param password
     * @return
     */
    public User queryUserByUsernameAndPassword(String username, String password) {
        // 查询用户
        User record = new User();

        // 根据用户名查询
        record.setUsername(username);
        User user = userMapper.selectOne(record);

        // 校验
        if(user == null){
            throw new LyException(ExceptionEnum.INVALID_USERNAME_PASSWORD);
        }

        // 校验用户对象的密码和输入的密码是否相等
        if(! StringUtils.equals(user.getPassword(), CodecUtils.md5Hex(password, user.getSalt()))){
            throw new LyException(ExceptionEnum.INVALID_USERNAME_PASSWORD);
        }
        // 用户名和密码正确
        return user;
    }
}
