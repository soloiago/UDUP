package com.iago.undiaunapalabra.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.iago.undiaunapalabra.utils.Utils;

public class OnBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {			 
		Utils.setAutomaticUpdate(context);
    }
 }