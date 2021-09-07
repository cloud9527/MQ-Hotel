package com.ruyuan.little.project.rocketmq.api.coupon.listener;

import com.alibaba.fastjson.JSON;
import com.ruyuan.little.project.redis.api.RedisApi;
import com.ruyuan.little.project.rocketmq.api.coupon.dto.FirstLoginMessageDTO;
import com.ruyuan.little.project.rocketmq.api.coupon.service.CouponService;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @Author: wangjing
 * @Date 2021-09-07
 **/
@Component
public class FirstLoginMessageListener implements MessageListenerConcurrently {

    private static final Logger LOGGER = LoggerFactory.getLogger(FirstLoginMessageListener.class);

    /**
     * redis dubbo服务
     */
    @Reference(version = "1.0.0",
            interfaceClass = RedisApi.class,
            cluster = "failfast")
    private RedisApi redisApi;

    /**
     * 优惠券服务service组件
     */
    @Autowired
    private CouponService couponService;

    /**
     * 第一次登陆下发的优惠券id
     */
    @Value("${first.login.couponId}")
    private Integer firstLoginCouponId;

    /**
     * 第一次登陆优惠券有效天数
     */
    @Value("${first.login.coupon.day}")
    private Integer firstLoginCouponDay;


    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        for (MessageExt msg : list) {
            String body = new String(msg.getBody(), StandardCharsets.UTF_8);
            try {
                LOGGER.info("received login success message:{}", body);
                FirstLoginMessageDTO firstLoginMessageDTO = JSON.parseObject(body, FirstLoginMessageDTO.class);
                // 用户id
                Integer userId = firstLoginMessageDTO.getUserId();
                // 手机号
                String phoneNumber = firstLoginMessageDTO.getPhoneNumber();
                // 分发权益
                couponService.distributeCoupon(firstLoginMessageDTO.getBeid(),
                        firstLoginMessageDTO.getUserId(),
                        firstLoginCouponId,
                        firstLoginCouponDay,
                        0,
                        phoneNumber);
                LOGGER.info("distribute userId:{} first login coupon end", userId);
            } catch (Exception e) {
                // 消费失败
                LOGGER.info("received login success message:{}, consumer fail", body);
                // Failure consumption,later try to consume 消费失败，以后尝试消费
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }

        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }
}
