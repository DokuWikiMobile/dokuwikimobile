package de.timroes.dokuapp.activities;

import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Toast;
import de.timroes.axmlrpc.XMLRPCServerException;
import de.timroes.dokuapp.DokuwikiConstants;
import de.timroes.dokuapp.R;
import de.timroes.dokuapp.Settings;
import de.timroes.dokuapp.services.DokuwikiService;

public class BrowserActivity extends DokuwikiActivity {

	private WebView browser;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.browser);
		browser = (WebView)findViewById(R.id.mainbrowser);
	}

	@Override
	public void onServiceBound(DokuwikiService service) {
		super.onServiceBound(service);
		displayPage(Settings.HOME);
	}

	private synchronized void displayPage(String pagename) {
		// blabbla show loading stuff
		connector.getService().getPage(this, pagename);
		Toast.makeText(this, "displayPage", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onServerErrorCallback(XMLRPCServerException error, long id) {

		if(error.getErrorNr() == DokuwikiConstants.ERROR_NO_ACCESS) {
			showDialog(DIALOG_LOGIN);
		}
		
	}

}