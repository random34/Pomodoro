package com.laiqinan.pomodoro;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

public class DateProviderTest {

	private DateProvider dp;

	@Before
	public void setUp() throws Exception {
		dp = new DateProvider();
		
	}

	@Test
	public void testProvideCurrentTrue() {
		dp.setProvideCurrentDate(true);
		Date pd = dp.getDate();
		Date c = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yymmdd");
		String d1 = sdf.format(pd);
		String d2 = sdf.format(c);
		assertEquals(d1,d2);
	}
	
	@Test
	public void testProvideCurrentFalse(){
		dp.setProvideCurrentDate(false);
		Date d = new Date();
		dp.setDate(d);
		Date nd = dp.getDate();
		assertEquals(d.toString(),nd.toString());
	}

}
