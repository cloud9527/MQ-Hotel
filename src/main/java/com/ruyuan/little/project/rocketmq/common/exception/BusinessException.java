package com.ruyuan.little.project.rocketmq.common.exception;

/**
 * @Author: wangjing
 * @Date 2021-09-07
 **/
public class BusinessException extends RuntimeException{
    public BusinessException(String message) {
        super(message);
    }
}
