package org.dokuwikimobile.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import org.dokuwikimobile.DokuwikiApplication;
import org.dokuwikimobile.R;
import org.dokuwikimobile.listener.PageListener;
import org.dokuwikimobile.model.Page;
import org.dokuwikimobile.ui.view.DokuwikiWebView;
import org.dokuwikimobile.xmlrpc.DokuwikiXMLRPCClient.Canceler;
import org.dokuwikimobile.xmlrpc.ErrorCode;

/**
 *
 * @author Tim Roes <mail@timroes.de>
 */
public class BrowserActivity extends DokuwikiActivity implements PageListener {

	public static final String EXTRA_PAGE_ID = "pageid";

	private DokuwikiWebView browser;
	private Canceler canceler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		browser = new DokuwikiWebView(this);

		setupActionBar();

		startLoadingPage();
		
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		startLoadingPage();
	}

	

	/**
	 * Create the option menu for this activity.
	 * 
	 * @param menu The menu to inflate.
	 * @return Whether the menu has been changed.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.browser, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.pageinfo).setEnabled(true);
		if(canceler != null) {
			menu.findItem(R.id.reload_abort).setTitle(R.string.cancel);
			menu.findItem(R.id.reload_abort).setIcon(R.drawable.ic_bar_cancel);
		} else {
			menu.findItem(R.id.reload_abort).setTitle(R.string.refresh);
			menu.findItem(R.id.reload_abort).setIcon(R.drawable.ic_bar_refresh);
		}
		return true;
	}

	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch(item.getItemId()) {
			case R.id.reload_abort:
				if(canceler != null) {
					Log.d(DokuwikiApplication.LOGGER_NAME, "Abort loading");
					canceler.cancel();
					endLoading();
				} else {
					startLoadingPage();
				}
				return true;
			case R.id.search:
				onSearchRequested();
				return true;
			case R.id.preferences:
				startActivity(new Intent(this, Preferences.class));
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}

	}
	
	/**
	 * Setup the action bar. This will set the titles and enable the up navigation.
	 */
	private void setupActionBar() {

		ActionBar bar = getSupportActionBar();
		// Set titel to titel of dokuwiki
		bar.setTitle(manager.getDokuwiki().getTitle());
		
	}

	private void startLoadingPage() {
		
		// Start the loading screen
		loadingScreen();
		
		if(getIntent().getStringExtra(EXTRA_PAGE_ID) != null) {
			manager.getPage(this, getIntent().getStringExtra(EXTRA_PAGE_ID));
		} else {
			manager.getPage(this, "start");
		}

	}

	private void showLoading(Canceler canceler) {
		this.canceler = canceler;
		handler.post(new Runnable() {

			public void run() {
				invalidateOptionsMenu();
			}
			
		});

	}

	private void endLoading() {
		canceler = null;
		handler.post(new Runnable() {

			public void run() {
				setContentView(browser);
				invalidateOptionsMenu();
				setSupportProgressBarIndeterminateVisibility(false);
			}
			
		});
	}

	public void onPageLoaded(Page page) {
		showPage(page);
	}

	private void showPage(Page page) {
		browser.loadPage(page);
	}
	
	@Override
	public void onStartLoading(Canceler cancel, long id) {
		super.onStartLoading(cancel, id);
		showLoading(cancel);
	}

	@Override
	public void onEndLoading(long id) {
		super.onEndLoading(id);
		endLoading();
	}

	@Override
	public void onError(ErrorCode error, long id) {
		super.onError(error, id);
		endLoading();
	}

	
}
