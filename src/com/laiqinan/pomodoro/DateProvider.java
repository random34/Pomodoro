package com.laiqinan.pomodoro;

import java.util.Date;

public class DateProvider {

	private boolean provideCurrent = true;
	private Date date = null;
	
	public Date getDate() {
		if (provideCurrent){
			date = new Date();
		}else if (date==null){
			throw new RuntimeException("Date have not been set");
		}
		return date;
	}
	
	public void setDate(Date d){
		date = d;
	}

	public void setProvideCurrentDate(boolean b) {
		provideCurrent = b;
	}

}
