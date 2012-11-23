package com.iago.undiaunapalabra.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class DownloadDialogAsyncTask extends AsyncTask<Void, Void, Boolean>{
	
	private ProgressDialog dialog;
	private Context ctx;

	public DownloadDialogAsyncTask(Context ctx) {
		this.ctx = ctx;
		dialog = new ProgressDialog(ctx);
		dialog.setMessage("Descargando histórico...");
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setCancelable(false);			
	}

	protected void onPreExecute() {
		dialog.show(); //Mostramos el diálogo antes de comenzar
	}

	protected Boolean doInBackground(Void... params) {
		FtpDownloader.getHistorico(ctx);
		return true;
	}

	protected void onPostExecute(Boolean result) {
		dialog.dismiss();
		new AddHistoricalWordsAsyncTask(ctx).execute();
	}
}
