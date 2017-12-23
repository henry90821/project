package com.iskyshop.smilife.basicInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.service.ISysConfigService;

@Service
public class HotSearchServiceImpl implements IHotSearchService {
    @Autowired
    private ISysConfigService sysConfigService;

    /**
     * 获取热搜关键词
     * @return
     */
    public Map<String,Object> getHotSearch() {
        // TODO Auto-generated method stub
        SysConfig sysConfig = sysConfigService.getSysConfig();
        String hotSearchStr = sysConfig.getHotSearch();
        String[] hotSearchArray = hotSearchStr.split(",");
        Map<String, Object> hotSearchMap = new HashMap<String, Object>();
        List<String> words = new ArrayList<String>();
        for (String hotSearch : hotSearchArray) {
            words.add(hotSearch);
        }
        hotSearchMap.put("word", words);
        return hotSearchMap;
    }

}
