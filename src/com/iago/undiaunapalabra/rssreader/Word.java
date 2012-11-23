package com.iago.undiaunapalabra.rssreader;

import android.util.Log;

public class Word {
	private String name = "";
	private String definition = "";
	private long dateMs = 0;
	private String dateString = "";
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDefinition() {
		return definition;
	}
	public void setDefinition(String definition) {
		this.definition = definition;
	}
	public long getDateMs() {
		return dateMs;
	}
	public void setDateMs(long dateMs) {
		this.dateMs = dateMs;
		Log.i("DATE", String.valueOf(dateMs));
	}
	public String getDateString() {
		return dateString;
	}
	public void setDateString(String dateString) {
		this.dateString = dateString;
	}
	
}
