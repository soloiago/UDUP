package com.iago.undiaunapalabra.game;

import java.util.List;

public class Question {
	private String Word;
	private List<String> clues;
	//Hasta que se desordene, la respuesta correcta es la primera
	private int RightAnswer = 0;
	
	public String getWord() {
		return Word;
	}
	public void setWord(String word) {
		Word = word;
	}
	public List<String> getClues() {
		return clues;
	}
	public void setClues(List<String> clues) {
		this.clues = clues;
	}
	public int getRightAnswer() {
		return RightAnswer;
	}
	public void setRightAnswer(int rightAnswer) {
		RightAnswer = rightAnswer;
	}
}
