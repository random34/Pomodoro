package com.laiqinan.pomodoro;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CommandLineInterface {

	private static final String FILE_NAME = "pomodoro-data/save.properties";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		File file = new File(FILE_NAME);
//		if (!file.exists()) {
//			PrintWriter pw;
//			try {
//				pw = new PrintWriter(file);
//				pw.println(new SimpleDateFormat(PomodoroWork.DATE_FORMAT)
//						.format(new Date()));
//				pw.println(0);
//				pw.println(0);
//				pw.println(0);
//				pw.println("no arrive date");
//				pw.close();
//				System.out.println("new file created.");
//			} catch (FileNotFoundException e) {
//				e.printStackTrace();
//			}
//		}

		PomodoroWork po = new PomodoroWork(FILE_NAME);
		po.execute(args);
		if (PomodoroWork.isRandomCommand(args[0])) {
			System.out.println("Generated random number:  " + po.getRandom());
		} else {
			po.print();
		}
	}

}
