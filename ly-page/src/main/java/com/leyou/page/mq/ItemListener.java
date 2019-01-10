package com.leyou.page.mq;

import com.leyou.page.service.PageService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 监听商品消息生成商品详情页
 *
 * author 暗氵愧
 * HostName dell
 * Date 2018/12/23 12:52
 */
@Component
public class ItemListener {

    @Autowired
    private PageService pageService;

    /**
     * 监听商品新增/修改消息来生成/修改商品静态页
     *
     * @param spuId
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "page.item.insert.queue", durable = "true"),
            exchange = @Exchange(name = "ly.item.exchange", type = ExchangeTypes.TOPIC),
            key = {"item.insert", "item.update"}
    ))
    public void createOrUpdateHtml(Long spuId){
        System.out.println(spuId);
        if(spuId == null){
            return;
        }
        // 处理消息，生成/修改商品静态页
        pageService.createHtml(spuId);
    }

    /**
     * 监听删除商品消息来删除商品静态页
     *
     * @param spuId
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "page.item.delete.queue", durable = "true"),
            exchange = @Exchange(name = "ly.item.exchange", type = ExchangeTypes.TOPIC),
            key = "item.delete"
    ))
    public void deleteHtml(Long spuId){
        if(spuId == null){
            return;
        }
        pageService.deleteHtml(spuId);
    }
}
