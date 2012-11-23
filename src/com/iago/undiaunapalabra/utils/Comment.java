package com.iago.undiaunapalabra.utils;

public class Comment {
	private String autor;
	private String frase;
	private String fecha;
	
	public String getAutor() {
		return autor;
	}
	
	public void setAutor(String autor) {
		this.autor = autor;
	}
	
	public String getFrase() {
		return frase;
	}
	
	public void setFrase(String frase) {
		this.frase = frase;
	}
	
	public String getFecha() {
		return fecha;
	}
	
	public void setFecha(String fecha) {
		this.fecha = fecha;
	}
	
	@Override public boolean equals(Object com){ 
		Comment comment = (Comment) com;
		
		if (this.autor.equals(comment.getAutor()) && this.frase.equals(comment.getFrase()) && this.fecha.equals(comment.getFecha()))
			return true;
		else
			return false;
	}

}
