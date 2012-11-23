package com.iago.undiaunapalabra.game;

public class GameResult {
	static GameResult gameResult;
	int hits = 0;
	int miss = 0;
	
	public static GameResult getInstance() {
		if (gameResult == null) {
			gameResult = new GameResult();
		}
		return gameResult;
	}
	
	public void addHit() {
		hits++;
	}
	
	public void addMiss() {
		miss++;
	}

	public int getHits() {
		return hits;
	}

	public int getMiss() {
		return miss;
	}

	public void setHits(int hits) {
		this.hits = hits;
	}

	public void setMiss(int miss) {
		this.miss = miss;
	}
}
