package com.iskyshop.core.tools;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.iskyshop.foundation.service.IUserService;

public class ExcelExportTest {
	@Autowired
	private IUserService userService;
	
	public static InputStream getUserExcel() throws IllegalArgumentException, IOException, IllegalAccessException {
		HashMap params=new HashMap();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		List<String[]> headNames = new ArrayList<String[]>();
		headNames.add(new String[] { "用户名", "昵称", "真实姓名", "用户角色", "出生日期" });
		List<String[]> fieldNames = new ArrayList<String[]>();
		fieldNames.add(new String[] { "userName", "nickName", "trueName", "userRole", "birthday" });

		ExportSetInfo setInfo = new ExportSetInfo();
		String query="select obj from User obj";
//        this.userService.query(query, params, -1,-1);
		setInfo.setFieldNames(fieldNames);
		setInfo.setTitles(new String[] { "后台用户信息" });
		setInfo.setHeadNames(headNames);
		setInfo.setOut(baos);
		// 将需要导出的数据输出到baos
		ExcelUtil.export2Excel(setInfo);
		return new ByteArrayInputStream(baos.toByteArray());
	}
	
	public static void main(String[] args) {
//		ExcelExportTest.getUserExcel();
	}
}
