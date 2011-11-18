package de.timroes.dokuapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.Toast;
import de.timroes.axmlrpc.XMLRPCServerException;
import de.timroes.dokuapp.R;
import de.timroes.dokuapp.Settings;
import de.timroes.dokuapp.content.Page;
import de.timroes.dokuapp.services.DokuwikiService;
import de.timroes.dokuapp.views.MessageView;
import de.timroes.dokuapp.xmlrpc.DokuwikiXMLRPCClient;

public class BrowserActivity extends DokuwikiActivity {

	private WebView browser;
	private MessageView message;

	private Page currentPage;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.browser);
		browser = (WebView)findViewById(R.id.mainbrowser);
		message = (MessageView)findViewById(R.id.message);
	}

	@Override
	public void onServiceBound(DokuwikiService service) {
		super.onServiceBound(service);
		displayPage(Settings.HOME);
	}

	private synchronized void displayPage(String pagename) {
		// blabbla show loading stuff
		connector.getService().getPage(this, pagename);
		Toast.makeText(this, "Loading page...", Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onPageLoadedCallback(Page page) {
		super.onPageLoadedCallback(page);
		message.setMessage(MessageView.Type.WARNING, page.getPageInfo().toString());
		browser.loadDataWithBaseURL(null, page.getHtml(), "text/html", "utf-8", null);
		Toast.makeText(this, "Page loaded.", Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.browser, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.realod) {
			displayPage(Settings.HOME);
		} else if(item.getItemId() == R.id.preferences) {
			startActivity(new Intent(this, Preferences.class));
		} else {
			return super.onOptionsItemSelected(item);
		}

		return false;
	}

	@Override
	public void onServerErrorCallback(XMLRPCServerException error, long id) {

		if(error.getErrorNr() == DokuwikiXMLRPCClient.ERROR_NO_ACCESS) {
			showDialog(DIALOG_LOGIN);
		}
		
	}

}