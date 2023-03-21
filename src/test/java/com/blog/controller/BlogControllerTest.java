package com.blog.controller;

import com.blog.repository.BlogRepository;
import com.blog.service.BlogService;
import com.blog.util.ResponseCode;
import com.blog.vo.BlogVo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.blog.util.ParameterUtils.pageOption;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@AutoConfigureMockMvc
//@ExtendWith(MockitoExtension.class)
@DisplayName("BlogController")
@WebMvcTest(controllers = BlogController.class)
class BlogControllerTest {

    @InjectMocks
    private BlogController blogController;

    @MockBean
    private BlogService blogService;

    @MockBean
    private BlogRepository blogRepository;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("[SUCCESS] getSearch")
    public void getSearchSuccess () {
        MockHttpServletRequest servletRequest;
        servletRequest = new MockHttpServletRequest();
        servletRequest.setRequestURI("/search");
        servletRequest.setMethod("GET");

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("code", ResponseCode.SUCCESS.getCode());
        responseMap.put("message", ResponseCode.SUCCESS.getMessage());
        Map<String, Object> responseDataMap = new HashMap<>();
        responseDataMap.put("start", 1);
        responseDataMap.put("display", 10);
        responseDataMap.put("searchEngine", "searchKakao");
        responseMap.put("data", responseDataMap);

        Map<String, Object> param = new HashMap<>();
        param.put("keyword", "검색");
        param.put("sort", "accuracy");
        param.put("pageNum", "1");
        param.put("pageCount", "10");
        param.put("searchEngine", "kakao");


        // given
        try {
            if (param.containsKey("keyword")) {
                param.putAll(pageOption(param));
                given(blogService.searchOfKeyword(param)).willReturn(responseMap);

            } else {
                throw new Exception(ResponseCode.NO_REQUIRED_PARAM.getMessage() + ": (keyword)");
            }
        } catch (Exception exception) {
            responseMap.put("code", ResponseCode.FAIL.getCode());
            responseMap.put("message", ResponseCode.FAIL.getMessage());
            responseMap.put("exceptionMessage", exception.getMessage());
        }

        try {
            mockMvc.perform(get("/search?keyword=검색&sort=accuracy&pageNum=1&pageCount=10&searchEngine=kakao"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("code").value(ResponseCode.SUCCESS.getCode()))
                    .andExpect(jsonPath("message").value(ResponseCode.SUCCESS.getMessage()))
                    .andExpect(jsonPath("data").isNotEmpty())
//                    .andDo(print())
                    .andReturn();

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Test
    @DisplayName("[FAIL] getSearch")
    public void getSearchFail () {
        MockHttpServletRequest servletRequest;
        servletRequest = new MockHttpServletRequest();
        servletRequest.setRequestURI("/search");
        servletRequest.setMethod("GET");

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("code", ResponseCode.SUCCESS.getCode());
        responseMap.put("message", ResponseCode.SUCCESS.getMessage());
        Map<String, Object> responseDataMap = new HashMap<>();
        responseDataMap.put("start", 1);
        responseDataMap.put("display", 10);
        responseDataMap.put("searchEngine", "searchKakao");
        responseMap.put("data", responseDataMap);

        Map<String, Object> param = new HashMap<>();
        param.put("keyword", "검색");
        param.put("sort", "accuracy");
        param.put("pageNum", "1");
        param.put("pageCount", "10");
        param.put("searchEngine", "kakao");


        // given
        try {
            if (param.containsKey("keyword")) {
                param.putAll(pageOption(param));
                given(blogService.searchOfKeyword(param)).willReturn(responseMap);

            } else {
                throw new Exception(ResponseCode.NO_REQUIRED_PARAM.getMessage() + ": (keyword)");
            }
        } catch (Exception exception) {
            responseMap.put("code", ResponseCode.FAIL.getCode());
            responseMap.put("message", ResponseCode.FAIL.getMessage());
            responseMap.put("exceptionMessage", exception.getMessage());
        }

        try {
            mockMvc.perform(get("/search?&sort=accuracy&pageNum=1&pageCount=10&searchEngine=kakao"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("code").value(ResponseCode.FAIL.getCode()))
                    .andExpect(jsonPath("message").value(ResponseCode.FAIL.getMessage()))
                    .andReturn();

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }


    @Test
    @DisplayName("[SUCCESS] getSearchPopular")
    public void getSearchPopularSuccess() throws Exception {
        MockHttpServletRequest servletRequest;
        servletRequest = new MockHttpServletRequest();
        servletRequest.setRequestURI("/searchPopular");
        servletRequest.setMethod("GET");

        Map<String, Object> responseMap = new HashMap<>();

        BlogVo blogVo = new BlogVo();
        blogVo.setBlogName("지적 대화");
        blogVo.setBlogSearchCount(1);

        List<BlogVo> responseList = new ArrayList<>();
        responseList.add(blogVo);

        responseMap.put("data", responseList);
        responseMap.put("code", ResponseCode.SUCCESS.getCode());
        responseMap.put("message", ResponseCode.SUCCESS.getMessage());

        Pageable optionLimit = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "blogSearchCount"));
        given(blogRepository.findTop10By(optionLimit)).willReturn(responseList);

        given(blogService.searchPopularList()).willReturn(responseMap);

        try {
            mockMvc.perform(get("/searchPopular"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("code").value(ResponseCode.SUCCESS.getCode()))
                    .andExpect(jsonPath("message").value(ResponseCode.SUCCESS.getMessage()))
                    .andExpect(jsonPath("data").isNotEmpty())
                    .andReturn();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("[FAIL] getSearchPopular")
    public void getSearchPopularFail() throws Exception {
        MockHttpServletRequest servletRequest;
        servletRequest = new MockHttpServletRequest();
        servletRequest.setRequestURI("/searchPopular");
        servletRequest.setMethod("GET");

        Map<String, Object> responseMap = new HashMap<>();


//        // given
//        responseMap.put("data", null);
//        responseMap.put("code", ResponseCode.FAIL.getCode());
//        responseMap.put("message", ResponseCode.FAIL.getMessage());
//
//        Pageable optionLimit = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "blogSearchCount"));
//        given(blogRepository.findTop10By(optionLimit)).willReturn(null);
//        given(blogService.searchPopularList()).willReturn(responseMap);
//
//
//        // when, then
//        try {
//            mockMvc.perform(get("/searchPopular"))
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("code").value(ResponseCode.FAIL.getCode()))
//                    .andExpect(jsonPath("message").value(ResponseCode.FAIL.getMessage()))
//                    .andExpect(jsonPath("data").isEmpty())
//                    //                    .andDo(print())
//                    .andReturn();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        // when, then
        doThrow(Exception.class).when(blogService).searchPopularList();
        assertThrows(Exception.class, () -> blogService.searchPopularList());
    }
}