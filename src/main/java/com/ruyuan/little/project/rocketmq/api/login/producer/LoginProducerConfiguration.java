package com.ruyuan.little.project.rocketmq.api.login.producer;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: wangjing
 * @Date 2021-09-07
 **/
@Configuration
public class LoginProducerConfiguration {

    @Value("${rocketmq.namesrv.address}")
    private String namesrvAddress;

    @Value("${rocketmq.login.producer.group}")
    private String loginProducerGroup;

    @Bean(value = "loginMqProducer")
    public DefaultMQProducer loginMqProducer() throws MQClientException {
        DefaultMQProducer producer = new DefaultMQProducer(loginProducerGroup);
        producer.setNamesrvAddr(namesrvAddress);
        producer.start();
        return producer;
    }

}
