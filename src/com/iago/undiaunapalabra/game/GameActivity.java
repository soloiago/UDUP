package com.iago.undiaunapalabra.game;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.iago.undiaunapalabra.R;

public class GameActivity extends Activity {
	private int question;
	private GameEngine gameEngine;
	private Context ctx;
	private int userAnswer = 4; //Le damos un valor fuera de rango para detectar que no se ha marcado ninguna respuesta
	private List<Question> questionList;
	private AdView adView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game);

		ctx = this;

		configAdMob();

		gameEngine = GameEngine.getInstance();

		question = getIntent().getExtras().getInt("question");

		questionList = gameEngine.getQuestionList();

		TextView text = (TextView) findViewById(R.id.textViewQuestion);
		TextView textContador = (TextView) findViewById(R.id.textContadorPreguntas);
		RadioButton clue0 = (RadioButton) findViewById(R.id.radioClue0);
		RadioButton clue1 = (RadioButton) findViewById(R.id.radioClue1);
		RadioButton clue2 = (RadioButton) findViewById(R.id.radioClue2);
		Button siguiente = (Button) findViewById(R.id.buttonSiguiente);
		RadioGroup answer = (RadioGroup) findViewById(R.id.radioGroupAnswer);

		text.setText(questionList.get(question).getWord());
		textContador.setText(" (" + Integer.toString(question+1)+ "/" + Integer.toString(GameEngine.getMaxNumOfWords()) + ")");
		clue0.setText(Html.fromHtml(questionList.get(question).getClues().get(0)),BufferType.SPANNABLE);
		clue1.setText(Html.fromHtml(questionList.get(question).getClues().get(1)),BufferType.SPANNABLE);
		clue2.setText(Html.fromHtml(questionList.get(question).getClues().get(2)),BufferType.SPANNABLE);

		siguiente.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (userAnswer == 4) {
					showDialog(0);
					return;
				}
				
				if (questionList.get(question).getRightAnswer() == userAnswer) {
					GameResult.getInstance().addHit();
				} else {
					GameResult.getInstance().addMiss();
				}

				if (question < GameEngine.getMaxNumOfWords() - 1) {
					Intent i = new Intent(ctx, GameActivity.class);
					i.putExtra("question", ++question);
					startActivity(i);
				} else {
					Intent i = new Intent(ctx, GameResultActivity.class);
					startActivity(i);
				}
			}
		});

		answer.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.radioClue0:
					userAnswer = 0;
					break;

				case R.id.radioClue1:
					userAnswer = 1;
					break;

				case R.id.radioClue2:
					userAnswer = 2;
					break;
				}

			}
		});
	}

	private void configAdMob() {
		adView = new AdView(this, AdSize.BANNER, "a1504ef025dd043");
		LinearLayout layout = (LinearLayout)findViewById(R.id.linearLayoutGame);
		layout.addView(adView);
		adView.loadAd(new AdRequest());
	}
	
	@Override
    protected Dialog onCreateDialog(int id) {
    	Dialog dialogo = null;

		dialogo = crearDialogoAlerta();
    
    	return dialogo;
    }

	@Override
	public void onPause() {
		super.onPause();
		finish();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	private Dialog crearDialogoAlerta() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Ninguna respuesta");
		builder.setMessage("Debe seleccionar una respuesta");
		builder.setPositiveButton("OK", new Dialog.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		return builder.create();
	}

}
