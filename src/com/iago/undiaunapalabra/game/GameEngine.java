package com.iago.undiaunapalabra.game;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.iago.undiaunapalabra.db.DbHandler;
import com.iago.undiaunapalabra.db.WordAndDefinition;
import com.iago.undiaunapalabra.utils.Utils;

public class GameEngine {
	private static GameEngine gameEngine;
	private List<Question> questionList;
	private List<WordAndDefinition> wordAndDefinitionList;
	private DbHandler dbHandler;
	private static int maxNumOfWords;
	
	public static int getMaxNumOfWords() {
		return maxNumOfWords;
	}

	public static void setMaxNumOfWords(int maxNumOfWords) {
		GameEngine.maxNumOfWords = maxNumOfWords;
	}

	public GameEngine(Context ctx) {
		gameEngine = this;
		dbHandler = new DbHandler(ctx);
	}

	public void generateGameList() {
		questionList = new ArrayList<Question>();
		wordAndDefinitionList = new ArrayList<WordAndDefinition>();
		dbHandler.open();
		wordAndDefinitionList = dbHandler.getWordAndDefinitionList();

		//Desordenamos la lista para cambiar la primera palabra
		disorderList();
				
		for (int i = 0; i < maxNumOfWords; i++) {
			getQuestion();
		}
		disorderQuestions();

		//Ojo cuidado - dbHandler.close();
	}

	private void getQuestion() {
		getWordAndClues(wordAndDefinitionList);
	}

	private void disorderQuestions() {
		List<Question> newQuestionList = new ArrayList<Question>();
		
		for (int i = 0; i < maxNumOfWords; i++) {
			List<Integer> newOrder = Utils.selectRandomNumbers(3);
			Question question = new Question();
			question.setWord(questionList.get(i).getWord());
			List<String> clues = new ArrayList<String>();
			for (int j = 0; j < 3; j++) {
				clues.add(questionList.get(i).getClues().get(newOrder.get(j)));
				if (newOrder.get(j)==0) {
					question.setRightAnswer(j);
				}
			}
			question.setClues(clues);
			newQuestionList.add(question);
		}
		questionList = newQuestionList;
		
	}

	private void getWordAndClues(List<WordAndDefinition> currentWordList) {
		//Cogemos la primera palabra y las 3 primeras definiciones
		Question question = new Question();
		question.setWord(wordAndDefinitionList.get(0).getWord());
		List<String> clues = new ArrayList<String>();
		clues.add(wordAndDefinitionList.get(0).getDefinition());
		clues.add(wordAndDefinitionList.get(1).getDefinition());
		clues.add(wordAndDefinitionList.get(2).getDefinition());
		question.setClues(clues);
		questionList.add(question);
		
		//Eliminamos el primero para que no vuelva a aparecer
		wordAndDefinitionList.remove(0);
		
		//Desordenamos la lista para que cambiar el orden de las pistas
		disorderList();
	}

	public List<Question> getQuestionList() {
		return questionList;
	}

	private void disorderList() {
		List<WordAndDefinition> newWordAndDefinitionList = new ArrayList<WordAndDefinition>(); 
		
		List<Integer> newOrder = Utils.selectRandomNumbers(wordAndDefinitionList.size());
		for (int i = 0; i < newOrder.size(); i++) {
			newWordAndDefinitionList.add(wordAndDefinitionList.get(newOrder.get(i)));
		}
		
		wordAndDefinitionList = newWordAndDefinitionList;
	}

	public static GameEngine getInstance() {
		return gameEngine;
	}
	
}
