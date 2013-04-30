package com.laiqinan.pomodoro;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
		read();
	}

	private void write(){
		try {
			PrintWriter pw = new PrintWriter(file.getName());
			pw.println(new SimpleDateFormat(DATE_FORMAT).format(new Date()));
			pw.println(getTomato());
			pw.println(getLeftTime());
			pw.println(getRest());
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private void read() {
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
			if (parameters.length!=2){
				throw new IllegalArgumentException("time need one argument");
			}
			int addTime = Integer.parseInt(parameters[1]);
			time+=addTime;
		}else if (verb.equals("add")){
			if (parameters.length!=2){
				throw new IllegalArgumentException("add need one argument");
			}
			int addTomato = Integer.parseInt(parameters[1]);
			time+=addTomato*25; 
		}else if (verb.equals("rest")){
			if (parameters.length!=2){
				throw new IllegalArgumentException("rest need one argument");
			}			
			int nr = Integer.parseInt(parameters[1]);
			rest += nr;
		}else if (verb.equals("-")||verb.equals("print")){
			
		}else if (verb.equals("random")){
			if (parameters.length!=3){
				throw new IllegalArgumentException("random need one argument");
			}
			random = genRandom(Integer.parseInt(parameters[1]),Integer.parseInt(parameters[2]));
		}
		else {
			throw new RuntimeException("Undefined Pomodoro command");
		}
		write();
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
		long endOfWork = DateUtils.getStart(current).getTime()+hourToLong(19);
		long diff = endOfWork - current.getTime();
		int tomato = getTomato()+(int)diff/1000/60/30;
		return calculateProductionScore(tomato);
	}
	
	private long hourToLong(int hour){
		return hour*60*60*1000;
	}

	public int getRandom() {
		// TODO Auto-generated method stub
		return random;
	}

}
