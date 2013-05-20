package com.laiqinan.pomodoro;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

public class PomodoroWorkTest {
	private static final String DEFAULT_SAVE_FILE_NAME = "pomodoro-data/save.properties";
	private PomodoroWork pomodoro;
	
	@Before
	public void setUp(){
//		pomodoro = new PomodoroWork("save.txt");
		//TODO use @parameter
		pomodoro = new PomodoroWork(DEFAULT_SAVE_FILE_NAME);
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
	//TODO refactor
	public void testTimeShortHand(){
		String [] parameters = {"t","10"};
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
	//TODO refactor
	public void testAddShortHand(){
		String [] parameters = {"a","3"};
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
		PomodoroWork npw = new PomodoroWork(DEFAULT_SAVE_FILE_NAME);
		int nt = npw.getTime();
		int nr = npw.getRest();
		assertTrue(nt-t==25);
		assertEquals(nr,r);
	}
	
	@Test
	public void testNewDayRollback() throws Exception {
		PomodoroWork yesterday = new PomodoroWork("pomodoro-data/save_yesterday.properties");
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
	public void testIllegalAddArguments() throws Exception {
		try {
			pomodoro.execute("add");
			fail("Didnot throw exception");
		} catch (Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
		}
	}
	
	@Test
	public void testIllegalRestArguments() throws Exception {
		try {
			pomodoro.execute("rest 1 2 3 4");
			fail("Didnot throw exception");
		} catch (Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
		}
	}
	
	@Test
	public void testIllegalTimeArguments() throws Exception {
		try {
			pomodoro.execute("time 4 3");
			fail("Didnot throw exception");
		} catch (Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
		}
	}
	
	@Test
	public void testIllegalRandomArguments() throws Exception{
		try {
			pomodoro.execute("random 4");
			fail("Didnot throw exception");
		} catch (Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
		}
	}
	
	@Test
	public void testIllegalTypeArguments() throws Exception{
		try {
			pomodoro.execute("add abc");
			fail("Didnot throw exception");
		} catch (Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
		}		
	}
	
	@Test
	public void testEstimateRestMorningStart() throws Exception {
		PomodoroWork p = createPomodoroByParseDate("2013-03-13 10:00:00", DEFAULT_SAVE_FILE_NAME);
		p.setTime(0);
		assertEquals(p.getEstimateRest(),0);
	}

	@Test
	public void testEstimateRest1130() throws Exception {
		PomodoroWork p = createPomodoroByParseDate("2013-03-13 11:30:00", DEFAULT_SAVE_FILE_NAME);
		p.setTime(75);
		assertEquals(p.getEstimateRest(),30);
		p.setTime(50);
		assertTrue(p.getEstimateRest()<0);
	}
	
	@Test
	public void testHasLargeBreakMorning() throws Exception {
		PomodoroWork p = createPomodoroByParseDate("2013-03-13 11:30:00", DEFAULT_SAVE_FILE_NAME);
		p.setTime(75);
		assertTrue(p.hasLargeBreak());
		p.setTime(50);
		assertFalse(p.hasLargeBreak());
		p = createPomodoroByParseDate("2013-03-13 13:00:00", DEFAULT_SAVE_FILE_NAME);
		p.setTime(125);
		assertFalse(p.hasLargeBreak());
	}
	
	@Test
	public void testHasLargeBreakAfternoon() throws Exception {
		PomodoroWork p = createPomodoroByParseDate("2013-03-13 15:30:00", DEFAULT_SAVE_FILE_NAME);
		p.setTime(200);
		assertTrue(p.hasLargeBreak());
		p.setTime(175);
		assertFalse(p.hasLargeBreak());
	}
	
	@Test
	public void testMaxProductionRate() throws Exception {
		PomodoroWork p = createPomodoroByParseDate("2013-03-13 17:00:00", DEFAULT_SAVE_FILE_NAME);
		p.setTime(200);
		assertEquals(4,p.getMaxProductionRate());
		p.setTime(225);
		assertEquals(5,p.getMaxProductionRate());
		p.setTime(125);
		assertEquals(3,p.getMaxProductionRate());
	}
	
	@Test
	public void testRandomLegal() throws Exception{
		final int start = 2;
		final int end = 11;
		String cmd = "random "+start+" "+end;
		boolean hasStart = false;
		boolean hasEnd = false;
		for (int i=0; i<1000; i++){
			pomodoro.execute(cmd);
			assertTrue(pomodoro.getRandom()>=start);
			assertTrue(pomodoro.getRandom()<=end);
			if (pomodoro.getRandom()==start)
				hasStart = true;
			if (pomodoro.getRandom()==end)
				hasEnd = true;
		}
		assertTrue(hasStart);
		assertTrue(hasEnd);
		
	}
	
	@Test
	//TODO refactor
	public void testRandomLegalShortHand() throws Exception{
		final int start = 2;
		final int end = 11;
		String cmd = "r "+start+" "+end;
		boolean hasStart = false;
		boolean hasEnd = false;
		for (int i=0; i<1000; i++){
			pomodoro.execute(cmd);
			assertTrue(pomodoro.getRandom()>=start);
			assertTrue(pomodoro.getRandom()<=end);
			if (pomodoro.getRandom()==start)
				hasStart = true;
			if (pomodoro.getRandom()==end)
				hasEnd = true;
		}
		assertTrue(hasStart);
		assertTrue(hasEnd);
		
	}
	@Test
	public void testArriveCheckin() throws Exception {
		PomodoroWork p = createPomodoroByParseDate("2013-04-01 10:00:00", DEFAULT_SAVE_FILE_NAME);
		p.execute("start");
		assertTrue("2013-04-01 10:00:00".equals(p.getArriveTimeString()));
	}
	
	@Test
	//TODO refactor
	public void testArriveCheckinShortHand() throws Exception {
		PomodoroWork p = createPomodoroByParseDate("2013-04-01 10:00:00", DEFAULT_SAVE_FILE_NAME);
		p.execute("s");
		assertTrue("2013-04-01 10:00:00".equals(p.getArriveTimeString()));
	}
	@Test
	public void testArriveCheckinSave() throws Exception {
		PomodoroWork p = createPomodoroByParseDate("2013-04-01 10:00:00", DEFAULT_SAVE_FILE_NAME);
		p.execute("start");
		PomodoroWork pp = createPomodoroByParseDate("2013-04-01 12:00:00", DEFAULT_SAVE_FILE_NAME);
		assertTrue("2013-04-01 10:00:00".equals(pp.getArriveTimeString()));
	}
	
	@Test
	public void testArriveCheckinCustomData() throws Exception {
		pomodoro.execute("start 08:00:00");
		assertTrue(pomodoro.getArriveTimeString().equals("08:00:00"));
	}
	
	@Test
	public void testPropertyFile() throws Exception {
		final String fileName = "pomodoro-data/testWrite.properties";
		final String attributeName = "attributes1";
		final String attributeValue = "001";
		Properties prop = new Properties();
		prop.setProperty(attributeName, attributeValue);
		prop.store(new FileOutputStream(fileName), null);
		
		Properties prop2 = new Properties();
		prop2.load(new FileInputStream(fileName));
		final String readAttribute = prop2.getProperty(attributeName);
		assertEquals(readAttribute, attributeValue);
	}
	
	@Test
	public void testEmptyProperty() throws Exception {
		
		final String fileName = "pomodoro-data/testWrite.properties";
		Properties prop2 = new Properties();
		prop2.load(new FileInputStream(fileName));
		final String readAttribute = prop2.getProperty("does not exits");
		assertEquals(readAttribute,null);
	}
	
	@Test
	public void testCreateNewPropertyFile() throws Exception {
		final String fileName = "pomodoro-data/newSave.properties";
		File file = new File(fileName);
		if (file.exists())
			file.delete();
		final String timeString = "2013-04-01 10:00:00";
		PomodoroWork p = createPomodoroByParseDate(timeString, fileName);
		p.execute("time 0");
		Properties prop = new Properties();
		prop.load(new FileInputStream(fileName));
		assertEquals(prop.getProperty("time"), "0");
		assertEquals(prop.getProperty("arriveTime"),"NOT BEEN SET");
		assertEquals(prop.getProperty("lastSaveTime"), timeString);
	}
	
	@Test
	public void testLoadPropertyFile()throws Exception{
		final String fileName = "pomodoro-data/testFileRead.properties";
		final String timeString = "2013-04-01 10:00:00";
		PomodoroWork p = createPomodoroByParseDate(timeString, fileName);
		assertEquals(p.getTime(),51);
		assertEquals(p.getArriveTimeString(),"arriveTime");
		
	}
	
	
	//you can have a large break
	//you should not have a break
	//max possible production rate
	//13-5,13:30-6
	
	//analysis results? 
	//given the achievement and current time
	//tell me: it is OK/need to concentrate more on work
	

}
