package com.laiqinan.pomodoro;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;

import com.laiqinan.util.DateUtils;

public class PomodoroWork {
	
	public static final String DATE_FORMAT="yyyy-MM-dd HH:mm:ss";

	File file;
	public int getTomato() {
		return time / 25;
	}

	public int getLeftTime() {
		return time % 25;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	private int time;
	private int rest;
	
	public int getRest() {
		return rest;
	}

	public void setRest(int rest) {
		this.rest = rest;
	}

	private int random=0;
	
	public PomodoroWork(String fileName) {
		file = new File(fileName);
		read();
	}

	public PomodoroWork(String string, DateProvider dp) {
		this.dateProvider = dp;
		file = new File(string);
		if (! file.exists()){
			createNewPropertyFile(string);
		}
		
		read();
	}

	private String arriveDateString="";
	
	public void createNewPropertyFile(String fileName){
		Properties prop = new Properties();
		prop.setProperty("time", "0");
		prop.setProperty("arriveTime","NOT BEEN SET");
		prop.setProperty("lastSaveTime",
				//TODO refactor
				new SimpleDateFormat(DATE_FORMAT).format(dateProvider.getDate()));
		try {
			prop.store(new FileOutputStream(fileName), null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private void write(){
		try {
			PrintWriter pw = new PrintWriter(file.getName());
			pw.println(new SimpleDateFormat(DATE_FORMAT).format(dateProvider.getDate()));
			pw.println(getTomato());
			pw.println(getLeftTime());
			pw.println(getRest());
			pw.println(arriveDateString);
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private void read() {
		if (file.getName().endsWith(".txt"))
			readFromTxt();
		else if (file.getName().endsWith(".properties"))
			readFromProperties();
	}
	
	private void readFromProperties() {
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(file));
			time = Integer.parseInt(prop.getProperty("time", "0"));
			arriveDateString = prop.getProperty("arriveTime");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void readFromTxt(){
		Scanner scanner=null;
		try {
			scanner = new Scanner(file);
			String lastOpenDateString = scanner.nextLine();
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(PomodoroWork.DATE_FORMAT);
			Date lastOpenDate = simpleDateFormat.parse(lastOpenDateString);
			Date current = dateProvider.getDate();
			if (DateUtils.isSameDay(lastOpenDate, current)){
				int tomato = scanner.nextInt();
				int leftTime = scanner.nextInt();
				rest = scanner.nextInt();
				time = tomato*25+leftTime;
				scanner.nextLine();
				arriveDateString = scanner.nextLine();
			}else{
				rest = 0;
				time = 0;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}finally{
			scanner.close();
		}
	}

	public void execute(String[] parameters) {
		
		String verb = parameters[0];
		
		if (verb.equals("time")){
			checkArgumentsSize(parameters, 2, "time need one argument");
			int addTime = Integer.parseInt(parameters[1]);
			time += addTime;
		}else if (verb.equals("add")){
			checkArgumentsSize(parameters, 2, "add need one argument");
			int addTomato = Integer.parseInt(parameters[1]);
			time += addTomato*25; 
		}else if (verb.equals("rest")){
			checkArgumentsSize(parameters, 2, "rest need one argument");
			int nr = Integer.parseInt(parameters[1]);
			rest += nr;
		}else if (verb.equals("-")||verb.equals("print")){
			
		}else if (verb.equals("random")){
			checkArgumentsSize(parameters, 3, "random need one argument");
			random = genRandom(Integer.parseInt(parameters[1]),Integer.parseInt(parameters[2]));
		}else if (verb.equals("start")){
			if (parameters.length==1)
				arriveDateString = new SimpleDateFormat(DATE_FORMAT).format(dateProvider.getDate());
			else if (parameters.length==2)
				arriveDateString = parameters[1];
			else
				throw new IllegalArgumentException("start need one or zero argument");
		}
		else {
			throw new RuntimeException("Undefined Pomodoro command");
		}
		write();
	}
	
	private void checkArgumentsSize(String [] parameters, int size, String message){
		if (parameters.length!=size){
			throw new IllegalArgumentException(message);
		}
	}
	

	private int genRandom(int start, int end) {
		Random rand = new Random();
		return rand.nextInt(end-start+1) + start;
	}

	public void execute(String string) {
		String [] parameters = string.split(" ");
		execute(parameters);
	}
	
	@Override
	public String toString() {
		return "PomodoroWork [time=" + time + ", rest=" + rest + "]";
	}
	
	public void print(){
		System.out.println("tomato:\t\t"+getTomato());
		System.out.println("left time:\t"+getLeftTime());
		System.out.println("rest :\t\t"+getRest());
		System.out.println("current production rate: \t"+getProductionScore());
		System.out.println("arrive time: "+arriveDateString);
		if (hasLargeBreak())
			System.out.println("Good, you can have a break!!!!!");
		else
			System.out.println("You can still achieve production score "+getMaxProductionRate()+" !!!");
	}
	
	
	private DateProvider dateProvider = new DateProvider();
	public void setDateProvider(DateProvider dateProvider) {
		this.dateProvider = dateProvider;
	}

	public int getProductionScore() {
		
		int tomato = getTomato();
		int productionScore = calculateProductionScore(tomato);
		
		return productionScore;
	}

	private int calculateProductionScore(int tomato) {
		int productionScore = 0;
		if (tomato>12 || tomato==12 && getLeftTime()>0)
			productionScore = 5;
		else if (tomato >= 11)
			productionScore = 4;
		else if (tomato >= 9)
			productionScore = 3;
		else if (tomato >= 6)
			productionScore = 2;
		else 
			productionScore = 1;
		return productionScore;
	}
	
	public int getEstimateRest() {
		Date current = dateProvider.getDate();
		long estimated = DateUtils.getStart(current).getTime()+hourToLong(10);
		
		estimated += getTomato() * 40 * 60 * 1000;
		
		if (isAfternoon(current)){
			estimated += hourToLong(1);
		}
		
		return (int)(estimated-current.getTime())/60/1000;
	}

	private boolean isAfternoon(Date current) {
		return current.getTime()-DateUtils.getStart(current).getTime()-hourToLong(14) > 0;
	}

	public boolean hasLargeBreak() {
		return getEstimateRest()>=25;
	}

	public int getMaxProductionRate() {
		Date current = dateProvider.getDate();
		// endOfWork = the start of the day (0:00:00)+ 19 hours (so I left 19:00). 
		long endOfWork = DateUtils.getStart(current).getTime()+hourToLong(19);
		long diff = endOfWork - current.getTime();
		int tomato = getTomato()+(int)diff/1000/60/30;
		return calculateProductionScore(tomato);
	}
	
	private long hourToLong(int hour){
		return hour*60*60*1000;
	}

	public int getRandom() {
		return random;
	}

	public String getArriveTimeString() {
		return arriveDateString;
	}

}
