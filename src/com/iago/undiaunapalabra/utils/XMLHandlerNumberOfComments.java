package com.iago.undiaunapalabra.utils;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLHandlerNumberOfComments extends DefaultHandler {
	private List<NumberOfComments> numberOfComments = null;
	private NumberOfComments currentNumberOfComment = null;
	private StringBuilder text = null;

	public List<NumberOfComments> getNumberOfComments() {
		return numberOfComments;
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		super.characters(ch, start, length);
		if (currentNumberOfComment != null)
			text.append(ch, start, length);
	}

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		numberOfComments = new ArrayList<NumberOfComments>();
		text = new StringBuilder();
	}

	@Override
	public void endElement(String uri, String localName, String name)
			throws SAXException {
		super.endElement(uri, localName, name);

		if (numberOfComments != null) {
			if (localName.equals("palabra")) {
				currentNumberOfComment.setPalabra(text.toString());
			} else if (localName.equals("comments")) {
				currentNumberOfComment.setNumberOfComments(Integer.valueOf(text.toString()));
			} else if (localName.equals("elemento")) {
				numberOfComments.add(currentNumberOfComment);
			}

			text.setLength(0);
		}
	}

	@Override
	public void startElement(String uri, String localName,
			String name, Attributes attributes) throws SAXException {
		super.startElement(uri, localName, name, attributes);
		if (localName.equals("elemento")) {
			currentNumberOfComment = new NumberOfComments();
		}
	}

}
