package com.iskyshop.smilife.broadcast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iskyshop.foundation.domain.Article;
import com.iskyshop.foundation.service.IArticleService;
import com.tydic.framework.util.PropertyUtil;

@Service
public class BroadcastServiceImpl implements IBroadcastService{

    @Autowired
    private IArticleService articleService;

    /**
     * 获取广播消息轮播
     * @return
     */
    public Map<String,Object> getAppBroadcasts() {
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("type", "user");
        params.put("articleClassId", Long.parseLong(PropertyUtil.getProperty("AppBroadcastCode")));
        params.put("display", true);
        List<Article> arts = this.articleService.query("select obj from Article obj where obj.type=:type and obj.articleClass.id=:articleClassId and obj.display=:display order by obj.addTime desc",
                params, -1, -1);
        Map<String,Object> articleMap = new HashMap<String,Object>();
        List<String> titles = new ArrayList<String>();
        for(Article a:arts){
            titles.add(a.getTitle());
        }
        articleMap.put("broadcastMsg", titles);
        return articleMap;
    }
    
}
