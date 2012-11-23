package com.iago.undiaunapalabra.receivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.iago.undiaunapalabra.R;
import com.iago.undiaunapalabra.activities.OneWordActivity;
import com.iago.undiaunapalabra.db.DbHandler;

public class OnAlarmReceiver extends BroadcastReceiver {
	private final int NOTIF_ALERTA_ID = 0;
	private Context context;
	private String word;
	private DbHandler dbHandler;

	@Override
	public void onReceive(Context context, Intent intent) {
		dbHandler = new DbHandler(context);
		dbHandler.open();

		this.context = context;

		addNewWord();

		dbHandler.close();
	}

	private void addNewWord() {
		word = dbHandler.getTodayWord();

		if (word.equals("")) {
			if (dbHandler.updateWithNewRssFeed()) {

				word = dbHandler.getTodayWord();

				setNotification();

				Log.i("UDUP", "Actualización: " + word);
			} else {
				Log.i("UDUP", "No hay palabras nuevas");
			}
		}
	}

	private void setNotification() {
		//Primero vemos si tenemos que generar la notificación (según las preferencias)
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context); 
		String notificationKey = context.getString(R.string.pref_notification_key);
		String notificationState = prefs.getString(notificationKey, "on");

		if (notificationState.equals("on")) {
			//Obtenemos una referencia al servicio de notificaciones
			String ns = Context.NOTIFICATION_SERVICE;
			NotificationManager notificationManager = (NotificationManager) context.getSystemService(ns);

			//Configuramos la notificación
			int icono = R.drawable.ic_launcher;
			CharSequence textoEstado = "¡Nueva palabra!";
			long hora = System.currentTimeMillis();
			Notification notification = new Notification(icono, textoEstado, hora);
			notification.flags |= Notification.FLAG_AUTO_CANCEL;

			//Configuramos el Intent
			Context contexto = context.getApplicationContext();
			CharSequence titulo = "La palabra del día de hoy es...";
			Intent notificationIntent = new Intent(contexto, OneWordActivity.class);
			notificationIntent.putExtra("name", word);
			PendingIntent contIntent = PendingIntent.getActivity(contexto, 0, notificationIntent, 0);
			notification.setLatestEventInfo(contexto, titulo, word, contIntent);

			notificationManager.notify(NOTIF_ALERTA_ID, notification);
		}
	}

}