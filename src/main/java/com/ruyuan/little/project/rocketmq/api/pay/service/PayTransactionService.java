package com.ruyuan.little.project.rocketmq.api.pay.service;

import com.ruyuan.little.project.rocketmq.api.pay.dto.PayTransaction;

/**
 * @Author: wangjing
 * @Date 2021-09-10
 **/
public interface PayTransactionService {

    /**
     * 保存支付流水记录
     *
     * @param payTransaction 支付流水
     * @param phoneNumber    手机号
     * @return 记录流水结果
     */
    Boolean save(PayTransaction payTransaction, String phoneNumber);
}
