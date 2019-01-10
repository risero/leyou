package com.leyou.cart.service;

import com.leyo.auth.entity.UserInfo;
import com.leyou.cart.interceptor.UserInterceptor;
import com.leyou.cart.pojo.Cart;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 购物车业务
 *
 * author 暗氵愧
 * HostName dell
 * Date 2019/1/3 19:25
 */
@Service
public class CartService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String KEY_PRE = "cart:uid:";

    /**
     * 把商品添加进购物车
     *
     * @param cart
     */
    public void addCart(Cart cart) {
        // 获取当前用户
        UserInfo user = UserInterceptor.getUser();

        // key
        String key = KEY_PRE + user.getId();
        BoundHashOperations<String, Object, Object> operation = redisTemplate.boundHashOps(key);

        // hashkey
        String hashKey = cart.getSkuId().toString();

        // 记录cart的num
        Integer num = cart.getNum();

        // 判断当前购物车商品，是否存在
        if(operation.hasKey(hashKey)){
            // 是，修改数量
            String jsonCart = operation.get(hashKey).toString();
            cart = JsonUtils.parse(jsonCart, Cart.class);
            cart.setNum(cart.getNum() + num);// 这里不能加1，而是加上购买的数量
        }
        // 写回redis，存入的对象必须装成json字符串存储进去，iskuId不存在则新增。
        operation.put(cart.getSkuId().toString(), JsonUtils.serialize(cart));
    }


    public List<Cart> queryCartList() {
        // 获取登录的当前用户
        UserInfo user = UserInterceptor.getUser();
        // key
        String key = KEY_PRE + user.getId();
        if(!redisTemplate.hasKey(key)){
            // key不存在，返回404
            throw new LyException(ExceptionEnum.CART_NOT_FOUND);
        }
        // 存在，获取购物车的所有商品
        BoundHashOperations<String, Object, Object> operation = redisTemplate.boundHashOps(key);
        // 根据用户的所有购物车商品
        List<Cart> carts = operation.values().stream().map(o -> JsonUtils.parse(o.toString(), Cart.class))
                .collect(Collectors.toList());
        return carts;
    }

    /**
     * 修改商品数量
     *
     * @param skuId
     * @param num
     */
    public void updateNum(Long skuId, Integer num) {
        // 获取登录的当前用户
        UserInfo user = UserInterceptor.getUser();
        // key
        String key = KEY_PRE + user.getId();
        BoundHashOperations<String, Object, Object> operation = redisTemplate.boundHashOps(key);

        // hashkey
        String hashKey = skuId.toString();

        // 判断商品是否存在购物车中
        if(!operation.hasKey(hashKey)){
            throw new LyException(ExceptionEnum.CART_NOT_FOUND);
        }

        // 查询购物车的商品是否存在该skuId
        Cart cart = JsonUtils.parse(operation.get(hashKey).toString(), Cart.class);

        // 存在，则修改商品数量
        cart.setNum(num); // 直接设置num，因为在前端已经是加好了的，我们直接设置进行就行

        // 重新写入redis
        operation.put(hashKey, JsonUtils.serialize(cart));
    }

    /**
     * 删除购物车数据
     *
     * @param skuId
     */
    public void deleteCart(Long skuId) {
        // 获取登录的当前用户
        UserInfo user = UserInterceptor.getUser();
        // key
        String key = KEY_PRE + user.getId();
        // 删除
        redisTemplate.opsForHash().delete(key, skuId.toString());
    }

    /**
     * 登录后合并购物车商品
     *
     * @param carts
     */
    public void insertCartList(List<Cart> carts) {
        // 获取登录的当前用户
        UserInfo user = UserInterceptor.getUser();
        String key = KEY_PRE + user.getId();
        BoundHashOperations<String, Object, Object> operation = redisTemplate.boundHashOps(key);
        // 遍历商品
        for (Cart cart : carts) {
            // 判断商品是否存在
            if(operation.hasKey(cart.getSkuId().toString())){
                Cart cacheCart = JsonUtils.parse(operation.get(cart.getSkuId().toString()).toString(), Cart.class);
                // 商品存在，数量相加
                cacheCart.setNum(cacheCart.getNum() + cart.getNum());
                operation.put(cacheCart.getSkuId().toString(), JsonUtils.serialize(cacheCart));
            }else{
                // 商品不存在，则添加商品
                operation.put(cart.getSkuId().toString(), JsonUtils.serialize(cart));
            }
        }
    }
}
