package com.iago.undiaunapalabra.utils;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.iago.undiaunapalabra.db.DbHandler;
import com.iago.undiaunapalabra.db.DbHelperHistoricalWords;

public class AddHistoricalWordsAsyncTask extends AsyncTask<Void, Integer, Boolean>{

	private ProgressDialog dialog;
	private Context ctx;
	private DbHandler dbHandler;
	private int total;
	private int progress = 0;
	private DbHelperHistoricalWords dbHelperHistorico;

	public AddHistoricalWordsAsyncTask(Context ctx) {
		this.ctx = ctx;
		dbHandler =  new DbHandler(ctx);
		dbHandler.open();
		
		dialog = new ProgressDialog(ctx);
		dialog.setMessage("Incluyendo nuevas palabras...");
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		dialog.setCancelable(false);			

	}

	protected void onPreExecute() {
		dbHelperHistorico = dbHandler.migrate();
		total = dbHelperHistorico.getTotalWords();
		dialog.setMax(total);
		dialog.show(); //Mostramos el diálogo antes de comenzar
	}

	protected Boolean doInBackground(Void... params) {
		if (dbHelperHistorico != null) {
			insertNewWords(dbHelperHistorico);
		}
		return true;
	}

	protected void onProgressUpdate(Integer... progress) {
		dialog.setProgress(progress[0]);
    }

	protected void onPostExecute(Boolean result) {
		FtpDownloader.deleteFile(ctx);
		dialog.dismiss();
	}
	
	private void insertNewWords(DbHelperHistoricalWords dbHelperHistorico) {
		Cursor historicoCursor = dbHelperHistorico.fetchAll();
		
		historicoCursor.moveToFirst();
		
		do {
			ContentValues contentValues = new ContentValues();
			contentValues.put(DbHandler.KEY_WORD, historicoCursor.getString(0));
			contentValues.put(DbHandler.KEY_DEFINITION, historicoCursor.getString(1));
			contentValues.put(DbHandler.KEY_DATE_MS, historicoCursor.getLong(2));
			contentValues.put(DbHandler.KEY_DATE_STRING, historicoCursor.getString(3));
			if (!isWordInMainDB(historicoCursor.getString(0))) {
				
				if(dbHandler.insert(DbHandler.DATABASE_MAIN_TABLE, contentValues) != -1) {
					Log.i(Utils.tag, "Palabra insertada en la BD correctamente");
				} else {
					Log.e(Utils.tag, "Problema al escribir en la BD");
				}
			} else {
				String[] args = new String[] {historicoCursor.getString(0)};
				if(dbHandler.update(DbHandler.DATABASE_MAIN_TABLE, contentValues, args) != -1) {
					Log.i(Utils.tag, "Palabra actualizada en la BD correctamente");
				} else {
					Log.e(Utils.tag, "Problema al actualizar en la BD");
				}
			}
			
			if ((progress++ % 10) == 0) {
				publishProgress(progress);
			}
			
		} while (historicoCursor.moveToNext());
	}
	
	private boolean isWordInMainDB(String word) {
		String[] campos = new String[] {DbHandler.KEY_WORD};
		String[] args = new String[] {word};
		Cursor cursor = dbHandler.query(DbHandler.DATABASE_MAIN_TABLE, campos, DbHandler.KEY_WORD+"=?", args);
		
		return cursor.moveToFirst();
	}

	
}
