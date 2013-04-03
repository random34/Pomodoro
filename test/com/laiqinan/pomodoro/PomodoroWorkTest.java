package com.laiqinan.pomodoro;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

public class PomodoroWorkTest {
	private PomodoroWork pomodoro;
	
	@Before
	public void setUp(){
		pomodoro = new PomodoroWork("save.txt");
	}

	@Test
	public void testFileRead() throws Exception{
		PomodoroWork p = createPomodoroByParseDate("2013-03-13 08:00:00", "save_testFileRead.txt");
		assertEquals(p.getTomato(),4);
		assertEquals(p.getLeftTime(),0);
		assertEquals(p.getTime(),100);
		assertEquals(p.getRest(),60);
	}

	private PomodoroWork createPomodoroByParseDate(String dateStr, String fileName) throws ParseException {
		DateProvider dp = new DateProvider();
		dp.setProvideCurrentDate(false);
		Date d = new SimpleDateFormat(PomodoroWork.DATE_FORMAT).parse(dateStr);
		dp.setDate(d);		
		PomodoroWork p = new PomodoroWork(fileName,dp);
		return p;
	}
	
	@Test
	public void testTimeBasic() {
		String [] parameters = {"time","100"};
		int tomato = pomodoro.getTomato();
		pomodoro.execute(parameters);
		int newTomato = pomodoro.getTomato();
		assertEquals(newTomato-tomato,4);
	}
	
	@Test
	public void testTimeCarry(){
		String [] parameters = {"time","10"};
		int tomato = pomodoro.getTomato();
		int leftTime = pomodoro.getLeftTime();
		int time = pomodoro.getTime();
		
		pomodoro.execute(parameters);
		int newTomato = pomodoro.getTomato();
		int newLeftTime = pomodoro.getLeftTime();
		int newTime = pomodoro.getTime();
		assertEquals(time+10,newTime);
		assertEquals(tomato*25+leftTime+10,newTomato*25+newLeftTime);
	} 
	
	@Test
	public void testAdd(){
		String [] parameters = {"add","3"};
		int tomato = pomodoro.getTomato();
		int leftTime = pomodoro.getLeftTime();
		int time = pomodoro.getTime();
		pomodoro.execute(parameters);
		int newTo = pomodoro.getTomato();
		int nL = pomodoro.getLeftTime();
		int nT = pomodoro.getTime();
		assertTrue(newTo-tomato==3);
		assertEquals(leftTime,nL);
		assertTrue(nT-time==75);
	}
	
	@Test 
	public void testRest(){
		String [] parameters = {"rest","25"};
		int t = pomodoro.getTime();
		int r = pomodoro.getRest();
		pomodoro.execute(parameters);
		int nt = pomodoro.getTime();
		int nr = pomodoro.getRest();
		assertEquals(t,nt);
		assertTrue(r+25==nr);
	}
	
	@Test
	public void testOneStringCommand() throws Exception {
		int t = pomodoro.getTomato();
		pomodoro.execute("time 25");
		int nt = pomodoro.getTomato();
		assertTrue(nt-t==1);
	}
	
	@Test
	public void testUnknownCommand(){
		String [] para = {"shouldNotExists@","100"};
		try {
			pomodoro.execute(para);
		} catch (Exception e) {
		}

	}
	
	@Test
	public void testFileWrite() throws Exception {
		int t = pomodoro.getTime();
		int r = pomodoro.getRest();
		pomodoro.execute("add 1");
		PomodoroWork npw = new PomodoroWork("save.txt");
		int nt = npw.getTime();
		int nr = npw.getRest();
		assertTrue(nt-t==25);
		assertEquals(nr,r);
	}
	
	@Test
	public void testNewDayRollback() throws Exception {
		PomodoroWork yesterday = new PomodoroWork("save_yesterday.txt");
		assertTrue(yesterday.getTime()==0);
		assertTrue(yesterday.getRest()==0);
	}
	
	@Test
	public void testProductionScore() throws Exception {
		pomodoro.setTime(305);
		int score = pomodoro.getProductionScore();
		assertEquals(score, 5);
		pomodoro.setTime(275);
		assertEquals(4, pomodoro.getProductionScore());
		pomodoro.setTime(225);
		assertEquals(3,pomodoro.getProductionScore());
		pomodoro.setTime(150);
		assertEquals(2,pomodoro.getProductionScore());
		pomodoro.setTime(75);
		assertEquals(1, pomodoro.getProductionScore());
	}
	
	@Test
	public void testTemp() throws Exception {
		Date current = new Date();
		System.out.println(new SimpleDateFormat(PomodoroWork.DATE_FORMAT).format(current));
	}
	
	@Test
	public void testIllegalArguments() throws Exception {
		try {
			pomodoro.execute("add");
			fail("Didnot throw exception");
		} catch (Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
		}
		try {
			pomodoro.execute("add abc");
			fail("Didnot throw exception");
		} catch (Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
		}
	}
	
	@Test
	public void testEstimateRestMorningStart() throws Exception {
		PomodoroWork p = createPomodoroByParseDate("2013-03-13 10:00:00", "save.txt");
		p.setTime(0);
		assertEquals(p.getEstimateRest(),0);
	}

	@Test
	public void testEstimateRest1130() throws Exception {
		PomodoroWork p = createPomodoroByParseDate("2013-03-13 11:30:00", "save.txt");
		p.setTime(75);
		assertEquals(p.getEstimateRest(),30);
		p.setTime(50);
		assertTrue(p.getEstimateRest()<0);
	}
	
	@Test
	public void testHasLargeBreakMorning() throws Exception {
		PomodoroWork p = createPomodoroByParseDate("2013-03-13 11:30:00", "save.txt");
		p.setTime(75);
		assertTrue(p.hasLargeBreak());
		p.setTime(50);
		assertFalse(p.hasLargeBreak());
		p = createPomodoroByParseDate("2013-03-13 13:00:00", "save.txt");
		p.setTime(125);
		assertFalse(p.hasLargeBreak());
	}
	
	@Test
	public void testHasLargeBreakAfternoon() throws Exception {
		PomodoroWork p = createPomodoroByParseDate("2013-03-13 15:30:00", "save.txt");
		p.setTime(200);
		assertTrue(p.hasLargeBreak());
		p.setTime(175);
		assertFalse(p.hasLargeBreak());
	}
	
	@Test
	public void testMaxProductionRate() throws Exception {
		PomodoroWork p = createPomodoroByParseDate("2013-03-13 17:00:00", "save.txt");
		p.setTime(200);
		assertEquals(4,p.getMaxProductionRate());
		p.setTime(225);
		assertEquals(5,p.getMaxProductionRate());
		p.setTime(125);
		assertEquals(3,p.getMaxProductionRate());
	}
	

	//you can have a large break
	//you should not have a break
	//max possible production rate
	//13-5,13:30-6
	
	//analysis results? 
	//given the achievement and current time
	//tell me: it is OK/need to concentrate more on work
	

}