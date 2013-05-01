package com.laiqinan.pomodoro;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CommandLineInterface {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		File file = new File("save.txt");
		if (!file.exists()) {
			PrintWriter pw;
			try {
				pw = new PrintWriter(file);
				pw.println(new SimpleDateFormat(PomodoroWork.DATE_FORMAT)
						.format(new Date()));
				pw.println(0);
				pw.println(0);
				pw.println(0);
				pw.println("no arrive date");
				pw.close();
				System.out.println("new file created.");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		PomodoroWork po = new PomodoroWork(file.getName());
		// if (args.length==0){
		// System.out.println("No arguments!");
		// po.execute("-");
		// }else if (args.length==1 &&
		// (args[0].equals("print")||args[0].equals("-"))
		// || args.length==2 && (args[0]))
		po.execute(args);
		if (args[0].equals("random")) {
			System.out.println("Generated random number:  " + po.getRandom());
		} else {
			po.print();
		}
	}

}
