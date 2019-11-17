package com.changgou.order.listener;

import com.alibaba.fastjson.JSON;
import com.changgou.order.config.RabbitMQConfig;
import com.changgou.order.service.OrderService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component

public class OrderPayListener {

    @Autowired
    private OrderService orderService;

    @RabbitListener(queues = RabbitMQConfig.ORDER_PAY)
    public void updatePayStatus(String message){
        System.out.println("监听类接收到消息"+message);
        Map map = JSON.parseObject(message,Map.class);

        orderService.updatePayStatus((String)map.get("orderId"),(String)map.get("transactionId"));

    }
}
