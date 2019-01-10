package com.leyou.cart.web;

import com.leyou.cart.pojo.Cart;
import com.leyou.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 购物车Controller
 *
 * author 暗氵愧
 * HostName dell
 * Date 2019/1/3 19:19
 */
@RestController
public class CartController {

    @Autowired
    private CartService cartService;

    /**
     * 添加商品到购物车
     *
     * @param cart 购物车商品对象
     * @return
     */
    @PostMapping
    public ResponseEntity<Void> addCart(@RequestBody Cart cart){
        cartService.addCart(cart);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 查询购物车所有商品
     *
     * @return
     */
    @GetMapping("/list")
    public ResponseEntity<List<Cart>> queryCartList(){
        List<Cart> cartList = cartService.queryCartList();
        return ResponseEntity.ok(cartList);
    }

    /**
     * 修改购物车的商品数量
     *
     * @param skuId 商品id
     * @param num 商品数量
     * @return
     */
    @PutMapping
    public ResponseEntity<Void> updateCartNum(@RequestParam("id") Long skuId, @RequestParam("num") Integer num){
        cartService.updateNum(skuId, num);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 删除购物车数据
     *
     * @param skuId
     * @return
     */
    @DeleteMapping("{skuId}")
    public ResponseEntity<Void> deleteCart(@PathVariable("skuId") Long skuId){
        cartService.deleteCart(skuId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 合并购物车
     *
     * @param carts 购物车商品集合
     * @return
     */
    @PostMapping("/insert")
    public ResponseEntity<Void> insertCartList(@RequestBody List<Cart> carts){
        cartService.insertCartList(carts);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
