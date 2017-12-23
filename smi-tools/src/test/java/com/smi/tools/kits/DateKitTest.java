package com.smi.tools.kits;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import com.smi.tools.lang.DateTime;

public class DateKitTest {

	@Test
	public void testOffsiteDate() {
		System.out.println(DateKit.offsiteDay(new Date(), 1));
	}
	
	
	@Test
	public void testDiff() {
		DateTime d1 = DateKit.offsiteDate(new Date(), Calendar.MINUTE, 5);
		DateTime d2 = new DateTime();
		System.out.println(d1 + "   " + d2);
		System.out.println(DateKit.diff(d2, d1, 3));
		
		System.out.println(StrKit.getRandomStr(6, true));
	}
}
