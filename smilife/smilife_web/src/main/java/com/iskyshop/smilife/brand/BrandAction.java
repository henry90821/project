package com.iskyshop.smilife.brand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.smilife.common.Result;
import com.iskyshop.smilife.enums.ErrorEnum;

@Controller
@RequestMapping("/api/app")
public class BrandAction {

	private Logger logger = Logger.getLogger(getClass());
	
    @Autowired
    private IBrandService brandService;
    
    /**
     * 获取指定品牌列表
     * @return
     */
    @RequestMapping(value="/mall2403GetBrandList.htm",produces={"application/json"})
    @ResponseBody
    public Object getBrandList(){
        Result result = new Result();
        try{
            List<Map> recommendedBrands = brandService.getRecommendedBrands();
            result.setData(recommendedBrands);
        }catch(Exception e){
            logger.error("获取指定品牌列表出参异常：" + e.getMessage());
            result.set(ErrorEnum.SYSTEM_ERROR).setMsg("获取指定品牌列表出参异常");
        }
        return result;
    }
    
    /**
     * 获取指定品牌商品列表
     */
    @RequestMapping(value = "/mall2404GetGoodsOfBrand.htm",produces={"application/json"})
    @ResponseBody
    public Object getBrandGoodsList(Long goodsBrandId, Integer currentPage, Integer pageSize, String orderBy, String orderType){
    	Result result = new Result();
    	try{
    		IPageList pList = brandService.getGoodsOfBrand(goodsBrandId, currentPage, pageSize, orderBy, orderType);
    	    
    		Map<String, Object> pageResult = new HashMap<String, Object>(5);
    	    pageResult.put("currentPage", pList.getCurrentPage() - 1);
    	    pageResult.put("pageSize", pList.getPageSize());
    	    pageResult.put("totalCount", pList.getRowCount());
    	    pageResult.put("pageCount", pList.getPages());
    	    
    	    List<Goods> goodsList = pList.getResult();
    	    List<Map<String, String>> goodsInfos = new ArrayList<Map<String, String>>(goodsList.size());
    	    
    	    for(Goods g: goodsList) {
    	    	Map<String, String> gi = new HashMap<String, String>(5);
    	    	gi.put("goodId", String.valueOf(g.getId()));
    	    	gi.put("goodName", g.getGoods_name());
    	    	gi.put("goodPrice", CommUtil.formatMoney(g.getGoods_current_price()));
    	    	gi.put("goodsSalenum", String.valueOf(g.getGoods_salenum()));
    	    	Accessory mainPhoto = g.getGoods_main_photo();
				if(mainPhoto != null) {
					gi.put("goodPicUrl", mainPhoto.getPath() + "/" + mainPhoto.getName());
				} else {
					gi.put("goodPicUrl", "");
				}
				goodsInfos.add(gi);
    	    }
    	    
    	    pageResult.put("list", goodsInfos);
    	    
    	    result.setData(pageResult);
    	}catch(Exception e){
    	    logger.error("获取指定品牌商品列表出参异常：" + e.getMessage());
    	    result.set(ErrorEnum.SYSTEM_ERROR).setMsg("获取指定品牌商品列表异常");
    	}
    	return result;
    }
}
