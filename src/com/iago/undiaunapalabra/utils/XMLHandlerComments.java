package com.iago.undiaunapalabra.utils;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLHandlerComments extends DefaultHandler {
	private List<Comment> comments = null;
	private Comment currentComment = null;
	private StringBuilder text = null;

	public List<Comment> getComments() {
		return comments;
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		super.characters(ch, start, length);
		if (currentComment != null)
			text.append(ch, start, length);
	}

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		comments = new ArrayList<Comment>();
		text = new StringBuilder();
	}

	@Override
	public void endElement(String uri, String localName, String name)
			throws SAXException {
		super.endElement(uri, localName, name);

		if (comments != null) {
			if (localName.equals("autor")) {
				currentComment.setAutor(text.toString());
			} else if (localName.equals("frase")) {
				currentComment.setFrase(text.toString());
			} else if (localName.equals("fecha")) {
				currentComment.setFecha(text.toString());
			} else if (localName.equals("elemento")) {
				comments.add(currentComment);
			}

			text.setLength(0);
		}
	}

	@Override
	public void startElement(String uri, String localName,
			String name, Attributes attributes) throws SAXException {
		super.startElement(uri, localName, name, attributes);
		if (localName.equals("elemento")) {
			currentComment = new Comment();
		}
	}

}
