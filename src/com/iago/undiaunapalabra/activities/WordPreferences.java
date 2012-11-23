package com.iago.undiaunapalabra.activities;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.iago.undiaunapalabra.R;

public class WordPreferences extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}
}