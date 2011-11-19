package de.timroes.dokuapp.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import de.timroes.axmlrpc.XMLRPCServerException;
import de.timroes.dokuapp.R;
import de.timroes.dokuapp.Settings;
import de.timroes.dokuapp.content.Page;
import de.timroes.dokuapp.services.DokuwikiService;
import de.timroes.dokuapp.views.DokuwikiWebView;
import de.timroes.dokuapp.views.DokuwikiWebView.ScrollListener;
import de.timroes.dokuapp.views.MessageView;
import de.timroes.dokuapp.xmlrpc.DokuwikiXMLRPCClient;

public class BrowserActivity extends DokuwikiActivity implements ScrollListener {

	private final static int DIALOG_PAGEINFO = 1;
	
	private DokuwikiWebView browser;
	private MessageView message;

	private Page currentPage;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.browser);
		browser = (DokuwikiWebView)findViewById(R.id.mainbrowser);
		message = (MessageView)findViewById(R.id.message);
		
		browser.setScrollListener(this);
		message.setWebView(browser);
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

	private String getPageInfoMessage() {
		if(currentPage == null)
			return "";

		return getResources().getString(R.string.pageinfo_dialog, 
				currentPage.getPageInfo().getName(),
				currentPage.getPageInfo().getAuthor(),
				currentPage.getPageInfo().getLastModified().toLocaleString());
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch(id) {
			case DIALOG_PAGEINFO:
				((AlertDialog)dialog).setMessage(Html.fromHtml(getPageInfoMessage()));
				break;
		}
		super.onPrepareDialog(id, dialog);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id) {
			case DIALOG_PAGEINFO:
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle(R.string.pageinfo);
				builder.setMessage(Html.fromHtml(getPageInfoMessage()));
				builder.setPositiveButton(R.string.ok, new OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
					}
				});
				return builder.create();
			default:
				return super.onCreateDialog(id);
		}
	}

	@Override
	protected void onPageLoadedCallback(Page page) {
		super.onPageLoadedCallback(page);
		this.currentPage = page;
		message.setMessage(MessageView.Type.SUCCESS, page.getPageInfo().toString());
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
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.pageinfo).setEnabled(this.currentPage != null);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.realod) {
			displayPage(Settings.HOME);
		} else if(item.getItemId() == R.id.preferences) {
			startActivity(new Intent(this, Preferences.class));
		} else if(item.getItemId() == R.id.pageinfo) {
			showDialog(DIALOG_PAGEINFO);
		} else {
			return super.onOptionsItemSelected(item);
		}

		return false;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	public void onScroll(int l, int t, int oldl, int oldt) {
	}

}