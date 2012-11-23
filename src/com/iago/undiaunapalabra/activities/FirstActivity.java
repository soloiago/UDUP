package com.iago.undiaunapalabra.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView.BufferType;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.iago.undiaunapalabra.R;
import com.iago.undiaunapalabra.db.DbHandler;
import com.iago.undiaunapalabra.game.PreGameActivity;
import com.iago.undiaunapalabra.utils.DownloadDialogAsyncTask;
import com.iago.undiaunapalabra.utils.Utils;
import com.iago.undiaunapalabra.wordlist.WordListActivity;

public class FirstActivity extends Activity {
	private Button b1 = null;
	private Button b2 = null;
	private Button b3 = null;
	private Button llaveButton = null;
	private Context ctx = null;
	private DbHandler dbHandler = null;
	private boolean firstTime = true;
	private ProgressDialog dialog = null;
	private AdView adView;

	private static String tag = "UDUP";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		ctx = this;

		adView = new AdView(this, AdSize.BANNER, "a1504ef025dd043");
		LinearLayout layout = (LinearLayout)findViewById(R.id.mainLinear);
		layout.addView(adView);
		adView.loadAd(new AdRequest());

		try {
			dbHandler = new DbHandler(this);
			dbHandler.open();

			Utils.setAutomaticUpdate(ctx);

			if (dbHandler.getNumberOfWords() == 0) {
				dialog = new ProgressDialog(ctx);
				dialog.setMessage("Descargando palabras...");
				dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				dialog.setCancelable(false);			

				new DownloadWords().execute();
			}


		} catch (Exception e) {
			Log.e(tag, e.getMessage());
		}
		
		
	}

	private class DownloadWords extends AsyncTask<Void, Float, Boolean>{

		protected void onPreExecute() {
			dialog.show(); //Mostramos el dialogo antes de comenzar
		}

		protected Boolean doInBackground(Void... params) {
			DbHandler dbHandler = new DbHandler(ctx);
			dbHandler.open();
			return dbHandler.updateWithNewRssFeed();
		}

		protected void onPostExecute(Boolean result) {
			dialog.dismiss();
			configEvents();
		}
	}

	@Override
	public void onStart(){
		super.onStart();

		configEvents();

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
		String orderPreferenceKey = getString(R.string.pref_first_screen_key);
		String firstScreen = prefs.getString(orderPreferenceKey, "menu");

		if (firstTime & firstScreen.equals("word")) {
			firstTime = false;
			Intent i = new Intent(ctx, OneWordActivity.class);
			String word = "";

			try {
				word = dbHandler.getTodayWord();
			} catch (Exception e) {
				Log.e(tag, e.getMessage());
			}
			i.putExtra("name", word);
			startActivity(i);
		}
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (dbHandler != null) {
			dbHandler.close();
		}
		if (adView != null) {
			adView.destroy();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater mi = getMenuInflater();
		mi.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		Intent i = null;
		switch (item.getItemId()) {
		case R.id.itemInfo:
			i = new Intent(this, AboutActivity.class);
			startActivity(i);	
			return true;
		case R.id.itemHowTo:
			i = new Intent(this, HowToActivity.class);
			startActivity(i);	
			return true;
		case R.id.itemHistorico:
			new DownloadDialogAsyncTask(ctx).execute();
			return true;
		}

		return super.onMenuItemSelected(featureId, item);
	}

	public void configEvents() {
		b1 = (Button) findViewById(R.id.imageButtonSelector1);
		b2 = (Button) findViewById(R.id.imageButtonSelector2);
		b3 = (Button) findViewById(R.id.imageButtonSelector3);
		llaveButton = (Button) findViewById(R.id.imageButtonTools);

		b1.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Intent i = new Intent(ctx, OneWordActivity.class);
				String word = dbHandler.getTodayWord();
				i.putExtra("name", word);
				startActivity(i);
			}
		});

		String lastWord = "";

		try {
			lastWord = dbHandler.getLastWord();
		} catch (Exception e) {
			lastWord = "No disponible\nClick para actualizar";
		}

		b1.setText(Html.fromHtml(lastWord), BufferType.SPANNABLE);
		
		b2.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent i = new Intent(ctx, WordListActivity.class);
				startActivity(i);
			}
		});

		b3.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent i = new Intent(ctx, PreGameActivity.class);
				startActivity(i);
			}
		});
		
		llaveButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Intent i = new Intent(ctx, WordPreferences.class);
				startActivity(i);
			}
		});
	}

}
