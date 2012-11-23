package com.iago.undiaunapalabra.wordlist;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.iago.undiaunapalabra.R;
import com.iago.undiaunapalabra.activities.OneWordActivity;
import com.iago.undiaunapalabra.activities.WordPreferences;
import com.iago.undiaunapalabra.db.DbHandler;
import com.iago.undiaunapalabra.utils.CommentManager;
import com.iago.undiaunapalabra.utils.NumberOfComments;
import com.iago.undiaunapalabra.wordlist.WordListAdapter.WordHolder;

public class WordListActivity extends ListActivity {
	private DbHandler dbHandler;
	private ProgressDialog dialog;
	private Context ctx;
	private EditText filterText;
	private ImageButton searchButton;
	private ArrayAdapter<WordAdapter> adapter;
	private ProgressBar progressBar;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ctx = this;

		setContentView(R.layout.mainlist);
		
		registerForContextMenu(getListView());

		dbHandler = new DbHandler(ctx);
		dbHandler.open();
		
		if (dbHandler.getTodayWord().equals("")) {
			dialog = new ProgressDialog(ctx);
			dialog.setMessage("Descargando nuevas palabras...");
			dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			dialog.setCancelable(false);
			new DownloadWords().execute();
		}
		else {
			showWordList();
		}
		
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		new DownloadNumberOfComments().execute();
		
		searchButton = (ImageButton) findViewById(R.id.imageButtonSearch);
		searchButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				searchButton.setVisibility(ImageButton.GONE);
				filterText.setVisibility(EditText.VISIBLE);
			}
		});
		
		filterText = (EditText) findViewById(R.id.editTextSearch);
	    filterText.addTextChangedListener(filterTextWatcher);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater mi = getMenuInflater();
		mi.inflate(R.menu.menulist, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		Intent i = null;
		switch (item.getItemId()) {
		case R.id.itemConfig:
			i = new Intent(this, WordPreferences.class);
			startActivity(i);	
			return true;
		}

		return super.onMenuItemSelected(featureId, item);
	}
	
	private TextWatcher filterTextWatcher = new TextWatcher() {

	    public void afterTextChanged(Editable s) {
	    }

	    public void beforeTextChanged(CharSequence s, int start, int count,
	            int after) {
	    }

	    public void onTextChanged(CharSequence s, int start, int before,
	            int count) {
	        adapter.getFilter().filter(s);
	    }

	};

	@Override
	protected void onDestroy() {
	    super.onDestroy();
		if (dbHandler != null) {
			dbHandler.close();
		}
	    filterText.removeTextChangedListener(filterTextWatcher);
	}
	
	private void showWordList() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx); 
		String orderPreferenceKey = getString(R.string.pref_order_key);
		String order = prefs.getString(orderPreferenceKey, DbHandler.KEY_DATE_MS + " DESC");
		
		Cursor cursorWords = dbHandler.fetchAllWords(order);
		startManagingCursor(cursorWords);
		
		List<WordAdapter> words = new ArrayList<WordAdapter>();
		
		if (cursorWords.moveToFirst()) {
		     //Recorremos el cursor hasta que no haya más registros
		     do {
		          String name = cursorWords.getString(0);
		          String date = cursorWords.getString(1);
		          float rating = cursorWords.getFloat(2);
		          int comments = cursorWords.getInt(3);
		          WordAdapter word = new WordAdapter();
		          word.setWord(name);
		          word.setDate(date);
		          word.setRating(rating);
		          word.setComments(comments);
		          words.add(word);
		     } while(cursorWords.moveToNext());
		}
		
		adapter = new WordListAdapter(this, R.layout.listpresentation, words);
		
		setListAdapter(adapter);
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		showWordList();
		String text = filterText.getText().toString();
		adapter.getFilter().filter(text);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent i = new Intent(this, OneWordActivity.class);
		WordHolder wh = (WordHolder) v.getTag();
		String name = (String) wh.word.getText();
		i.putExtra("name", name);
		startActivity(i);
	}
	
	private class DownloadWords extends AsyncTask<Void, Float, Boolean>{

		protected void onPreExecute() {
			dialog.show(); //Mostramos el diálogo antes de comenzar
		}

		protected Boolean doInBackground(Void... params) {
			Boolean update = false;
			if (dbHandler.updateWithNewRssFeed()) {
				update = true;
			}
			return update;
		}

		protected void onPostExecute(Boolean update) {
			dialog.dismiss(); //Cerremos el diálogo
			if (update) {
				Toast.makeText(ctx, "Lista de palabras actualizada correctamente", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(ctx, "No hay ninguna actualización", Toast.LENGTH_SHORT).show();
			}
			showWordList();
		}
	}
	
	private class DownloadNumberOfComments extends AsyncTask<Void, Float, Boolean>{

		protected void onPreExecute() {
			progressBar.setVisibility(ProgressBar.VISIBLE);
		}

		protected Boolean doInBackground(Void... params) {
			Boolean update = true;
			List<NumberOfComments> numberOfComments = CommentManager.getNumberOfComments();
			
			for (int i = 0; i < numberOfComments.size(); i++) {
				dbHandler.updateNumberOfComments(numberOfComments.get(i).getPalabra(), 
						numberOfComments.get(i).getNumberOfComments());
			}
			
			return update;
		}

		protected void onPostExecute(Boolean update) {
			progressBar.setVisibility(ProgressBar.GONE);
			showWordList();
		}
	}

}
