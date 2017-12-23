package com.smi.mc.service.atomic.cust;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.smi.mc.common.PageMap;
import com.smi.mc.dao.cust.CustInfoMapper;
import com.smi.mc.exception.AtomicServiceException;
import com.smi.mc.po.PageBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.bull.javamelody.MonitoredWithSpring;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 会员基本信息原子服务
 * 
 * @author smi
 *
 */
@MonitoredWithSpring
@Service
public class CustAtomicService {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private CustInfoMapper custInfoMapper;

	/**
	 * 会员基本信息查询
	 * 
	 * @param param
	 * @return
	 * @throws AtomicServiceException
	 */
	public Map<String, Object> qryCustInfo(Map<String, Object> param) throws AtomicServiceException {
		List<Map<String, Object>> list = null;
		Map<String, Object> resMap = new HashMap<String, Object>();
		try {
			// PageMap pageMap = new PageMap();

			if ((!StringUtils.isEmpty((String) param.get("PAGE_INDEX")))
					&& (!StringUtils.isEmpty((String) param.get("PAGE_SIZE")))) {
				int page_index = Integer.parseInt((String) param.get("PAGE_INDEX"));
				int page_size = Integer.parseInt((String) param.get("PAGE_SIZE"));
				// pageMap.setStartNum((page_index - 1) * page_size);
				// pageMap.setPageSize(page_size);
				// param.put("pageMap", pageMap);
				// 分页
				// PageHelper.startPage(page_index, page_size);
				PageBean pages = new PageBean(page_index, page_size);
				param.put("startNum", pages.getStart());
				param.put("endNum", pages.getEnd());
				list = custInfoMapper.qryCustInfoByPage(param);
				int total=this.custInfoMapper.selectCountPages(param);
				pages.setTotal(total);
				pages.setPages(pages.getTotal()/page_size+1);
				pages.setPagesize(page_size,page_index,total);
				resMap.put("TOTAL",pages.getTotal());
				resMap.put("PAGE_SIZE", pages.getPagesize());
				resMap.put("TOTAL_PAGE", pages.getPages());
			} else {
				list = custInfoMapper.qryCustInfo(param);
			}
			resMap.put("CUST", list);
			return resMap;
		} catch (Exception e) {
			this.logger.error("查询会员资料异常", e);
			throw new AtomicServiceException(e);
		}
	}

	/**
	 * 会员基本信息的增加
	 * 
	 * @param requestMap
	 * @return
	 * @throws AtomicServiceException
	 */
	public String addCust(Map<String, Object> requestMap) throws AtomicServiceException {
		try {
			custInfoMapper.addCust(requestMap);
			return requestMap.get("CUST_ID").toString();
		} catch (Exception e) {
			this.logger.error("新增会员异常", e);
			throw new AtomicServiceException(e);
		}
	}

	/**
	 * 会员基本信息的更新
	 * 
	 * @param requestMap
	 * @return
	 * @throws AtomicServiceException
	 */
	public int updCust(Map<String, Object> requestMap) throws AtomicServiceException {
		if (this.logger.isDebugEnabled())
			this.logger.debug("enter method:CustAtomicService.updCust");
		try {
			int rows = custInfoMapper.updCust(requestMap);
			this.logger.info("修改的记录有" + rows + "行");
			return rows;
		} catch (Exception e) {
			this.logger.error("修改会员异常", e);
			throw new AtomicServiceException(e);
		}
	}
}