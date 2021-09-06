package com.ruyuan.little.project.rocketmq.api;

import com.ruyuan.little.project.common.dto.CommonResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: wangjing
 * @Date 2021-09-06
 **/
@RestController
public class HealthController {
    @RequestMapping(value = "/")
    public CommonResponse health() {
        return CommonResponse.success();
    }
}
