package com.blog.service;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface BlogService {

    Map<String, Object> searchOfKeyword(Map<String, Object> param) throws Exception;

    Map<String, Object> searchPopularList() throws Exception;

}
