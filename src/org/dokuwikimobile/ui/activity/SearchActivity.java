package org.dokuwikimobile.ui.activity;

import android.app.SearchManager;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import java.util.ArrayList;
import java.util.List;
import org.dokuwikimobile.DokuwikiApplication;
import org.dokuwikimobile.R;
import org.dokuwikimobile.listener.SearchListener;
import org.dokuwikimobile.model.SearchResult;
import org.dokuwikimobile.xmlrpc.DokuwikiXMLRPCClient.Canceler;
import org.dokuwikimobile.xmlrpc.ErrorCode;

/**
 *
 * @author Tim Roes <mail@timroes.de>
 */
public class SearchActivity extends DokuwikiActivity implements SearchListener {

	private ListView results;
	private SearchResultAdapter adapter;
	private Canceler canceler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		adapter = new SearchResultAdapter();
		adapter.registerDataSetObserver(new SearchDataObserver());
		
		results = new ListView(this);
		results.setAdapter(adapter);
		results.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(SearchActivity.this, BrowserActivity.class);
				intent.putExtra(BrowserActivity.EXTRA_PAGE_ID, 
						((SearchResult)adapter.getItem(position)).getId());
				startActivity(intent);
			}

		});

		setupActionBar();

		startSearchFromIntent();
		
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);

		// If there is a running search cancel this before starting new search
		if(canceler != null) {
			Log.d(DokuwikiApplication.LOGGER_NAME, "Aborting previous search for new search.");
			canceler.cancel();
		}
		
		startSearchFromIntent();
	}

	private void startSearchFromIntent() {

		if(Intent.ACTION_SEARCH.equals(getIntent().getAction())) {
			// Show loading screen
			loadingScreen();
			// Clear subtitle
			getSupportActionBar().setSubtitle(null);
			// Start search
			manager.search(this, getIntent().getStringExtra(SearchManager.QUERY));
		}
		
	}

	private void setupActionBar() {

		ActionBar bar = getSupportActionBar();
		// Set titel to titel of dokuwiki
		bar.setTitle(manager.getDokuwiki().getTitle());

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.search, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case R.id.new_search:
				onSearchRequested();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	public void onSearchResults(final List<SearchResult> pages, long id) {
		
		handler.post(new Runnable() {

			public void run() {

				// If some results have been found, display these
				if(!pages.isEmpty()) {
					
					setContentView(results);

					// Clear current entries in the adapter
					adapter.results.clear();
					// Add search results to adapter
					adapter.results.addAll(pages);
					
					adapter.notifyDataSetChanged();

				}

			}

		});
		
	}

	@Override
	public void onStartLoading(Canceler cancel, long id) {
		super.onStartLoading(cancel, id);
		this.canceler = cancel;
	}

	@Override
	public void onEndLoading(long id) {
		super.onEndLoading(id);
		this.canceler = null;

		handler.post(new NoResultsScreen());
		
	}

	@Override
	public void onError(ErrorCode error, long id) {
		super.onError(error, id);
		// TODO: implement this
		throw new UnsupportedOperationException("Not supported yet.");
	}

	private class SearchResultAdapter extends BaseAdapter {

		private List<SearchResult> results = new ArrayList<SearchResult>();
		
		public int getCount() {
			return results.size();
		}

		public Object getItem(int position) {
			return results.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {

			if(convertView == null) 
				convertView = getLayoutInflater().inflate(R.layout.searchresult, parent, false);

			SearchResult result = results.get(position);
			String hits = getResources().getQuantityString(R.plurals.search_hits,
					result.getScore(), result.getScore());
			
			((TextView)convertView.findViewById(R.id.searchresult_id)).setText(result.getTitle());
			((TextView)convertView.findViewById(R.id.searchresult_hits)).setText(hits);

			return convertView;
			
		}

	}

	/**
	 * The SearchDataObserver refreshes the subtitle of the actionbar to show
	 * the amount of search results.
	 */
	private class SearchDataObserver extends DataSetObserver {

		/**
		 * This method will be called when the search results has been changed.
		 * It will refresh the subtitle in the ActionBar to show the amount of
		 * search results.
		 */
		@Override
		public void onChanged() {
			super.onChanged();
			getSupportActionBar().setSubtitle(getResources().getQuantityString(
					R.plurals.search_results, adapter.getCount(), adapter.getCount()));
		}
		
	}

	/**
	 * This Runnable will check if the adapter holds no search results.
	 * If it has no results, a big no search result message will be thrown.
	 */
	private class NoResultsScreen implements Runnable {

		public void run() {

			if(adapter.getCount() == 0) {
				// If no results have been found, and we have been finished loading
				// display a big no search result message.
				messageScreen(R.string.no_search_results,
						new View.OnClickListener() {
							public void onClick(View v) {
								onSearchRequested();
							}
						}
				);
			}

		}
		
	}

}
