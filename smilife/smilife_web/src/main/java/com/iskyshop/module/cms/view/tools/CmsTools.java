package com.iskyshop.module.cms.view.tools;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.module.cms.domain.InformationClass;
import com.iskyshop.module.cms.domain.InformationReply;
import com.iskyshop.module.cms.service.IInformationClassService;
import com.iskyshop.module.cms.service.IInformationReplyService;

@Component
public class CmsTools {
	@Autowired
	private IInformationClassService informationClassService;
	@Autowired
	private IInformationReplyService informationReplyService;

	public List<Map> getCmsList(String json) {
		List<Map> maps = null;
		if (!StringUtils.isNullOrEmpty(json)) {
			Json.fromJson(List.class, json);
		}
		return maps;
	}

	public Map getCmsMap(String json) {
		Map map = null;
		if (!StringUtils.isNullOrEmpty(json)) {
			Json.fromJson(Map.class, json);
		}
		return map;
	}

	public String queryInforClass(String id) {
		InformationClass informationClass = this.informationClassService.getObjById(CommUtil.null2Long(id));
		if (informationClass != null) {
			return informationClass.getIc_name();
		} else {
			return "";
		}
	}

	public List<InformationClass> queryChildClass(String id) {
		Map map = new HashMap();
		map.put("id", CommUtil.null2Long(id));
		List<InformationClass> informationClasses = this.informationClassService
				.query("select obj from InformationClass obj where obj.ic_pid=:id", map, -1, -1);
		return informationClasses;
	}

	public int queryComment(String id) {
		Map map = new HashMap();
		map.put("id", CommUtil.null2Long(id));
		List<InformationReply> informationReplies = this.informationReplyService
				.query("select count(obj.id) from InformationReply obj where obj.info_id=:id", map, -1, -1);
		int count = CommUtil.null2Int(informationReplies.get(0));
		return count;
	}
}
