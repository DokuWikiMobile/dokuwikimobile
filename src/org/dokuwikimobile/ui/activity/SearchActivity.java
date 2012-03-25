package org.dokuwikimobile.ui.activity;

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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import org.dokuwikimobile.R;
import org.dokuwikimobile.model.SearchResult;
import org.dokuwikimobile.listener.CancelableListener;
import org.dokuwikimobile.manager.DokuwikiManager;
import org.dokuwikimobile.xmlrpc.DokuwikiXMLRPCClient.Canceler;

/**
 *
 * @author Tim Roes
 */
public class SearchActivity extends DokuwikiActivity implements CancelableListener,
		OnCancelListener {

	private DokuwikiManager manager;

	/**
	 * The progress dialog that is showed during searching.
	 */
	private ProgressDialog progress;

	/**
	 * The progress bar that is shown below the list.
	 */
	private View searchProgress;

	/**
	 * The no search results view above the list of results.
	 */
	private View searchInformation;
	
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

	/**
	 * The adapter for the search results.
	 */
	private SearchResultAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);

		searchProgress = findViewById(R.id.search_progress);
                // TODO: Move to xml file
		searchProgress.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				cancel();
			}
		});

		searchInformation = findViewById(R.id.search_information);
		searchInformation.setVisibility(View.GONE);
                // TODO: Move to xml file
		searchInformation.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				onSearchRequested();
			}
		});

		resultList = (ListView)findViewById(R.id.search_results);
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
		startSearch();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		queueSearch();
		startSearch();
	};

	/**
	 * Queue the search string from the intent for a search.
	 * This will show the loading dialog. The doSearch method can be called
	 * afterwards, to start the queued search.
	 */
	private void queueSearch() {
		if(Intent.ACTION_SEARCH.equals(getIntent().getAction())) {
			queuedSearch = getIntent().getStringExtra(SearchManager.QUERY);
			// Show no search results until we have some to show.
			hideSearchInformation();
			// Show the progress dialog as long as we don't have any results to show
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
		if(queuedSearch != null) {
			manager.search(this, queuedSearch);
			// Clear queued search since, since its started now
			queuedSearch = null;
		}
	}

	@Override
	protected void onSearchCallback(List<SearchResult> pages, long id) {
		super.onSearchCallback(pages, id);
		
		// If we got any results or are completly finished with searching,
		// hide the dialog loading interface.
		if(!pages.isEmpty() || !isLoading()) {
			hideDialogLoading();
		}

		// Clear current entries in the list
		adapter.results.clear();

		// If any results have been found, show them in the list.
		if(!pages.isEmpty()) {
			// Clear old results and show new results
			showSearchResults(pages.size());
			adapter.results.addAll(pages);
		} else if(!isLoading()) {
			// If no Search results are found and search has been finished
			// show the no search result text.
			showSearchResults(0);
		}

		adapter.notifyObservers();

	}

	public void onStartLoading(Canceler cancel, long id) {
		this.canceler = cancel;
		showBottomLoading();
	}

	public void onEndLoading(long id) {
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
		showSearchResults(adapter.results.size());
	}

	private void showSearchResults(int size) {
		String information = getResources().getQuantityString(R.plurals.search_results, 
				size, String.valueOf(size));
		
		((TextView)findViewById(R.id.search_information_text)).setText(information);
		searchInformation.setVisibility(View.VISIBLE);
	}
	
	private void hideSearchInformation() {
		searchInformation.setVisibility(View.GONE);
	}
	
	private void showBottomLoading() {
		searchProgress.setVisibility(View.VISIBLE);
		// TODO: can be removed
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

	private boolean isLoading() {
		return searchProgress.getVisibility() == View.VISIBLE
				|| progress.isShowing();
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