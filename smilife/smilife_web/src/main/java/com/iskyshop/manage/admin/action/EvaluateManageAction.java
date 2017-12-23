package com.iskyshop.manage.admin.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.foundation.domain.Evaluate;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.query.EvaluateQueryObject;
import com.iskyshop.foundation.service.IEvaluateService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.view.web.tools.EvaluateViewTools;
import com.iskyshop.view.web.tools.GoodsViewTools;

/**
 * 
 * <p>
 * Title: EvaluateManageAction.java
 * </p>
 * 
 * <p>
 * Description: 系统商品评价管理类
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2014
 * </p>
 * 
 * <p>
 * Company: 沈阳网之商科技有限公司 www.iskyshop.com
 * </p>
 * 
 * @author erikzhang
 * 
 * 
 * @version 1.0
 */
@Controller
public class EvaluateManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IEvaluateService evaluateService;
	@Autowired
	private EvaluateViewTools evaluateViewTools;
	@Autowired
	private IGoodsService goodsService;
	
	@Autowired
	private GoodsViewTools goodsViewTools;

	@SecurityMapping(title = "商品评价列表", value = "/admin/evaluate_list.htm*", rtype = "admin", rname = "商品评价", rcode = "evaluate_admin", rgroup = "交易")
	@RequestMapping("/admin/evaluate_list.htm")
	public ModelAndView evaluate_list(HttpServletRequest request, HttpServletResponse response, String currentPage,
					String orderBy, String orderType, String goods_name, String userName) {
		ModelAndView mv = new JModelAndView("admin/blue/evaluate_list.html", configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request, response);
		EvaluateQueryObject qo = new EvaluateQueryObject(currentPage, mv, orderBy, orderType);
		if (!StringUtils.isNullOrEmpty(goods_name)) {
			qo.addQuery("obj.evaluate_goods.goods_name", new SysMap("goods_name", "%" + goods_name + "%"), "like");
		}
		if (!StringUtils.isNullOrEmpty(userName)) {
			qo.addQuery("obj.evaluate_user.userName", new SysMap("evaluate_user", userName), "=");
		}
		mv.addObject("goods_name", goods_name);
		mv.addObject("userName", userName);
		IPageList pList = this.evaluateService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		
		mv.addObject("goodsViewTools", goodsViewTools);
		return mv;
	}
	
	@SecurityMapping(title = "商品评价导入", value = "/admin/evaluate_export.htm*", rtype = "admin", rname = "商品评价", rcode = "evaluate_admin", rgroup = "交易")
	@RequestMapping("/admin/evaluate_export.htm")
	public ModelAndView evaluate_export(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/success.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Logger.getLogger(this.getClass()).info("------------start import goods evaluate----------");
		
		//获取上传的xlsx文件
		try {
			String path = CommUtil.getServerRealPathFromRequest(request) + "csv" + File.separator
					+ "evaluate";
			CommUtil.createFolder(path);
			int already_import_count = 0;
			int no_import_count = 0;
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Map map = CommUtil.saveFileToServer(request, "xls_evaluate", path, "evaluate.xls", new String[]{"xls"});
			if (!StringUtils.isNullOrEmpty(map.get("fileName"))) {
				String file=path+File.separator+map.get("fileName");
				
				//读取xls文件
				InputStream stream = new FileInputStream(file);  
		        Workbook wb = new HSSFWorkbook(stream);  
		        
		        Sheet sheet1 = wb.getSheetAt(0);  
		        for (Row row : sheet1) {
		        	int rowNum=row.getRowNum();
		        	if(rowNum==0)
		        		continue;
		        	
		        	//基本数据初始化
		        	String userName="";
		        	long goodsId=0;
		        	String goodsName="";
		        	int saleAmount=-1;
		        	String evaluateMsg="";
		        	int evaluateType=1;//默认好评
		        	Date evaluateTime=null;
		        	
		        	//判断类型
		            for (Cell cell : row) {
		            	//读取每个条目的数据
		            	String cellData=null;
		            	switch(cell.getCellType()){
		            	case Cell.CELL_TYPE_BLANK:
		            		break;
		            	case Cell.CELL_TYPE_NUMERIC:
		            		cellData=String.valueOf((long)cell.getNumericCellValue()); 
		            		break;
		            	case Cell.CELL_TYPE_STRING:
		            		cellData=cell.getStringCellValue();
		            		break;
		            	//XLSX公式栏
		            	case Cell.CELL_TYPE_FORMULA:
		            		cellData=String.valueOf(cell.getNumericCellValue()); 
		            		break;
		            	default:
		            		continue;
		            	}
		            	
		            	if(cell.getColumnIndex()==0)
		            		goodsId=CommUtil.null2Long(cellData);
		            	else if(cell.getColumnIndex()==1)
		            		goodsName=cellData;
		            	else if(cell.getColumnIndex()==2)
		            		saleAmount=cellData==null? saleAmount : CommUtil.null2Int(cellData);
		            	else if(cell.getColumnIndex()==3)
		            		evaluateMsg=cellData;
		            	else if(cell.getColumnIndex()==4){
		            		if("好评".equals(cellData))
		            			evaluateType=1;
		            		else if("中评".equals(cellData))
		            			evaluateType=0;
		            		else if("差评".equals(cellData))
		            			evaluateType=-1;
		            		else
		            			evaluateType=1;
		            	}
		            	else if(cell.getColumnIndex()==5)
		            		userName=cellData.trim();
		            	else if(cell.getColumnIndex()==6)
		            		evaluateTime=sdf.parse(cellData);
		            	
		            }  
		            
		            //如果提供的买家账号是 xxxxxxxxx(小张) 形式 则去掉用户名字
		            Pattern p=Pattern.compile("(.+)\\(.+\\)$");
		            Matcher matcher=p.matcher(userName);
		            if(matcher.find())
		            	userName=matcher.group(1);
		            
		            if(userName==null || userName.length()<=0)
		            	continue;
		            
		            //保存评论
		            if(evaluateService.saveEvaluateFromXlsxRow(userName, goodsId, evaluateMsg, evaluateType, evaluateTime, saleAmount))
		            	already_import_count +=1;
		            else
		            	no_import_count +=1;
		        }
		        
		        String msg="导入评论成功数量："+already_import_count+" 失败数量："+no_import_count + "!";
		        Logger.getLogger(this.getClass()).info(msg);
		        mv.addObject("op_title", msg);
				mv.addObject("list_url", CommUtil.getURL(request) + "/admin/evaluate_list.htm");
			}
		}catch(Exception e){
			mv.addObject("op_title","出错了，错误信息:"+e.getMessage());
			Logger.getLogger(this.getClass()).info("throw exception msg:"+e.getMessage());
			e.printStackTrace();
		}
		
		return mv;
	}

	@SecurityMapping(title = "商品评价编辑", value = "/admin/evaluate_edit.htm*", rtype = "admin", rname = "商品评价", rcode = "evaluate_admin", rgroup = "交易")
	@RequestMapping("/admin/evaluate_edit.htm")
	public ModelAndView evaluate_edit(HttpServletRequest request, HttpServletResponse response, String currentPage,
					String id) {
		ModelAndView mv = new JModelAndView("admin/blue/evaluate_edit.html", configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request, response);
		Evaluate obj = this.evaluateService.getObjById(CommUtil.null2Long(id));
		mv.addObject("obj", obj);
		mv.addObject("evaluateViewTools", this.evaluateViewTools);
		return mv;
	}

	@SecurityMapping(title = "商品评价编辑", value = "/admin/evaluate_save.htm*", rtype = "admin", rname = "商品评价", rcode = "evaluate_admin", rgroup = "交易")
	@RequestMapping("/admin/evaluate_save.htm")
	public ModelAndView evaluate_save(HttpServletRequest request, HttpServletResponse response, String currentPage,
					String id, String evaluate_status, String evaluate_admin_info, String list_url, String edit) {
		ModelAndView mv = new JModelAndView("admin/blue/success.html", configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request, response);
		Evaluate obj = this.evaluateService.getObjById(CommUtil.null2Long(id));
		obj.setEvaluate_admin_info(evaluate_admin_info);
		int eva_status = CommUtil.null2Int(evaluate_status);
		obj.setEvaluate_status(eva_status);
		this.evaluateService.update(obj);
		
		//如果管理员取消了评价，则要对商品的评分进行调整
		if(eva_status == 2) {
			Goods goods = this.goodsService.getObjById(obj.getEvaluate_goods().getId());
			int eva_val_old = obj.getEvaluate_buyer_val();
			BigDecimal desc_eva_val_old = obj.getDescription_evaluate();
			
			BigDecimal eva_count = BigDecimal.valueOf(goods.getEvaluate_count());
            BigDecimal desc_eva_total = goods.getDescription_evaluate().multiply(eva_count);
			eva_count = eva_count.subtract(BigDecimal.valueOf(1));
			
			goods.setEvaluate_count(eva_count.intValue());
			goods.setDescription_evaluate(BigDecimal.valueOf(CommUtil.div(desc_eva_total.subtract(desc_eva_val_old), eva_count)));
			if(eva_val_old == 1) {//好评
				goods.setWell_evaluate(goods.getWell_evaluate() - 1);
			} else if(eva_val_old == 0) {//中评
				goods.setMiddle_evaluate(goods.getMiddle_evaluate() - 1);
			} else {//差评
				goods.setBad_evaluate(goods.getBad_evaluate() - 1);
			}	
			this.goodsService.update(goods);
		}
		
		mv.addObject("list_url", list_url);
		mv.addObject("op_title", "商品评价编辑成功");
		return mv;
	}
}
