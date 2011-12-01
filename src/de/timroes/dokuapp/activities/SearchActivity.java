package de.timroes.dokuapp.activities;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import de.timroes.dokuapp.R;
import de.timroes.dokuapp.content.SearchResult;
import de.timroes.dokuapp.services.DokuwikiService;
import java.util.List;

/**
 *
 * @author Tim Roes
 */
public class SearchActivity extends DokuwikiActivity {

	private ProgressDialog progress;
	private String pendingSearch;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);

		((ListView)findViewById(R.id.search_results)).setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> adview, View view, int position, long id) {
				Intent intent = new Intent(SearchActivity.this, BrowserActivity.class);
				intent.putExtra(BrowserActivity.PAGEID, ((SearchResult)adview.getItemAtPosition(position)).getId());
				startActivity(intent);
			}

		});

		waitForSearch();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		waitForSearch();
	}

	@Override
	public void onServiceBound(DokuwikiService service) {
		super.onServiceBound(service);
		// Do a requested search
		doSearch();
	}

	private void waitForSearch() {
		if(Intent.ACTION_SEARCH.equals(getIntent().getAction())) {
			pendingSearch = getIntent().getStringExtra(SearchManager.QUERY);
			progress = ProgressDialog.show(this, null, getResources().getText(R.string.searching));
			doSearch();
		}
	}
	
	private void doSearch() {
		if(connector.isConnected() && pendingSearch != null) {
			connector.getService().search(this, pendingSearch);
			pendingSearch = null;
		}
	}

	@Override
	protected void onSearchCallback(List<SearchResult> pages, long id) {
		super.onSearchCallback(pages, id);
		progress.dismiss();
		ListView view = (ListView)findViewById(R.id.search_results);
		ArrayAdapter<SearchResult> adapter = new ArrayAdapter<SearchResult>(this, android.R.layout.simple_list_item_1);
		for(SearchResult r : pages) {
			adapter.add(r);
		}
		view.setAdapter(adapter);
	}

	

}