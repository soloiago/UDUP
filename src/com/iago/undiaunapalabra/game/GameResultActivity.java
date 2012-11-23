package com.iago.undiaunapalabra.game;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.iago.undiaunapalabra.R;
import com.iago.undiaunapalabra.db.DbHandler;

public class GameResultActivity extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gameresult);
		
		Integer hits = Integer.valueOf((GameResult.getInstance().getHits()));
		Integer miss = Integer.valueOf(GameResult.getInstance().getMiss());
		
		Float porcentaje = (float) (100.0 / GameEngine.getMaxNumOfWords());
		
		Float aciertos = Float.valueOf(porcentaje * hits);
		Float fallos = Float.valueOf(porcentaje * miss);
		
		TextView textHits = (TextView) findViewById(R.id.textHitsResult);
		textHits.setText("Aciertos: " + hits.toString() +
				" (" + aciertos.toString() + "%)");
		
		TextView textMiss = (TextView) findViewById(R.id.textMissResult);
		textMiss.setText("Errores: " + miss.toString() +
				" (" + fallos.toString() + "%)");
		
		DbHandler dbHandler = new DbHandler(this);
		dbHandler.open();
		dbHandler.saveHits(hits);
		dbHandler.saveTotal(hits + miss);
	}

}
