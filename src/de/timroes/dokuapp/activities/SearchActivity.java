package de.timroes.dokuapp.activities;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import de.timroes.dokuapp.R;
import de.timroes.dokuapp.content.SearchResult;
import de.timroes.dokuapp.services.DokuwikiService;
import de.timroes.dokuapp.services.LoadingListener;
import de.timroes.dokuapp.xmlrpc.DokuwikiXMLRPCClient.Canceler;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Tim Roes
 */
public class SearchActivity extends DokuwikiActivity implements LoadingListener,
		OnCancelListener {

	/**
	 * The progress dialog that is showed during searching.
	 */
	private ProgressDialog progress;

	/**
	 * The progress bar that is shown below the list.
	 */
	private View searchProgress;

	/**
	 * The canceler to cancel the current search.
	 */
	private Canceler canceler;

	/**
	 * The queued search string.
	 */
	private String queuedSearch;

	/**
	 * The listview of search results.
	 */
	private ListView resultList;

	private List<SearchResult> searchResults = new ArrayList<SearchResult>();

	private View noSearchResults;

	private SearchResultAdapter adapter;

//	private List<SearchResult
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);

		searchProgress = findViewById(R.id.search_progress);
		searchProgress.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				cancel();
			}
		});

		noSearchResults = getLayoutInflater().inflate(R.layout.nosearchresults, null);

		resultList = (ListView)findViewById(R.id.search_results);
		resultList.setFooterDividersEnabled(false);
		resultList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> adview, View view, int position, long id) {
				// A search result has been clicked
				Intent intent = new Intent(SearchActivity.this, BrowserActivity.class);
				intent.putExtra(BrowserActivity.PAGEID, ((SearchResult)adview.getItemAtPosition(position)).getId());
				startActivity(intent);
			}
		});

		adapter = new SearchResultAdapter();
		resultList.setAdapter(adapter);

		queueSearch();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		queueSearch();
	};

	@Override
	public void onServiceBound(DokuwikiService service) {
		super.onServiceBound(service);
		// Do a requested search
		startSearch();
	}

	/**
	 * Queue the search string from the intent for a search.
	 * This will show the loading dialog. The doSearch method can be called
	 * afterwards, to start the queued search.
	 */
	private void queueSearch() {
		if(Intent.ACTION_SEARCH.equals(getIntent().getAction())) {
			queuedSearch = getIntent().getStringExtra(SearchManager.QUERY);
			progress = ProgressDialog.show(this, null, getResources().getText(R.string.searching), 
					true, true, this);
			// Try to start the search yet. Normally the service won't be available yet.
			startSearch();
		}
	}
	
	/**
	 * This method starts a queued search. It will be called when the service
	 * is available for the activity.
	 */
	private void startSearch() {
		if(connector.isConnected() && queuedSearch != null) {
			connector.getService().search(this, this, queuedSearch);
			// Clear queued search since, since its started now
			queuedSearch = null;
		}
	}

	@Override
	protected void onSearchCallback(List<SearchResult> pages, long id) {
		super.onSearchCallback(pages, id);
		
		// If we got any results hide the dialog
		hideDialogLoading();

		// Empty results, write warning
		if(pages.isEmpty()) {
			System.out.println("Pages empty");
			//resultList.addFooterView(noSearchResults);
			return;
		}

		// Clear old results and show new results
		//searchResults.clear();
		//searchResults.addAll(pages);
		adapter.results.clear();
		adapter.results.addAll(pages);
		adapter.notifyObservers();
		//resultList.setAdapter(resultList.getAdapter());

	}

	public void startLoading(Canceler cancel) {
		this.canceler = cancel;
		showBottomLoading();
	}

	public void endLoading() {
		hideBottomLoading();
		hideDialogLoading();
	}

	/**
	 * Will be called, when the user press backs, while the progress
	 * dialog is shown. It will hide the dialog. If the queued search
	 * has already been started, cancel it.
	 */
	public void onCancel(DialogInterface dialog) {
		cancel();
	}

	private void cancel() {
		if(canceler != null) {
			canceler.cancel();
		}
		hideDialogLoading();
		hideBottomLoading();
	}
	
	private void showBottomLoading() {
		searchProgress.setVisibility(View.VISIBLE);
		// Must be done due to a "bug" in Android, see also:
		// http://code.google.com/p/android/issues/detail?id=12870
		resultList.setAdapter(resultList.getAdapter());
	}

	private void hideBottomLoading() {
		resultList.post(new Runnable() {
			public void run() {
				searchProgress.setVisibility(View.GONE);
			}
		});
	}

	private void hideDialogLoading() {
		if(progress.isShowing()) {
			progress.dismiss();
		}
	}

	/**
	 * The adapter that holds the search results for the result ListView.
	 */
	private class SearchResultAdapter implements ListAdapter {

		private List<DataSetObserver> observers = new ArrayList<DataSetObserver>();
		public List<SearchResult> results = new ArrayList<SearchResult>();
		
		public boolean areAllItemsEnabled() {
			return true;
		}

		public boolean isEnabled(int arg0) {
			return true;
		}

		public void registerDataSetObserver(DataSetObserver observer) {
			observers.add(observer);
		}

		public void unregisterDataSetObserver(DataSetObserver observer) {
			observers.remove(observer);
		}

		public int getCount() {
			return results.size();
		}

		public Object getItem(int pos) {
			return results.get(pos);
		}

		public long getItemId(int pos) {
			return pos;
		}

		public boolean hasStableIds() {
			return false;
		}

		public View getView(int pos, View oldView, ViewGroup parent) {
			if(oldView == null)
				oldView = SearchActivity.this.getLayoutInflater().inflate(R.layout.searchresult, null);
			((TextView)oldView.findViewById(R.id.searchresult_id)).setText(results.get(pos).getId());
			String hits = getResources().getString(R.string.search_hits,
					results.get(pos).getScore());
			((TextView)oldView.findViewById(R.id.searchresult_hits)).setText(hits);
			return oldView;
		}

		public int getItemViewType(int arg0) {
			return 0;
		}

		public int getViewTypeCount() {
			return 1;
		}

		public boolean isEmpty() {
			return results.isEmpty();
		}

		public void notifyObservers() {
			for(DataSetObserver o : observers) {
				o.onChanged();
			}
		}
		
	}

}