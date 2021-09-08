package com.ruyuan.little.project.rocketmq.admin.producer;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: wangjing
 * @Date 2021-09-08
 **/
@Configuration
public class HotelRoomProducerConfiguration {

    @Value("${rocketmq.namesrv.address}")
    private String namesrvAddress;

    @Value("${rocketmq.hotelRoom.producer.group}")
    private String hotelRoomProducerGroup;

    @Bean(value = "hotelRoomMqProducer")
    public DefaultMQProducer hotelRoomProducer() throws MQClientException {
        DefaultMQProducer producer = new DefaultMQProducer(hotelRoomProducerGroup);
        producer.setNamesrvAddr(namesrvAddress);
        producer.start();
        return producer;

    }

}
