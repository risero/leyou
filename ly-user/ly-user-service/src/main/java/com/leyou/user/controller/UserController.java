package com.leyou.user.controller;

import com.leyou.user.pojo.User;
import com.leyou.user.service.UserService;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.stream.Collectors;

/**
 * 用户控制层
 *
 * author 暗氵愧
 * HostName dell
 * Date 2018/12/27 20:22
 */
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    /**
     *  校验数据
     *
     * @param data 要校验的数据
     * @param type 要校验的数据类型：1，用户名；2，手机；
     * @return true:可用，false不可用
     */
    @GetMapping("/check/{data}/{type}")
    public ResponseEntity<Boolean> check(
            @PathVariable(value = "data", required = true) String data,
            @PathVariable(name="type", required = true) Integer type){
        Boolean isEnable = userService.checkData(data, type);
        return ResponseEntity.ok(isEnable);
    }

    /**
     * 发送验证码
     *
     * @param phone 手机号
     * @return
     */
    @PostMapping("/code")
    public ResponseEntity<Void> sendCode(@RequestParam(value = "phone", required = true) String phone){
        userService.sendCode(phone);
        // 返回状态码204，build()无返回
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 用户注册
     *
     * @param user
     * @param code 验证码
     * @return
     */
    @PostMapping("/register")
    public ResponseEntity<Void> registry(@Valid User user, BindingResult result, @RequestParam(value = "code", required = true) String code){
        // 这里可以不返回错误信息，因为前端返回的错误信息
//        if(result.hasFieldErrors()){
//            throw new RuntimeException(result.getFieldErrors().stream()
//                    .map(e -> e.getDefaultMessage()).collect(Collectors.joining("|")));
//        }
        userService.register(user, code);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 用户登录
     *
     * @param username 账号
     * @param password 密码
     * @return
     */
    @GetMapping("/query")
    public ResponseEntity<User> queryUserByUsernameAndPassword(
            @RequestParam(required = true, value = "username") String username,
            @RequestParam(required = true, value = "password") String password){
        User user = userService.queryUserByUsernameAndPassword(username, password);
        return ResponseEntity.ok(user);
    }
}
