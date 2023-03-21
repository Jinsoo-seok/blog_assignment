package com.blog.service;

import com.blog.repository.BlogRepository;
import com.blog.util.ResponseCode;
import com.blog.vo.BlogVo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class BlogServiceImpl implements BlogService {

    private static Logger logger = LoggerFactory.getLogger(BlogServiceImpl.class);

    final BlogRepository blogRepository;

    @Value("${kakao.api.url}")
    private String kakaoApiUrl;
    @Value("${kakao.api.key}")
    private String kakaoApiKey;
    @Value("${naver.api.url}")

    private String naverApiUrl;
    @Value("${naver.client.id}")
    private String naverClientId;
    @Value("${naver.client.secret}")
    private String naverClientSecret;


    private static Integer KAKAO_LIMIT_PAGE_NUM = 50;
    private static Integer KAKAO_LIMIT_PAGE_COUNT = 50;
    private static String KAKAO_SORT_FIRST = "accuracy";
    private static String KAKAO_SORT_SECOND = "recency";

    private static Integer NAVER_LIMIT_PAGE_NUM = 100;
    private static Integer NAVER_LIMIT_PAGE_COUNT = 1000;
    private static String NAVER_SORT_FIRST = "sim";
    private static String NAVER_SORT_SECOND = "date";



    @Override
    public Map<String, Object> searchOfKeyword (Map<String, Object> param) throws Exception {

        Map<String, Object> resultMap = new HashMap<>();
        Map<String, Object> restResponseSearch = null;

        String[] searchEngineList = {"searchKaKao", "searchNaver"};
        String searchKeyword = (String)param.get("keyword");
        boolean searchEngineFailYN = false;

        // Default : kakao
        String searchEngine = searchEngineCheck((String) param.get("searchEngine"), searchEngineList);
        // Setting : searchParam
        Map<String, Object> searchParam =  searchEngineSetting(param, searchEngine, searchEngineList);
        // Search

        try {
            restResponseSearch = restTemplate(searchParam, searchEngine);
        }
        catch(Exception exception){
            // searchAPI : Fail -> Other searchAPI Call (once)
            if(!searchEngineFailYN) {
                searchEngineFailYN = true;
                String tempSearchEngine = null;

                for (String engine : searchEngineList) {
                    if (!engine.equals(searchEngine)) {
                        tempSearchEngine = engine;
                        break;
                    }
                }
                Map<String, Object> tempSearchParam =  searchEngineSetting(param, tempSearchEngine, searchEngineList);
                restResponseSearch = restTemplate(tempSearchParam, tempSearchEngine);
                if(restResponseSearch.size()>0){
                    searchEngine = tempSearchEngine;
                }
            }
        }

        // Save : Before Exist Check
        BlogVo blogVo = new BlogVo();
        BlogVo blogVoYN = blogRepository.findByName(searchKeyword);

        blogVo.setBlogName(searchKeyword);
        if(blogVoYN != null){
            Integer tempCount = blogVoYN.getBlogSearchCount() + 1;
            blogVo.setBlogSearchCount(tempCount);
        }
        else{
            blogVo.setBlogSearchCount(1);
        }
        
        // TODO : Select 없이 Insert or Update
        // TODO : Save 정상/비정상 판별
        blogRepository.save(blogVo);


        if (restResponseSearch.size() > 0) {
            restResponseSearch.put("searchEngineFailYN", searchEngineFailYN);
            restResponseSearch.put("searchEngine", searchEngine);
            resultMap.put("data", restResponseSearch);
            resultMap.put("code", ResponseCode.SUCCESS.getCode());
            resultMap.put("message", ResponseCode.SUCCESS.getMessage());
        } else {
            logger.info("[searchOfKeyword] - Not Exist Data");

            resultMap.put("code", ResponseCode.NO_CONTENT.getCode());
            resultMap.put("message", ResponseCode.NO_CONTENT.getMessage());
        }
        return resultMap;
    }

    @Override
    public Map<String, Object> searchPopularList () {
        Map<String, Object> resultMap = new ConcurrentHashMap<>();
        List<BlogVo> blogVoList;

        Pageable optionLimit = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "blogSearchCount"));
        blogVoList = blogRepository.findTop10By(optionLimit);

        if (blogVoList.size() > 0) {
            resultMap.put("data", blogVoList);
            resultMap.put("code", ResponseCode.SUCCESS.getCode());
            resultMap.put("message", ResponseCode.SUCCESS.getMessage());
        } else {
            logger.info("[searchOfKeyword] - Not Exist Data");

            resultMap.put("code", ResponseCode.NO_CONTENT.getCode());
            resultMap.put("message", ResponseCode.NO_CONTENT.getMessage());
        }
        return resultMap;
    }

    public String searchEngineCheck(String searchEngine, String[] searchEngineList){
        String resultSearchEngine = null;
        String Kakao = "kakao";
        String Naver = "naver";

        if(searchEngine.equals(Kakao) || searchEngine.equals(StringUtils.capitalize(Kakao)) || searchEngine.equals(Kakao.toUpperCase())){
            resultSearchEngine = searchEngineList[0];
        }
        else if(searchEngine.equals(Naver) || searchEngine.equals(StringUtils.capitalize(Naver)) || searchEngine.equals(Naver.toUpperCase())){
            resultSearchEngine = searchEngineList[1];
        }
        else{
            resultSearchEngine = searchEngineList[0];
        }

        return resultSearchEngine;
    }

    public Map<String, Object> searchEngineSetting(Map<String, Object> param, String searchEngine, String[] searchEngineList){
        Map<String, Object> resultMap = new HashMap<>();

        // [0] : pageNum, [1] : pageCount
        Integer[] limitKakao = {KAKAO_LIMIT_PAGE_NUM, KAKAO_LIMIT_PAGE_COUNT};
        Integer[] limitNaver = {NAVER_LIMIT_PAGE_NUM, NAVER_LIMIT_PAGE_COUNT};
        // default : [0]
        String[] sortKakao = {KAKAO_SORT_FIRST, KAKAO_SORT_SECOND};
        String[] sortNaver = {NAVER_SORT_FIRST, NAVER_SORT_SECOND};

        if(searchEngine.equals(searchEngineList[0])){
            resultMap = pageOptionSetting(param, limitKakao, sortKakao);
        }
        else if(searchEngine.equals(searchEngineList[1])){
            resultMap = pageOptionSetting(param, limitNaver, sortNaver);
        }
        else{
            resultMap = pageOptionSetting(param, limitKakao, sortKakao);
        }

        return resultMap;
    }

    public Map<String, Object> pageOptionSetting(Map<String, Object> param, Integer[] limitPaging, String[] sortType) {
        Map<String, Object> resultMap = new HashMap<>();

        Integer pageCount = ((Integer)param.get("pageCount") > limitPaging[1]) ? limitPaging[1] : (Integer)param.get("pageCount");
        Integer pageNum = ((Integer)param.get("pageNum") > limitPaging[0]) ? limitPaging[0] : (Integer)param.get("pageNum");
        String tempPageSort = (String) param.get("pageSort");

        if(tempPageSort.equals(sortType[0]) || tempPageSort.equals(StringUtils.capitalize(sortType[0])) || tempPageSort.equals(sortType[0].toUpperCase())){
            tempPageSort = sortType[0];
        }
        else if(tempPageSort.equals(sortType[1]) || tempPageSort.equals(StringUtils.capitalize(sortType[1])) || tempPageSort.equals(sortType[1].toUpperCase())){
            tempPageSort = sortType[1];
        }
        else{
            tempPageSort = sortType[0];
        }

        resultMap.put("keyword", param.get("keyword"));
        resultMap.put("pageSort", tempPageSort);
        resultMap.put("pageCount", pageCount);
        resultMap.put("pageNum", pageNum);

        return resultMap;
    }

    public Map<String, Object> restTemplate (Map<String, Object> param, String type) throws Exception {

        ResponseEntity<Map> rtResponse = null;
        Map<String, Object> resultResponse = null;
        HttpHeaders headers = new HttpHeaders();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        RestTemplate rt = new RestTemplate();
        HttpEntity<MultiValueMap<String, String>> rtRequest;
        String url = "";

        try {
            if (type.equals("searchKaKao")) {
                headers.set("Authorization", "KakaoAK " + kakaoApiKey);
                rtRequest = new HttpEntity<>(params, headers);

                url = kakaoApiUrl + "?"
                        + "query=" + param.get("keyword") + "&sort=" + param.get("pageSort")
                        + "&page=" + param.get("pageNum") + "&size=" + param.get("pageCount");

                rtResponse = rt.exchange(
                        url,
                        HttpMethod.GET,
                        rtRequest,
                        Map.class
                );
            }
            else if (type.equals("searchNaver")) {
                headers.set("X-Naver-Client-Id", naverClientId);
                headers.set("X-Naver-Client-Secret", naverClientSecret);
                rtRequest = new HttpEntity<>(params, headers);

                url = naverApiUrl + "?"
                        + "query=" + param.get("keyword") + "&sort=" + param.get("pageSort")
                        + "&start=" + param.get("pageNum") + "&display=" + param.get("pageCount");

                rtResponse = rt.exchange(
                        url,
                        HttpMethod.GET,
                        rtRequest,
                        Map.class
                );
            }
        }
        catch(Exception exception){
            throw new Exception(exception.getMessage());
        }

        if (rtResponse.getStatusCode().value() != 200) {
            throw new Exception("[ERROR][searchOfKeyword][RestTemplate]");
        }
        else{
            resultResponse = (Map<String, Object>) rtResponse.getBody();
        }

        return resultResponse;
    }
}
