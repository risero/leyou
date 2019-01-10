package com.leyou.common.advice;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.vo.ExceptionResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * 全局异常处理
 *
 * author 暗氵愧
 * HostName dell
 * Date 2018/11/28 17:05
 */
@ControllerAdvice
public class CommonExceptionHandler {

    @ExceptionHandler(LyException.class) // 以后捕获自定义的异常就可以了
    public ResponseEntity<ExceptionResult> handleException(LyException e){
        // 通过异常类获取枚举类
        ExceptionEnum em = e.getExceptionEnum();
        // 通过异常枚举类再获取自定义的异常信息
        return ResponseEntity.status(em.getCode()).body(new ExceptionResult(em));
    }
}
