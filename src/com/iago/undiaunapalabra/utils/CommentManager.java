package com.iago.undiaunapalabra.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.util.Log;

public class CommentManager {

	public static void sendComment(String palabra, String frase, String autor) {
		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(Data.insertCommentUrl);

		try {
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("palabra", palabra));
			nameValuePairs.add(new BasicNameValuePair("autor", autor));
			nameValuePairs.add(new BasicNameValuePair("frase", frase));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post Request
			httpclient.execute(httppost);

		} catch (ClientProtocolException e) {
			Log.e("UDUP", e.getMessage());
		} catch (IOException e) {
			Log.e("UDUP", e.getMessage());
		}
	} 

	public static List<Comment> getComments(String palabra) {
		XMLParserSAX xmlParser = null;
		
		try {
			palabra = palabra.replace(" ", "%20");
			xmlParser = new XMLParserSAX(Data.getCommentsUrl + "?palabra=" + palabra);
		} catch (Exception e) {
			Log.e(Utils.tag, e.getMessage());
		}
		
		return xmlParser.parseComments();
	} 
	
	public static List<NumberOfComments> getNumberOfComments() {
		XMLParserSAX xmlParser = null;
		
		try {
			xmlParser = new XMLParserSAX(Data.getNumberOfCommentsUrl);
		} catch (Exception e) {
			Log.e(Utils.tag, e.getMessage());
		}
		
		return xmlParser.parseNumberOfComments();
	} 

}
