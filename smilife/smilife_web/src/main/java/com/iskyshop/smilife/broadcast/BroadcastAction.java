package com.iskyshop.smilife.broadcast;

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
public class BroadcastAction {

    private Logger logger = Logger.getLogger(this.getClass());
    
    @Autowired
    private IBroadcastService articleService;
    
    /**
     * 获取广播消息轮播
     * @return
     */
    @RequestMapping(value="/mall2402GetBroadcastMsg.htm",produces={"application/json"})
    @ResponseBody
    public Object getAppBroadcast(){
        Result result = new Result();
        try{
            Map<String,Object> appBroadcasts = articleService.getAppBroadcasts();  
            logger.info("广播消息出参："+appBroadcasts);
            result.setData(appBroadcasts);
        }catch(Exception e){
            logger.error("获取广播消息异常:"+e.getMessage());
            result.set(ErrorEnum.SYSTEM_ERROR).setMsg("获取广播消息异常");
        }
        return result;
        
    }
}
