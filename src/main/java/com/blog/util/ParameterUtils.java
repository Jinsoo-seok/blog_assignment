package com.blog.util;

import java.util.HashMap;
import java.util.Map;

public class ParameterUtils {

    public static Map<String, Object> pageOption(Map<String, Object> param) {
        Map<String, Object> result = new HashMap<>();

        int pageCount = ((param.containsKey("pageCount") == false) ? 10 : (Integer.parseInt((String) param.get("pageCount"))));
        int pageNum = ((param.containsKey("pageNum") == false) ? 1 : (Integer.parseInt((String) param.get("pageNum"))));
        int page = ((pageNum-1) * pageCount);

        result.put("pageCount", pageCount);
        result.put("pageNum", pageNum);
        result.put("page", page);

        return result;
    }
}
