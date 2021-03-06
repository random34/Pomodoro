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

	private static final String PROPERTIES = ".properties";

	private static final String LAST_SAVE_TIME = "lastSaveTime";

	private static final String NOT_BEEN_SET = "NOT BEEN SET";

	private static final String ARRIVE_TIME = "arriveTime";

	private static final String TIME_STRING = "time";

	public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

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

	private int random = 0;

	public PomodoroWork(String fileName) {
		DateProvider dp = new DateProvider();
		dp.setProvideCurrentDate(true);
		initialise(fileName, dp);
	}

	public PomodoroWork(String string, DateProvider dp) {
		initialise(string, dp);
	}

	private void initialise(String string, DateProvider dp) {
		this.dateProvider = dp;
		file = new File(string);
		if (!file.exists()) {
			createNewPropertyFile(string);
		}
		read();
	}

	private String arriveDateString = "";

	public void createNewPropertyFile(String fileName) {
		Properties prop = new Properties();
		prop.setProperty(TIME_STRING, "0");
		prop.setProperty(ARRIVE_TIME, NOT_BEEN_SET);
		prop.setProperty(LAST_SAVE_TIME, formatDateProvider());
		try {
			prop.store(new FileOutputStream(fileName), null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private String formatDateProvider() {
		return new SimpleDateFormat(DATE_FORMAT).format(dateProvider.getDate());
	}

	private void write() {
		if (file.getName().endsWith(PROPERTIES))
			writeToProperties();
	}

	private void writeToProperties() {
		Properties prop = new Properties();
		prop.setProperty(TIME_STRING, String.valueOf(time));
		prop.setProperty(ARRIVE_TIME, arriveDateString);
		prop.setProperty(LAST_SAVE_TIME, formatDateProvider());
		try {
			prop.store(new FileOutputStream(file), null);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void read() {
		if (file.getName().endsWith(PROPERTIES))
			readFromProperties();
		else {
			throw new RuntimeException("unsupported file type");
		}
	}

	private void readFromProperties() {
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(file));

			String lastSaveTimeString = prop.getProperty(LAST_SAVE_TIME);
			// TODO refactor
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
					PomodoroWork.DATE_FORMAT);
			Date lastOpenDate = simpleDateFormat.parse(lastSaveTimeString);
			Date current = dateProvider.getDate();
			if (DateUtils.isSameDay(lastOpenDate, current)) {
				time = Integer.parseInt(prop.getProperty(TIME_STRING, "0"));
				arriveDateString = prop.getProperty(ARRIVE_TIME);
			}else{
				time = 0;
				arriveDateString = NOT_BEEN_SET;
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void execute(String[] parameters) {

		String verb = parameters[0];

		if (isTimeCommand(verb)) {
			checkArgumentsSize(parameters, 2, "time need one argument");
			int addTime = Integer.parseInt(parameters[1]);
			time += addTime;
		} else if (isAddCommand(verb)) {
			checkArgumentsSize(parameters, 2, "add need one argument");
			int addTomato = Integer.parseInt(parameters[1]);
			time += addTomato * 25;
		} 
		else if (verb.equals("-") || verb.equals("print")) {

		} else if (isRandomCommand(verb)) {
			checkArgumentsSize(parameters, 3, "random need one argument");
			random = genRandom(Integer.parseInt(parameters[1]),
					Integer.parseInt(parameters[2]));
		} else if (isStartCommand(verb)) {
			if (parameters.length == 1)
				arriveDateString = formatDateProvider();
			else if (parameters.length == 2)
				arriveDateString = parameters[1];
			else
				throw new IllegalArgumentException(
						"start need one or zero argument");
		} else {
			throw new RuntimeException("Undefined Pomodoro command");
		}
		write();
	}

	public static boolean isStartCommand(String verb) {
		return "start".equals(verb)||"s".equals(verb);
	}

	public static boolean isRandomCommand(String verb) {
		return "random".equals(verb) || "r".equals(verb);
	}

	public static boolean isAddCommand(String verb) {
		return "add".equals(verb) || "a".equals(verb);
	}

	public static boolean isTimeCommand(String verb) {
		return TIME_STRING.equals(verb)|| "t".equals(verb);
	}

	private void checkArgumentsSize(String[] parameters, int size,
			String message) {
		if (parameters.length != size) {
			throw new IllegalArgumentException(message);
		}
	}

	private int genRandom(int start, int end) {
		Random rand = new Random();
		return rand.nextInt(end - start + 1) + start;
	}

	public void execute(String string) {
		String[] parameters = string.split(" ");
		execute(parameters);
	}

	@Override
	public String toString() {
		return "PomodoroWork [time=" + time + "]";
	}

	public void print() {
		System.out.println("tomato:\t\t" + getTomato());
		System.out.println("left time:\t" + getLeftTime());
		System.out
				.println("current production rate: \t" + getProductionScore());
		System.out.println("arrive time: " + arriveDateString);
		if (hasLargeBreak())
			System.out.println("Good, you can have a break!!!!!");
		else
			System.out.println("You can still achieve production score "
					+ getMaxProductionRate() + " !!!");
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
		if (tomato > 12 || tomato == 12 && getLeftTime() > 0)
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
		long estimated = DateUtils.getStart(current).getTime() + hourToLong(10);

		estimated += getTomato() * 40 * 60 * 1000;

		if (isAfternoon(current)) {
			estimated += hourToLong(1);
		}

		return (int) (estimated - current.getTime()) / 60 / 1000;
	}

	private boolean isAfternoon(Date current) {
		return current.getTime() - DateUtils.getStart(current).getTime()
				- hourToLong(14) > 0;
	}

	public boolean hasLargeBreak() {
		return getEstimateRest() >= 25;
	}

	public int getMaxProductionRate() {
		Date current = dateProvider.getDate();
		// endOfWork = the start of the day (0:00:00)+ 19 hours (so I left
		// 19:00).
		long endOfWork = DateUtils.getStart(current).getTime() + hourToLong(19);
		long diff = endOfWork - current.getTime();
		int tomato = getTomato() + (int) diff / 1000 / 60 / 30;
		return calculateProductionScore(tomato);
	}

	private long hourToLong(int hour) {
		return hour * 60 * 60 * 1000;
	}

	public int getRandom() {
		return random;
	}

	public String getArriveTimeString() {
		return arriveDateString;
	}

}
