package com.leyou.common.vo;

import com.leyou.common.enums.ExceptionEnum;
import lombok.Data;

/**
 * author 暗氵愧
 * HostName dell
 * Date 2018/11/28 17:50
 */
@Data
public class ExceptionResult {

    private int status;
    private String message;
    private Long timestamp; //时间挫

    public ExceptionResult(ExceptionEnum em){
        this.status = em.getCode();
        this.message = em.getMsg();
        this.timestamp = System.currentTimeMillis();
    }
}
