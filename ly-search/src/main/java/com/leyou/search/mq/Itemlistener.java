package com.leyou.search.mq;

import com.leyou.search.service.SearchService;
import com.rabbitmq.client.BuiltinExchangeType;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 监听商品消息
 *
 * author 暗氵愧
 * HostName dell
 * Date 2018/12/22 21:13
 */
@Component
public class Itemlistener {

    @Autowired
    private SearchService searchService;

    /**
     * 监听商品新增或修改商品索引消息
     * 消息发送失败会抛出异常会重试
     *
     * @param spuId
     */
    @RabbitListener(
            bindings = @QueueBinding(
                    // 商品新增队列
                    value = @Queue(name = "search.item.insert.queue", durable = "true"),
                    // 发布订阅 Topic通配符方式
                    exchange = @Exchange(name = "ly.item.exchange", type = ExchangeTypes.TOPIC),
                    key = {"item.update", "item.insert"}
            ))
    public void listenerInsertOrUpdateIndex(Long spuId){
        if(spuId == null){
            return;
        }
        // 处理消息，对索引库进行新增或修改
        searchService.createOrUpdate(spuId);
    }

    /**
     * 监听商品删除索引消息
     *
     * @param spuId
     */
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(name = "search.item.delete.queue", durable = "true"),
                    exchange = @Exchange(name = "ly.item.exchange", type = ExchangeTypes.TOPIC),
                    key = "item.delete"
            ))
    public void listenerDelete(Long spuId){
        if(spuId == null){
            return;
        }
        searchService.deleteIndex(spuId);
    }
}
