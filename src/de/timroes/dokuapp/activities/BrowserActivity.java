package de.timroes.dokuapp.activities;

import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Toast;
import de.timroes.axmlrpc.XMLRPCException;
import de.timroes.axmlrpc.XMLRPCServerException;
import de.timroes.dokuapp.R;
import de.timroes.dokuapp.Settings;

public class BrowserActivity extends DokuwikiActivity {

	private WebView browser;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browser);
		browser = (WebView)findViewById(R.id.mainbrowser);
    }

	@Override
	protected void onServiceBound() {
		super.onServiceBound();
		displayPage(Settings.HOME);
	}

	@Override
	protected void onServiceUnbound() {
		super.onServiceUnbound();
	}

	private synchronized void displayPage(String pagename) {
		// blabbla show loading stuff
		service.getPage(this, pagename);
		Toast.makeText(this, "displayPage", Toast.LENGTH_SHORT).show();
	}

	public void onServerErrorCallback(XMLRPCServerException error, long id) {
		Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
	}


}