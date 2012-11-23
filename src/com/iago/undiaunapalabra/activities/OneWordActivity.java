package com.iago.undiaunapalabra.activities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.widget.Toast;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.iago.undiaunapalabra.R;
import com.iago.undiaunapalabra.db.DbHandler;
import com.iago.undiaunapalabra.utils.Comment;
import com.iago.undiaunapalabra.utils.CommentManager;
import com.iago.undiaunapalabra.utils.Utils;

public class OneWordActivity extends Activity{
	private static final int DIALOG_TEXT_ENTRY = 1;
	private static final int DIALOG_RATING_ENTRY = 2;

	private TextView texto = null;
	private TextView fecha = null;
	private RatingBar rating = null;
	private float puntuacion;
	private ProgressBar progressBar = null;

	private DbHandler dbHandler = null;
	private Context ctx = null;

	private ProgressDialog dialog = null;

	private AdView adView;

	private String palabra;
	private String definicion;

	private List<Comment> listComments = null;
	private List<Comment> listOldComments = new ArrayList<Comment>();

	private SharedPreferences preferences = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.oneword);
		ctx = this;

		preferences = PreferenceManager.getDefaultSharedPreferences(ctx);

		dbHandler = new DbHandler(ctx);
		dbHandler.open();

		findViewById(R.id.textView1).requestFocus();

		configAdMob();

		getViews();

		configEvents();

		setContent();
	}

	private void configAdMob() {
		adView = new AdView(this, AdSize.BANNER, "a1504ef025dd043");
		LinearLayout layout = (LinearLayout)findViewById(R.id.oneWordLinear);
		layout.addView(adView);
		adView.loadAd(new AdRequest());
	}

	private void getViews() {
		texto = (TextView) findViewById(R.id.texto);
		fecha = (TextView) findViewById(R.id.fecha);
		rating = (RatingBar) findViewById(R.id.ratingBarGrande);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
	}

	private void configEvents() {
		rating.setEnabled(false);

		findViewById(R.id.buttonGuardarPuntuacion).setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				showDialog(DIALOG_RATING_ENTRY);
			}
		});

		findViewById(R.id.buttonShare).setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				Intent sendIntent = new Intent();
				sendIntent.setAction(Intent.ACTION_SEND);
				sendIntent.putExtra(Intent.EXTRA_TEXT, palabra + ":\n" + definicion);
				sendIntent.setType("text/plain");
				startActivity(sendIntent);
			}
		});

		findViewById(R.id.buttonEnviarFrase).setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				showDialog(DIALOG_TEXT_ENTRY);
			}
		});

	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		LayoutInflater factory = null;

		switch (id) {
		case DIALOG_TEXT_ENTRY:
			// This example shows how to add a custom layout to an AlertDialog
			factory = LayoutInflater.from(ctx);
			final View textEntryView = factory.inflate(R.layout.dialog_text_entry, null);
			String keyAuthor = getString(R.string.author_key);
			String author = preferences.getString(keyAuthor, "");

			EditText editTextAuthor = (EditText) textEntryView.findViewById(R.id.editTextAutor);
			editTextAuthor.setText(author);

			dialog = new AlertDialog.Builder(ctx)
			.setIcon(android.R.drawable.btn_star)
			.setTitle("Envíar comentario")
			.setView(textEntryView)
			.setPositiveButton(R.string.enviar, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					EditText editTextComentario = (EditText) textEntryView.findViewById(R.id.editTextFrase);
					EditText editTextAutor = (EditText) textEntryView.findViewById(R.id.editTextAutor);

					String frase = editTextComentario.getText().toString();
					String autor = editTextAutor.getText().toString();

					if (autor.equals("")) {
						autor = "Anónimo";
					} else {
						SharedPreferences.Editor editor;
						editor = preferences.edit();
						String keyAuthor = getString(R.string.author_key);
						editor.putString(keyAuthor, autor);
						editor.commit();
					}

					if (frase.equals("")) {
						Toast.makeText(ctx, "Debe escribir algo.", Toast.LENGTH_SHORT).show();
					} else {
						CommentManager.sendComment(palabra, frase, autor);
						setComments();
					}
				}
			})
			.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {}
			}).create();
			break;

		case DIALOG_RATING_ENTRY:
			// This example shows how to add a custom layout to an AlertDialog
			factory = LayoutInflater.from(ctx);
			final View ratingEntryView = factory.inflate(R.layout.dialog_rating_entry, null);

			final RatingBar rating = (RatingBar) ratingEntryView.findViewById(R.id.ratingBarDialog);
			rating.setRating(puntuacion);

			dialog = new AlertDialog.Builder(ctx)
			.setIcon(android.R.drawable.btn_star)
			.setTitle(R.string.guardar_puntuacion)
			.setView(ratingEntryView)
			.setPositiveButton(R.string.guardar, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dbHandler.updateElement(palabra, rating.getRating());
					setPuntuacion(rating.getRating());
				}
			})
			.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {}
			}).create();
			break;
		}

		return dialog;
	}

	private void setPuntuacion(float puntuacion) {
		rating.setRating(puntuacion);
	}

	private void setContent() {
		try {
			palabra = getIntent().getExtras().getString("name");
			dbHandler.open();
			Cursor cursor = dbHandler.getElementByName(palabra);


			cursor.moveToFirst();
			palabra = cursor.getString(0);
			definicion = cursor.getString(1);
			texto.setText(Html.fromHtml("<b>"+palabra+"</b><br>"+definicion), BufferType.SPANNABLE);
			fecha.setText(cursor.getString(3));
			puntuacion = cursor.getFloat(2);
			rating.setRating(puntuacion);
			setComments();

		} catch (Exception e) {
			dialog = new ProgressDialog(this);
			dialog.setMessage("Descargando...");
			dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			dialog.setCancelable(false);			

			new DownloadWords().execute();
		}
	}

	public void setComments() {
		new GetCommentsAsyncTask().execute();
		removeDialog(DIALOG_TEXT_ENTRY);
	}

	@Override
	public void onPause() {
		super.onPause();
		if (dbHandler != null) {
			dbHandler.close();
		}
		finish();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}


	private Cursor getMoreRecentWord() {
		Cursor word = getDbWord();

		if (word.getCount() == 0) {
			dbHandler.updateWithNewRssFeed();
			word = getDbWord();
		}

		return word;
	}

	private Cursor getDbWord() {
		Calendar calendar = Calendar.getInstance();
		return dbHandler.getWord(calendar.getTimeInMillis() - 
				calendar.get(Calendar.MILLISECOND) - 
				calendar.get(Calendar.SECOND)*1000 - 
				calendar.get(Calendar.MINUTE)*1000*60 - 
				calendar.get(Calendar.HOUR_OF_DAY)*1000*60*60);
	}

	private class DownloadWords extends AsyncTask<Void, Float, Cursor>{

		protected void onPreExecute() {
			dialog.show(); //Mostramos el diálogo antes de comenzar
		}

		protected Cursor doInBackground(Void... params) {
			Cursor message = getMoreRecentWord();
			palabra = dbHandler.getTodayWord();
			return message;
		}

		protected void onPostExecute(Cursor message) {
			dialog.dismiss();

			if (message.getCount() != 0) {
				message.moveToFirst();
				texto.setText(Html.fromHtml("<b>" + message.getString(0) +  "</b><br>" + message.getString(1)), BufferType.SPANNABLE);
				fecha.setText(message.getString(2));
				setComments();
			} else {
				texto.setText("La palabra de hoy aún no está disponible");
				fecha.setText("-");
			}
		}
	}

	private class GetCommentsAsyncTask extends AsyncTask<Void, Void, Boolean>{

		public GetCommentsAsyncTask() {
		}

		protected void onPreExecute() {
			progressBar.setVisibility(ProgressBar.VISIBLE);
		}

		protected Boolean doInBackground(Void... params) {
			listComments = CommentManager.getComments(palabra);

			if (listComments != null)
				return true;
			else
				return false;

		}

		protected void onPostExecute(Boolean result) {
			progressBar.setVisibility(ProgressBar.GONE);

			if (result) {
				insertCommentsInLayout();
			}
		}

		private void insertCommentsInLayout() {
			LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayoutComentarios);

			List<Comment> newList = new ArrayList<Comment>();
			try {
				for (Comment comment: listComments) {
					if (!listOldComments.contains(comment)) {
						newList.add(comment);
					}
				}
			} catch (Exception e) {
				Log.e(Utils.tag, "Exception making the comment list: " + e.getMessage());
			}

			Button newTextView = null;

			for (int i = 0; i < newList.size(); i++) {
				newTextView = new Button(ctx);
				Comment comment = newList.get(i);
				newTextView.setText(Html.fromHtml("<b>" + comment.getAutor() + "</b> (<i>" + comment.getFecha() + "</i>)<br>" + comment.getFrase()), BufferType.SPANNABLE);
				newTextView.setTextColor(Color.BLACK);
				newTextView.setBackgroundDrawable(getResources().getDrawable(R.drawable.comment9));
				
				LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
				linearLayoutParams.setMargins(10, 0, 10, 10);
				newTextView.setLayoutParams(linearLayoutParams);

				linearLayout.addView(newTextView);
			}

			listOldComments = listComments;
		}
	}
}
