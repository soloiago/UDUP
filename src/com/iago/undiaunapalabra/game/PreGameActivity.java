package com.iago.undiaunapalabra.game;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.iago.undiaunapalabra.R;
import com.iago.undiaunapalabra.db.DbHandler;

public class PreGameActivity extends Activity {
	private SeekBar seekBar;
	private TextView textNumberOfQuestions;
	private Button startGameButton;
	private Button resetStatsButton;
	private CheckBox checkBoxMax;
	private TextView textHits;
	private TextView textMiss;
	private TextView textTotal;
	private AdView adView;
	
	private final String maxQuestionOnTag = "maxQuestionOn";
	
	private SharedPreferences.Editor editor;

	private Activity ctx = null;
	private DbHandler dbHandler = null;

	private final int min = 5;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pregame);

		ctx = this;
		
		configAdMob();

		dbHandler = new DbHandler(this);
		dbHandler.open();

		seekBar = (SeekBar) findViewById(R.id.seekBarNumberOfQuestions);
		textNumberOfQuestions = (TextView) findViewById(R.id.numberOfQuestions);
		startGameButton = (Button) findViewById(R.id.buttonStartGame);
		resetStatsButton = (Button) findViewById(R.id.buttonResetStats);
		checkBoxMax = (CheckBox) findViewById(R.id.checkBoxMax);
		textHits = (TextView) findViewById(R.id.textHits);
		textMiss = (TextView) findViewById(R.id.textMiss);
		textTotal = (TextView) findViewById(R.id.textTotal);
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
		editor = prefs.edit(); 
		int maxQuestionPrefs = prefs.getInt("maxQuestions", 5);
		boolean maxQuestionOnPref = prefs.getBoolean(maxQuestionOnTag, false);
		
		int currentMaxQuestions = dbHandler.getNumberOfWords();
		seekBar.setMax(currentMaxQuestions - min - 2);
		
		if (maxQuestionOnPref) {
			seekBar.setProgress(seekBar.getMax());
			seekBar.setEnabled(false);
			checkBoxMax.setChecked(true);
		} else {
			seekBar.setProgress(maxQuestionPrefs - 5);
			checkBoxMax.setChecked(false);
		}
		
		textNumberOfQuestions.setText(Integer.toString(seekBar.getProgress() + min));
		
		seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			public void onStopTrackingTouch(SeekBar seekBar) {
				textNumberOfQuestions.setText(Integer.toString(seekBar.getProgress() + min));
			}

			public void onStartTrackingTouch(SeekBar seekBar) {
				textNumberOfQuestions.setText(Integer.toString(seekBar.getProgress() + min));
			}

			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				textNumberOfQuestions.setText(Integer.toString(seekBar.getProgress() + min));
			}
		});

		startGameButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				int maxQuestions = seekBar.getProgress() + min;

				GameEngine gameEngine = new GameEngine(ctx);

				editor.putInt("maxQuestions", maxQuestions);
				editor.putBoolean(maxQuestionOnTag, checkBoxMax.isChecked());
				editor.commit();

				GameEngine.setMaxNumOfWords(maxQuestions);
				gameEngine.generateGameList();

				GameResult.getInstance().setHits(0);
				GameResult.getInstance().setMiss(0);

				Intent i = new Intent(ctx, GameActivity.class);
				i.putExtra("question", (int)0);
				startActivity(i);

			}
		});

		resetStatsButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				dbHandler.resetStats();
				showStats();
			}
		});

		checkBoxMax.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					seekBar.setProgress(seekBar.getMax());
					seekBar.setEnabled(false);
					
				} else {
					seekBar.setEnabled(true);
				}
			}
		});

		showStats();

	}
	
	private void configAdMob() {
		adView = new AdView(this, AdSize.BANNER, "a1504ef025dd043");
		LinearLayout layout = (LinearLayout)findViewById(R.id.preGameLinear);
		layout.addView(adView);
		adView.loadAd(new AdRequest());
	}

	private void showStats() {
		int hits = dbHandler.getHits();
		int total = dbHandler.getTotal();
		int miss = total - hits;

		Float porcentaje = (float) (100.0 / total);

		Float aciertos = Float.valueOf(porcentaje * hits);
		Float fallos = Float.valueOf(porcentaje * miss);
		
		if (total == 0) {
			textHits.setText("Aciertos: 0");
			textMiss.setText("Errores: 0");
		} else {
			textHits.setText("Aciertos: " + Integer.toString(hits) +
					" (" + aciertos.toString() + "%)");

			textMiss.setText("Errores: " + Integer.toString(miss) +
					" (" + fallos.toString() + "%)");
		}

		textTotal.setText("Total: " + Integer.toString(total));
	}

	@Override
	public void onPause() {
		super.onPause();
		dbHandler.close();
		finish();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
}
