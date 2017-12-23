package com.iskyshop.core.tools;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TransformDate {
	private static Log log = LogFactory.getLog(TransformDate.class);

	public static void main(String[] args) {
		// String x = "Mon Mar 02 13:57:49 CST 2015";
		String a = null;
		String x = "Tue Nov 03 18:08:09 CST 2015";
		SimpleDateFormat sdf1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss", Locale.UK);
		try {
			Date date = sdf1.parse(x);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String sDate = sdf.format(date);
			System.out.println(a.toString());
			System.out.println(sDate);
			log.info("-------");
		} catch (Exception e) {
			e.getStackTrace();
			log.info(e.getMessage());
//			log.error("go",e.getMessage());
		}

		System.out.println("可以执行");
	}
}
