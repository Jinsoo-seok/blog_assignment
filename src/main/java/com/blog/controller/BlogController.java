package com.blog.controller;

import com.blog.service.BlogService;
import com.blog.util.ResponseCode;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static com.blog.util.ParameterUtils.pageOption;

@RestController
@RequiredArgsConstructor
public class BlogController {

    private static final Logger logger = LoggerFactory.getLogger(BlogController.class);

    final BlogService blogService;

    /**
     * /search >> searchOfKeyword
     * @param request (HttpServletRequest Info)
     * @param param (keyword, sort, pageNum, pageCount, searchEngine)
     * @return responseMap
     */
    @GetMapping("/search")
    public Map<String, Object> getSearch(HttpServletRequest request, @RequestParam Map<String, Object> param) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "["+ request.getMethod() + "] [" + request.getRequestURI() + "]";
        logger.info("{} [START] [{}]", apiInfo, startTime);

        Map<String, Object> responseMap = new HashMap<>();

        try {
            if(param.containsKey("keyword")){
                param.putAll(pageOption(param));
                responseMap = blogService.searchOfKeyword(param);
            }
            else{
                throw new Exception(ResponseCode.NO_REQUIRED_PARAM.getMessage() + ": (keyword)");
            }
        }
        catch (Exception exception) {
            logger.error("[Exception][getSearch] - {}", exception.getMessage());
            responseMap.put("code", ResponseCode.FAIL.getCode());
            responseMap.put("message", ResponseCode.FAIL.getMessage());
            responseMap.put("exceptionMessage", exception.getMessage());
        }

        long endTime = System.currentTimeMillis();
        long procTime = endTime-startTime;
        logger.info("{} [END] [{}] - {}", apiInfo, procTime, responseMap.get("code"));

        return responseMap;
    }

    /**
     * /searchPopular >> searchPopularList
     * @param request (HttpServletRequest Info)
     * @return responseMap
     */
    @GetMapping("/searchPopular")
    public Map<String, Object> getSearchPopular(HttpServletRequest request) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "["+ request.getMethod() + "] [" + request.getRequestURI() + "]";
        logger.info("{} [START] [{}]", apiInfo, startTime);

        Map<String, Object> responseMap = new HashMap<>();

        try {
            responseMap = blogService.searchPopularList();
        }
        catch (Exception exception) {
            logger.error("[Exception][getSearchPopular] - {}", exception.getMessage());
            responseMap.put("code", ResponseCode.FAIL.getCode());
            responseMap.put("message", ResponseCode.FAIL.getMessage());
            responseMap.put("exceptionMessage", exception.getMessage());
        }

        long endTime = System.currentTimeMillis();
        long procTime = endTime-startTime;
        logger.info("{} [END] [{}] - {}", apiInfo, procTime, responseMap.get("code"));

        return responseMap;
    }
}
