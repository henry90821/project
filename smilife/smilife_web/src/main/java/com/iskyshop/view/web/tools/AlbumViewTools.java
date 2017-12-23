package com.iskyshop.view.web.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.IAlbumService;

/**
 * 商城图片处理工具类
 * 
 * @author erikzhang
 * 
 */
@Component
public class AlbumViewTools {
	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private IAlbumService albumService;
	@Autowired
	private IAccessoryService accessoryService;

	public List<Accessory> query_album(String id) {
		List<Accessory> list = new ArrayList<Accessory>();
		if (id != null && !"".equals(id)) {
			Map params = new HashMap();
			params.put("album_id", CommUtil.null2Long(id));
			list = this.accessoryService.query("select obj from Accessory obj where obj.album.id=:album_id", params, -1, -1);
		} else {
			list = this.accessoryService.query("select obj from Accessory obj where obj.album.id is null", null, -1, -1);
		}
		return list;
	}
}
