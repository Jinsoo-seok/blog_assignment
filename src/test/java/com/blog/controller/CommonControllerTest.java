package com.blog.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


@WebMvcTest(controllers = CommonController.class)
class CommonControllerTest {

    @InjectMocks
    private CommonController commoncontroller;

    private MockHttpServletRequest servletRequest;

    @Test
    @DisplayName("[SUCCESS] CommonController HealthCheck")
    public void getHealthCheck(){
        servletRequest = new MockHttpServletRequest();
        servletRequest.setRequestURI("/HealthCheck");
        servletRequest.setMethod("GET");

        Map<String, Object> responseMap = commoncontroller.getHealthCheck(servletRequest);

        assertThat(responseMap.get("code")).isEqualTo(200);
        assertThat(responseMap.get("message")).isEqualTo("SUCCESS");
    }
}
