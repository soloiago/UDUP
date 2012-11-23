package com.iago.undiaunapalabra.activities;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.iago.undiaunapalabra.R;

public class AboutActivity extends Activity {
	private final String aboutText = "La aplicación se basa en la información que undiaunapalabra nos ofrece en su página web:<br><h2><a href='http://www.undiaunapalabra.com'>www.undiaunapalabra.com</a></h2>";
	private final String aboutContact = "<h2><a href='mailto:soloiago@gmail.com'>soloiago@gmail.com</a><br><a href='http://www.iagodiaz.com'>http://www.iagodiaz.com</a></h2>";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		
		TextView about = (TextView) findViewById(R.id.textAboutUDUP);
		about.setText(Html.fromHtml(aboutText));
		about.setMovementMethod(LinkMovementMethod.getInstance());
		
	    TextView contact = (TextView) findViewById(R.id.textAboutContact);
	    contact.setText(Html.fromHtml(aboutContact));
	    contact.setMovementMethod(LinkMovementMethod.getInstance());
	}
}
