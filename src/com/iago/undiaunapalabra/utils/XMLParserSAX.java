package com.iago.undiaunapalabra.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;

public class XMLParserSAX {

	private URL rssUrl;

	public XMLParserSAX(String url)
	{
		try
		{
			rssUrl = new URL(url);
		}
		catch (MalformedURLException e)
		{
			throw new RuntimeException(e);
		}
	}

	public List<Comment> parseComments()
	{
		SAXParserFactory factory = SAXParserFactory.newInstance();
		InputStream inputStream = null;
		
		try
		{
			SAXParser parser = factory.newSAXParser();
			XMLHandlerComments handler = new XMLHandlerComments();
			
			InputSource is = new InputSource(rssUrl.toString());
			is.setEncoding("ISO-8859-1");
			
			parser.parse(is, handler);
			return handler.getComments();
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public List<NumberOfComments> parseNumberOfComments()
	{
		SAXParserFactory factory = SAXParserFactory.newInstance();
		InputStream inputStream = null;
		
		try
		{
			SAXParser parser = factory.newSAXParser();
			XMLHandlerNumberOfComments handler = new XMLHandlerNumberOfComments();
			
			InputSource is = new InputSource(rssUrl.toString());
			is.setEncoding("ISO-8859-1");
			
			parser.parse(is, handler);
			return handler.getNumberOfComments();
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	private InputStream getInputStream()
	{
		try
		{
			return rssUrl.openConnection().getInputStream();
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

}
