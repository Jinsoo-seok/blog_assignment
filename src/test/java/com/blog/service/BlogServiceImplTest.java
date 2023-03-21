package com.blog.service;

import com.blog.repository.BlogRepository;
import com.blog.util.ResponseCode;
import com.blog.vo.BlogVo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

//@SpringBootTest
@ExtendWith(MockitoExtension.class)
class BlogServiceImplTest {

    @InjectMocks
    BlogServiceImpl blogService;

    @Autowired
    private TestRestTemplate restTemplate;

    @Mock
    BlogRepository blogRepository;

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

    @Test
    @DisplayName("[SUCCESS] searchOfKeyword")
    void searchOfKeyword() throws Exception {

        Map<String, Object> param = new HashMap<>();
        param.put("keyword", "지적");
        param.put("pageNum", 1);
        param.put("pageCount", 10);
        param.put("pageSort", "accuracy");
        param.put("searchEngine", "kakao");

        Map<String, Object> resultMap = new HashMap<>();
        Map<String, Object> restResponseSearch = null;

        String[] searchEngineList = {"searchKaKao", "searchNaver"};
        String searchKeyword = (String)param.get("keyword");
        boolean searchEngineFailYN = false;
        String searchEngine = "searchKakao";

        Map<String, Object> searchParam = new HashMap<>();
        searchParam.put("keyword", "지적");
        searchParam.put("pageNum", 1);
        searchParam.put("pageCount", 10);
        searchParam.put("pageSort", "accuracy");
        searchParam.put("searchEngine", "searchKakao");

        ArrayList<Map<String, Object>> documentsMapList = new ArrayList<>();
        Map<String, Object> documentsMap = new HashMap<>();
        documentsMap.put("contents", "test");
        documentsMap.put("datetime", "datetime");
        documentsMap.put("title", "title");
        documentsMap.put("url", "url");

        Map<String, Object> meta = new HashMap<>();
        meta.put("is_end", false);
        meta.put("pageable_count", 1);
        meta.put("total_count", 1);

        Map<String, Object> searchReturn = new HashMap<>();
        searchReturn.put("documents", documentsMapList);
        searchReturn.put("meta", meta);
        searchReturn.put("searchEngineFailYN", searchEngineFailYN);
        searchReturn.put("searchEngine", searchEngine);

        Map<String, Object> responseMap = new ConcurrentHashMap<>();
        responseMap.put("data", searchReturn);
        responseMap.put("code", ResponseCode.SUCCESS.getCode());
        responseMap.put("message", ResponseCode.SUCCESS.getMessage());

        Map<String, Object> responseMap2 = new HashMap<>();


        Integer[] limitKakao = {KAKAO_LIMIT_PAGE_NUM, KAKAO_LIMIT_PAGE_COUNT};
        Integer[] limitNaver = {NAVER_LIMIT_PAGE_NUM, NAVER_LIMIT_PAGE_COUNT};
        // default : [0]
        String[] sortKakao = {KAKAO_SORT_FIRST, KAKAO_SORT_SECOND};
        String[] sortNaver = {NAVER_SORT_FIRST, NAVER_SORT_SECOND};


//        when(blogService.searchEngineCheck((String) param.get("searchEngine"), searchEngineList)).thenReturn(searchEngine);
//        when(blogService.searchEngineSetting(param, searchEngine, searchEngineList)).thenReturn(searchParam);
//        when(blogService.restTemplate(searchParam, searchEngine)).thenReturn(searchReturn);
//        when(blogService.searchOfKeyword(param)).thenReturn(responseMap);

        given(blogService.pageOptionSetting(param, limitKakao, sortKakao)).willReturn(searchParam);
        given(blogService.searchEngineCheck((String) param.get("searchEngine"), searchEngineList)).willReturn(searchEngine);
        given(blogService.searchEngineSetting(param, searchEngine, searchEngineList)).willReturn(searchParam);
        given(blogService.restTemplate(searchParam, searchEngine)).willReturn(searchReturn);
        given(blogService.searchOfKeyword(param)).willReturn(responseMap);

//        Map<String, Object> param2 = new HashMap<>();
//        param2.put("keyword", "지적");
//        param2.put("pageNum", "1");
//        param2.put("pageCount", "10");
//        param2.put("pageSort", "accuracy");
//        param2.put("searchEngine", "kakao");
//
//        responseMap2 = blogService.searchOfKeyword(param2);
//        System.out.println("responseMap2 = " + responseMap2);
//        // then
//        assertThat(responseMap2.get("code")).isEqualTo(ResponseCode.SUCCESS.getCode());
//        assertThat(responseMap2.get("message")).isEqualTo(ResponseCode.SUCCESS.getMessage());
//        assertThat(responseMap2.get("data")).isNotNull();

    }

    @Test
    @DisplayName("searchOfKeyword Repo findByName")
    void searchOfKeywordRepoFindByName(){
        Map<String, Object> param = new HashMap<>();
        param.put("keyword", "지적");
        param.put("pageNum", 1);
        param.put("pageCount", 10);
        param.put("pageSort", "accuracy");
        param.put("searchEngine", "kakao");

        String searchKeyword = (String)param.get("keyword");

        BlogVo blogVo = blogRepository.findByName(searchKeyword);
        assertThat(blogVo).isNull();
    }

    @Test
    @DisplayName("searchOfKeyword Repo Save : Insert")
    void searchOfKeywordRepoSaveInsert(){
        Map<String, Object> param = new HashMap<>();
        param.put("keyword", "지적");
        param.put("pageNum", 1);
        param.put("pageCount", 10);
        param.put("pageSort", "accuracy");
        param.put("searchEngine", "kakao");

        String searchKeyword = (String)param.get("keyword");
        BlogVo blogVoYN = null;
        BlogVo blogVo = new BlogVo();

        blogVo.setBlogName(searchKeyword);
        if(blogVoYN != null) {
            Integer tempCount = blogVoYN.getBlogSearchCount() + 1;
            blogVo.setBlogSearchCount(tempCount);
        }
        else{
            blogVo.setBlogSearchCount(1);
        }
        blogRepository.save(blogVo);

        // when
        List<BlogVo> blogVoList = blogRepository.findAll();

        // then
        assertThat(blogVoList.get(0).getBlogName()).isEqualTo(param.get("keyword"));
        assertThat(blogVoList.get(0).getBlogSearchCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("searchOfKeyword Repo Save : Update")
    void searchOfKeywordRepoSaveUpdate(){
        Map<String, Object> param = new HashMap<>();
        param.put("keyword", "지적");

        String searchKeyword = (String)param.get("keyword");
        BlogVo blogVo = new BlogVo();
        blogVo.setBlogName(searchKeyword);
        blogVo.setBlogSearchCount(1);

        BlogVo newBlogVo = blogRepository.save(blogVo);
        System.out.println("blogVo = " + blogVo);
        System.out.println("newBlogVo = " + newBlogVo);
//
//        // when
//        List<BlogVo> blogVoList = blogRepository.findAll();
//
//        // then
//        assertThat(blogVoList.get(0).getBlogName()).isEqualTo(param.get("keyword"));
//        assertThat(blogVoList.get(0).getBlogSearchCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("searchOfKeyword Response Data")
    void searchOfKeywordResponseData(){

        Map<String, Object> resultMap = null;
        Map<String, Object> restResponseSearch = null;

        boolean searchEngineFailYN = false;
        String searchEngine = "searchKakao";

        // when
        if (restResponseSearch.size() > 0) {
            restResponseSearch.put("searchEngineFailYN", searchEngineFailYN);
            restResponseSearch.put("searchEngine", searchEngine);
            resultMap.put("data", restResponseSearch);
            resultMap.put("code", ResponseCode.SUCCESS.getCode());
            resultMap.put("message", ResponseCode.SUCCESS.getMessage());
        } else {
            resultMap.put("code", ResponseCode.NO_CONTENT.getCode());
            resultMap.put("message", ResponseCode.NO_CONTENT.getMessage());
        }


        // then
        assertThat(resultMap.get("code")).isEqualTo(ResponseCode.NO_CONTENT.getCode());
        assertThat(resultMap.get("message")).isEqualTo(ResponseCode.NO_CONTENT.getMessage());



        // given
        restResponseSearch.put("searchEngineFailYN", searchEngineFailYN);
        restResponseSearch.put("searchEngine", searchEngine);
        restResponseSearch.put("searchData", "1");

        // when
        if (restResponseSearch.size() > 0) {
            restResponseSearch.put("searchEngineFailYN", searchEngineFailYN);
            restResponseSearch.put("searchEngine", searchEngine);
            resultMap.put("data", restResponseSearch);
            resultMap.put("code", ResponseCode.SUCCESS.getCode());
            resultMap.put("message", ResponseCode.SUCCESS.getMessage());
        } else {
            resultMap.put("code", ResponseCode.NO_CONTENT.getCode());
            resultMap.put("message", ResponseCode.NO_CONTENT.getMessage());
        }

        // then
        assertThat(resultMap.get("code")).isEqualTo(ResponseCode.SUCCESS.getCode());
        assertThat(resultMap.get("message")).isEqualTo(ResponseCode.SUCCESS.getMessage());
        assertThat(resultMap.get("data")).isNotNull();

    }

    @Test
    @DisplayName("[SUCCESS] searchPopularList")
    void searchPopularListSuccess() {

        // setting
        BlogVo blogVo = new BlogVo();
        blogVo.setBlogName("지적 대화");
        blogVo.setBlogSearchCount(5);
        BlogVo blogVo2 = new BlogVo();
        blogVo2.setBlogName("지적");
        blogVo2.setBlogSearchCount(1);

        List<BlogVo> responseList = new ArrayList<>();
        responseList.add(blogVo);
        responseList.add(blogVo2);

        Map<String, Object> responseMap = new ConcurrentHashMap<>();
        responseMap.put("data", responseList);
        responseMap.put("code", ResponseCode.SUCCESS.getCode());
        responseMap.put("message", ResponseCode.SUCCESS.getMessage());


        // given
        Pageable optionLimit = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "blogSearchCount"));
        given(blogRepository.findTop10By(optionLimit)).willReturn(responseList);


        // when
        responseMap = blogService.searchPopularList();


        // then
        assertThat(responseMap.get("code")).isEqualTo(ResponseCode.SUCCESS.getCode());
        assertThat(responseMap.get("message")).isEqualTo(ResponseCode.SUCCESS.getMessage());
        assertThat(responseMap.get("data")).isNotNull();

        List<BlogVo> responseDataMap = (List<BlogVo>) responseMap.get("data");
        assertThat(responseDataMap.get(0).getBlogName()).isEqualTo("지적 대화");
        assertThat(responseDataMap.get(0).getBlogSearchCount()).isEqualTo(5);
        assertThat(responseDataMap.get(1).getBlogName()).isEqualTo("지적");
        assertThat(responseDataMap.get(1).getBlogSearchCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("[FAIL] searchPopularList")
    void searchPopularListFail() {

        // setting
        List<BlogVo> responseList = new ArrayList<>();
        Map<String, Object> responseMap = new ConcurrentHashMap<>();
        responseMap.put("code", ResponseCode.SUCCESS.getCode());
        responseMap.put("message", ResponseCode.SUCCESS.getMessage());


        // given
        Pageable optionLimit = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "blogSearchCount"));
        given(blogRepository.findTop10By(optionLimit)).willReturn(responseList);


        // when
        responseMap = blogService.searchPopularList();


        // then
        assertThat(responseMap.get("code")).isEqualTo(ResponseCode.NO_CONTENT.getCode());
        assertThat(responseMap.get("message")).isEqualTo(ResponseCode.NO_CONTENT.getMessage());
    }

    @Test
    @DisplayName("searchEngineCheck")
    void searchEngineCheck() {
        String[] searchEngineList = {"searchKaKao", "searchNaver"};
        String searchEngine = "";

        // given, when, then
        // kakao
        searchEngine = "kakao";
        assertThat(blogService.searchEngineCheck(searchEngine, searchEngineList)).isEqualTo(searchEngineList[0]);
        searchEngine = "Kakao";
        assertThat(blogService.searchEngineCheck(searchEngine, searchEngineList)).isEqualTo(searchEngineList[0]);
        searchEngine = "KAKAO";
        assertThat(blogService.searchEngineCheck(searchEngine, searchEngineList)).isEqualTo(searchEngineList[0]);

        // naver
        searchEngine = "naver";
        assertThat(blogService.searchEngineCheck(searchEngine, searchEngineList)).isEqualTo(searchEngineList[1]);
        searchEngine = "Naver";
        assertThat(blogService.searchEngineCheck(searchEngine, searchEngineList)).isEqualTo(searchEngineList[1]);
        searchEngine = "NAVER";
        assertThat(blogService.searchEngineCheck(searchEngine, searchEngineList)).isEqualTo(searchEngineList[1]);

        // Default : kakao
        searchEngine = "test";
        assertThat(blogService.searchEngineCheck(searchEngine, searchEngineList)).isEqualTo(searchEngineList[0]);
    }

    @Test
    @DisplayName("searchEngineSetting")
    void searchEngineSetting() {
        Map<String, Object> param = new HashMap<>();
        param.put("keyword", "지적");
        param.put("pageNum", 1);
        param.put("pageCount", 10);
        param.put("pageSort", "accuracy");
        param.put("searchEngine", "kakao");

        String searchEngineKakao = "searchKaKao";
        String searchEngineNaver = "searchNaver";
        String searchEngineDefault = "test";
        String[] searchEngineList = {"searchKaKao", "searchNaver"};

        Map<String, Object> resultServiceMap = new HashMap<>();
        resultServiceMap = blogService.searchEngineSetting(param, searchEngineKakao, searchEngineList);
        assertThat(resultServiceMap.get("keyword")).isEqualTo(param.get("keyword"));
        assertThat(resultServiceMap.get("pageSort")).isEqualTo(param.get("pageSort"));
        assertThat(resultServiceMap.get("pageNum")).isEqualTo(param.get("pageNum"));
        assertThat(resultServiceMap.get("pageCount")).isEqualTo(param.get("pageCount"));

        resultServiceMap = blogService.searchEngineSetting(param, searchEngineNaver, searchEngineList);
        assertThat(resultServiceMap.get("keyword")).isEqualTo(param.get("keyword"));
        assertThat(resultServiceMap.get("pageSort")).isEqualTo("sim");
        assertThat(resultServiceMap.get("pageNum")).isEqualTo(param.get("pageNum"));
        assertThat(resultServiceMap.get("pageCount")).isEqualTo(param.get("pageCount"));

        resultServiceMap = blogService.searchEngineSetting(param, searchEngineDefault, searchEngineList);
        assertThat(resultServiceMap.get("keyword")).isEqualTo(param.get("keyword"));
        assertThat(resultServiceMap.get("pageSort")).isEqualTo(param.get("pageSort"));
        assertThat(resultServiceMap.get("pageNum")).isEqualTo(param.get("pageNum"));
        assertThat(resultServiceMap.get("pageCount")).isEqualTo(param.get("pageCount"));
    }

    @Test
    @DisplayName("pageOptionSetting")
    void pageOptionSetting() {

        // [0] : pageNum, [1] : pageCount
        Integer[] limitKakao = {KAKAO_LIMIT_PAGE_NUM, KAKAO_LIMIT_PAGE_COUNT};
        Integer[] limitNaver = {NAVER_LIMIT_PAGE_NUM, NAVER_LIMIT_PAGE_COUNT};
        // default : [0]
        String[] sortKakao = {KAKAO_SORT_FIRST, KAKAO_SORT_SECOND};
        String[] sortNaver = {NAVER_SORT_FIRST, NAVER_SORT_SECOND};

        Map<String, Object> resultServiceMap = new HashMap<>();
        Map<String, Object> param = new HashMap<>();


        // kakao
        param.put("keyword", "test");
        param.put("pageSort", "accuracy");
        param.put("pageNum", 1);
        param.put("pageCount", 10);
        resultServiceMap = blogService.pageOptionSetting(param, limitKakao, sortKakao);
        assertThat(resultServiceMap.get("keyword")).isEqualTo(param.get("keyword"));
        assertThat(resultServiceMap.get("pageSort")).isEqualTo(param.get("pageSort"));
        assertThat(resultServiceMap.get("pageNum")).isEqualTo(param.get("pageNum"));
        assertThat(resultServiceMap.get("pageCount")).isEqualTo(param.get("pageCount"));

        // kakao : sort default, 페이지 limit
        param.put("keyword", "test");
        param.put("pageSort", "accuracy");
        param.put("pageNum", 51);
        param.put("pageCount", 51);
        resultServiceMap = blogService.pageOptionSetting(param, limitKakao, sortKakao);
        assertThat(resultServiceMap.get("keyword")).isEqualTo(param.get("keyword"));
        assertThat(resultServiceMap.get("pageSort")).isEqualTo(sortKakao[0]);
        assertThat(resultServiceMap.get("pageNum")).isEqualTo(limitKakao[0]);
        assertThat(resultServiceMap.get("pageCount")).isEqualTo(limitKakao[1]);


        // naver
        param.put("keyword", "test");
        param.put("pageSort", "sim");
        param.put("pageNum", 1);
        param.put("pageCount", 10);
        resultServiceMap = blogService.pageOptionSetting(param, limitNaver, sortNaver);
        assertThat(resultServiceMap.get("keyword")).isEqualTo(param.get("keyword"));
        assertThat(resultServiceMap.get("pageSort")).isEqualTo(param.get("pageSort"));
        assertThat(resultServiceMap.get("pageNum")).isEqualTo(param.get("pageNum"));
        assertThat(resultServiceMap.get("pageCount")).isEqualTo(param.get("pageCount"));

        // naver : sort default, 페이지 limit
        param.put("keyword", "test");
        param.put("pageSort", "test");
        param.put("pageNum", 101);
        param.put("pageCount", 1001);
        resultServiceMap = blogService.pageOptionSetting(param, limitNaver, sortNaver);
        assertThat(resultServiceMap.get("keyword")).isEqualTo(param.get("keyword"));
        assertThat(resultServiceMap.get("pageSort")).isEqualTo(sortNaver[0]);
        assertThat(resultServiceMap.get("pageNum")).isEqualTo(limitNaver[0]);
        assertThat(resultServiceMap.get("pageCount")).isEqualTo(limitNaver[1]);
    }

    @Test
    @DisplayName("restTemplate")
    void restTemplate() {
        String searchEngine = "searchKakao";

        Map<String, Object> searchParam = new HashMap<>();
        searchParam.put("keyword", "지적");
        searchParam.put("pageNum", 1);
        searchParam.put("pageCount", 10);
        searchParam.put("pageSort", "accuracy");
        searchParam.put("searchEngine", "searchKakao");

        ArrayList<Map<String, Object>> documentsMapList = new ArrayList<>();
        Map<String, Object> documentsMap = new HashMap<>();
        documentsMap.put("contents", "test");
        documentsMap.put("datetime", "datetime");
        documentsMap.put("title", "title");
        documentsMap.put("url", "url");

        Map<String, Object> meta = new HashMap<>();
        meta.put("is_end", false);
        meta.put("pageable_count", 1);
        meta.put("total_count", 1);

        Map<String, Object> searchReturn = new HashMap<>();
        searchReturn.put("documents", documentsMapList);
        searchReturn.put("meta", meta);

        String url = "";
        HttpHeaders headers = new HttpHeaders();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        HttpEntity<MultiValueMap<String, String>> rtRequest;
        headers.set("Authorization", "KakaoAK " + kakaoApiKey);
        rtRequest = new HttpEntity<>(params, headers);

        url = "https://dapi.kakao.com/v2/search/web" + "?"
                        + "query=" + "지적" + "&sort=" + "accuracy"
                        + "&page=" + "1" + "&size=" + "10";

        Map<String, Object> resultResponse = null;
        try {
            given(blogService.restTemplate(searchParam, searchEngine)).willReturn(searchReturn);
            resultResponse = blogService.restTemplate(searchParam, searchEngine);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("RestTemplateKakao")
    void RestTemplateKakao() {

        // setting
        String type = "searchKakao";
        Map<String, Object> param = new HashMap<>();
        param.put("keyword", "지적");
        param.put("pageNum", 1);
        param.put("pageCount", 10);
        param.put("pageSort", "accuracy");
        param.put("searchEngine", "searchKakao");

        ResponseEntity<Map> rtResponse = null;
        Map<String, Object> resultResponse = null;
        HttpHeaders headers = new HttpHeaders();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        TestRestTemplate rt = restTemplate;
        HttpEntity<MultiValueMap<String, String>> rtRequest;
        String url = "";


        // when
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

        // then
        System.out.println("rtResponse = " + rtResponse);
        assertThat(rtResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(rtResponse.getBody()).isNotEmpty();

        Map<String, Object> rtResponseBody = rtResponse.getBody();
        assertThat(rtResponseBody.get("document")).isNotNull();
        assertThat(rtResponseBody.get("meta")).isNotNull();

    }

    @Test
    @DisplayName("RestTemplateNaver")
    void RestTemplateNaver() {

        // setting
        String type = "searchKakao";
        Map<String, Object> param = new HashMap<>();
        param.put("keyword", "지적");
        param.put("pageNum", 1);
        param.put("pageCount", 10);
        param.put("pageSort", "accuracy");
        param.put("searchEngine", "searchKakao");

        ResponseEntity<Map> rtResponse = null;
        Map<String, Object> resultResponse = null;
        HttpHeaders headers = new HttpHeaders();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        TestRestTemplate rt = restTemplate;
        HttpEntity<MultiValueMap<String, String>> rtRequest;
        String url = "";


        // when
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

        // then
        System.out.println("rtResponse = " + rtResponse);
        assertThat(rtResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(rtResponse.getBody()).isNotEmpty();

        Map<String, Object> rtResponseBody = rtResponse.getBody();
        assertThat(rtResponseBody.get("document")).isNotNull();
        assertThat(rtResponseBody.get("meta")).isNotNull();

    }
}