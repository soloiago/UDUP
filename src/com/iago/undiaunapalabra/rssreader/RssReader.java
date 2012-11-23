package com.iago.undiaunapalabra.rssreader;

import java.util.List;

import android.util.Log;


public class RssReader {
	
	public static String getLatestRssFeed(){
		String feed = "http://feeds.feedburner.com/UnDiaUnaPalabra?format=xml";
		
		
		RSSHandler rssHandler = new RSSHandler();
		List<Word> words =  rssHandler.getLatestWords(feed);
		Log.e("RSS ERROR", "Number of words " + words.size());
		return fillData(words);
	}
	
	private static String fillData(List<Word> words) {
		String out = "";
		
        for (Word word : words) {
        	out += word.getName() + "\n" + word.getDefinition() + "\n" + word.getDateString() + "\n\n";
        }
        
        return out;
	}

	public static List<Word> getLatestWordList() {
		String feed = "http://feeds.feedburner.com/UnDiaUnaPalabra?format=xml";
		
		RSSHandler rssHandler = new RSSHandler();
		List<Word> words =  rssHandler.getLatestWords(feed);
		Log.e("RSS ERROR", "Number of words " + words.size());
		
		return words;
	}

}
