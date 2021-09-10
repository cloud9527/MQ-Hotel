package com.ruyuan.little.project.rocketmq.api.order.listener;

import com.alibaba.fastjson.JSON;
import com.ruyuan.little.project.rocketmq.api.order.dto.OrderInfoDTO;
import com.ruyuan.little.project.rocketmq.api.order.enums.OrderStatusEnum;
import com.ruyuan.little.project.rocketmq.api.order.serivce.OrderEventInformManager;
import com.ruyuan.little.project.rocketmq.api.order.serivce.OrderService;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @Author: wangjing
 * @Date 2021-09-10
 **/
@Component
public class FinishedOrderTransactionListener implements TransactionListener {
    /**
     * 日志组件
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(FinishedOrderTransactionListener.class);

    /**
     * 订单service组件
     */
    @Autowired
    private OrderService orderService;

    /**
     * 订单事件消息通知管理组件
     */
    @Autowired
    private OrderEventInformManager orderEventInformManager;

    /**
     * 执行本地事务
     * 这个方法在half消息发送成功之后供MQ回调。方法中会对订单的状态进行修改，并且发送消息通知。
     * @param msg
     * @param arg
     * @return
     */
    @Override
    public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        String body = new String(msg.getBody(), StandardCharsets.UTF_8);
        OrderInfoDTO orderInfoDTO = JSON.parseObject(body, OrderInfoDTO.class);
        String orderNo = orderInfoDTO.getOrderNo();
        String phoneNumber = orderInfoDTO.getPhoneNumber();
        LOGGER.info("callback execute finished order local transaction orderNo:{}", orderNo);

        try {
            // 修改订单的状态
            orderService.updateOrderStatus(orderNo, OrderStatusEnum.FINISHED, phoneNumber);

            // 发送确认通知
            orderEventInformManager.informOrderFinishEvent(orderInfoDTO);

            // 成功 提交prepare消息
            LOGGER.info("finished order local transaction execute success commit orderNo:{}", orderNo);
            return LocalTransactionState.COMMIT_MESSAGE;
        } catch (Exception e) {
            // 执行本地事务失败 回滚prepare消息
            LOGGER.info("finished order local transaction execute fail rollback orderNo:{}", orderNo);
            return LocalTransactionState.ROLLBACK_MESSAGE;
        }
    }

    /**
     * 检查本地事务  如果由于各种原因导致mq没收到commit或者rollback消息回调检查本地事务结果
     *它是在MQ没有收到commit或者rollback消息时回调订单服务的方法。它会判断订单服务本地事务的执行情况，如果执行成功会发送commit消息，否则会发送rollback消息。
     * @param msg
     * @return
     */
    @Override
    public LocalTransactionState checkLocalTransaction(MessageExt msg) {
        String body = new String(msg.getBody(), StandardCharsets.UTF_8);
        OrderInfoDTO orderInfoDTO = JSON.parseObject(body, OrderInfoDTO.class);
        String orderNo = orderInfoDTO.getOrderNo();
        String phoneNumber = orderInfoDTO.getPhoneNumber();
        LOGGER.info("callback check finished order local transaction status orderNo:{}", orderNo);
        try {
            Integer orderStatus = orderService.getOrderStatus(orderNo, phoneNumber);
            if (Objects.equals(orderStatus, OrderStatusEnum.FINISHED.getStatus())) {
                LOGGER.info("finished order local transaction check result success commit orderNo:{}", orderNo);
                return LocalTransactionState.COMMIT_MESSAGE;
            } else {
                LOGGER.info("finished order local transaction check result fail rollback orderNo:{}", orderNo);
                return LocalTransactionState.ROLLBACK_MESSAGE;
            }

        } catch (Exception e) {
            // 查询订单状态失败
            LOGGER.info("finished order local transaction check result fail rollback orderNo:{}", orderNo);
            return LocalTransactionState.ROLLBACK_MESSAGE;
        }
    }
}
