package com.iago.undiaunapalabra.utils;

import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.iago.undiaunapalabra.receivers.OnAlarmReceiver;

/**
 * 
 * @author iago
 *
 */
public class Utils {
	public static final String tag = "UDUP";
	
	public static long getlDateMs(String date) {
		String[] dateSplitted = date.split("\\s+");

		GregorianCalendar fecha = new GregorianCalendar(Integer.valueOf(dateSplitted[3]), 
				getMonthInt(dateSplitted[2]), 
				Integer.valueOf(dateSplitted[1]));

		return fecha.getTimeInMillis();
	}

	public static String getDateString(String date) {
		String[] dateSplitted = date.split("\\s+");
		return dateSplitted[1] + " " + dateSplitted[2] + " " + dateSplitted[3];
	}

	private static int getMonthInt(String month) {
		int out = 100;

		if (month.equals("Jan")) {
			out = 0;
		} else if (month.equals("Feb")) {
			out = 1;
		} else if (month.equals("Mar")) {
			out = 2;
		} else if (month.equals("Apr")) {
			out = 3;
		} else if (month.equals("May")) {
			out = 4;
		} else if (month.equals("Jun")) {
			out = 5;
		} else if (month.equals("Jul")) {
			out = 6;
		} else if (month.equals("Aug")){
			out = 7;
		} else if (month.equals("Sep")) {
			out = 8;
		} else if (month.equals("Oct")) {
			out = 9;
		} else if (month.equals("Nov")) {
			out = 10;
		} else if (month.equals("Dec")) {
			out = 11;
		}

		return out;
	}

	public static List<Integer> selectRandomNumbers(int maxNumber)
	{
		List<Integer> numberList = new LinkedList<Integer>();
		for (int i = 1; i <= maxNumber; i++)
		{
			numberList.add(i-1);
		}
		Collections.shuffle(numberList);
		numberList = numberList.subList(0, maxNumber);
		return numberList;
	}

	public static void setAutomaticUpdate(Context ctx) {
		AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);

		Intent intent = new Intent(ctx, OnAlarmReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx, 0, intent, 1);

		//alarmManager.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis() + (5 * 1000), pendingIntent);
		Calendar calendar = Calendar.getInstance();

		Log.i("UDUP", "Curret: Day: " + calendar.get(Calendar.DAY_OF_MONTH) + 
				"; Hora: " + calendar.get(Calendar.HOUR_OF_DAY) + 
				"; Minutos: " + calendar.get(Calendar.MINUTE));

		final int UPDATE_HOUR = 8;

		int hour = calendar.get(Calendar.HOUR_OF_DAY);

		if (hour < UPDATE_HOUR) {
			calendar.add(Calendar.HOUR_OF_DAY, UPDATE_HOUR - 1 - hour);
		} else {
			calendar.add(Calendar.HOUR_OF_DAY, UPDATE_HOUR + (24 - hour - 1));
		}

		int minute = calendar.get(Calendar.MINUTE);
		calendar.add(Calendar.MINUTE, 60 - minute);

		Log.i("UDUP", "Siguiente actualización: Day: " + calendar.get(Calendar.DAY_OF_MONTH) + 
				"; Hora: " + calendar.get(Calendar.HOUR_OF_DAY) + 
				"; Minutos: " + calendar.get(Calendar.MINUTE));


		//calendar.set(Calendar.YEAR, Calendar.MONTH, day, hourOfDay, minute)
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000*60*60*24, pendingIntent);
	}
}
