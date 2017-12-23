package com.iskyshop.smilife.basicInfo;

import java.util.Map;

public interface IHotSearchService {

    /**
     * 获取热搜关键词
     * @return
     */
    public Map<String,Object> getHotSearch();
}
