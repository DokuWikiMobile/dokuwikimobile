package de.timroes.dokuapp.activities;

import android.os.Bundle;
import android.webkit.WebView;
import de.timroes.dokuapp.R;
import de.timroes.dokuapp.Settings;
import de.timroes.dokuapp.exceptions.DokuwikiError;
import de.timroes.dokuapp.exceptions.DokuwikiServerError;

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
		service.getPage(pagename, this);
	}

	@Override
	public void onPageLoaded(String pageHtml, long id) {
		super.onPageLoaded(pageHtml, id);
	}

	@Override
	public void onError(DokuwikiError error, long id) {
		super.onError(error, id);
	}

	@Override
	public void onServerError(DokuwikiServerError error, long id) {
		super.onServerError(error, id);
	}


}