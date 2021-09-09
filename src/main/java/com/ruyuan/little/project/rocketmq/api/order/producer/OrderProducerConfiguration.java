package com.ruyuan.little.project.rocketmq.api.order.producer;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: wangjing
 * @Date 2021-09-09
 **/
@Configuration
public class OrderProducerConfiguration {
    @Value("${rocketmq.namesrv.address}")
    private String namesrvAddress;

    @Value("${rocketmq.order.producer.group}")
    private String orderProducerGroup;

    @Bean(value="orderMqProducer")
    public DefaultMQProducer orderMqProducer() throws MQClientException {
        DefaultMQProducer producer = new DefaultMQProducer(orderProducerGroup);
        producer.setNamesrvAddr(namesrvAddress);
        producer.start();
        return producer;
    }
}
