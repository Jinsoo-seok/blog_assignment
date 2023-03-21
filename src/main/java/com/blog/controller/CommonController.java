package com.blog.controller;

import com.blog.util.ResponseCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
public class CommonController {

    private static final Logger logger = LoggerFactory.getLogger(CommonController.class);

    @GetMapping("/HealthCheck")
    public Map<String, Object> getHealthCheck(HttpServletRequest request) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "["+ request.getRequestURI() + "] [" + request.getMethod() + "]";
        logger.info("{} [START] [{}]", apiInfo, startTime);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("code", ResponseCode.SUCCESS.getCode());
        responseMap.put("message", ResponseCode.SUCCESS.getMessage());
        logger.info("HealthCheck 200 OK");

        long endTime = System.currentTimeMillis();
        long procTime = endTime-startTime;
        logger.info("{} [END] [{}] - {}", apiInfo, procTime, responseMap.get("code"));

        return responseMap;
    }
}
