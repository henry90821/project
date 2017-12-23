package com.iskyshop.smilife.basicInfo;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.iskyshop.smilife.common.Result;
import com.iskyshop.smilife.enums.ErrorEnum;

@Controller
@RequestMapping("/api/app")
public class HotSearchAction {

    private Logger logger = Logger.getLogger(HotSearchAction.class);
    
    @Autowired
    private IHotSearchService hotSearchService;
    
    /**
     * 获取热搜关键词
     * @return
     */
    @RequestMapping(value="/mall2401GetHotSearch.htm",produces = { "application/json" })
    @ResponseBody
    public Object getHotSearch(){
        Result result = new Result();
        try{
            Map<String, Object> hotSearch = hotSearchService.getHotSearch();
            logger.info("获取热搜关键词出参："+hotSearch);
            result.setData(hotSearch);
        }catch(Exception e){
            logger.error(e.getMessage());
            result.set(ErrorEnum.SYSTEM_ERROR).setMsg("查询热门搜索关键词异常");
        }
        return result;        
    }
}
