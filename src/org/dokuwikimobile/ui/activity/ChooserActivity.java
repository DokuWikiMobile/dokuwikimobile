package org.dokuwikimobile.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import java.net.MalformedURLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dokuwikimobile.Dokuwiki;
import org.dokuwikimobile.R;
import org.dokuwikimobile.ui.view.ABSListView;

/**
 *
 * @author Tim Roes <mail@timroes.de>
 */
public class ChooserActivity extends SherlockActivity implements AdapterView.OnItemClickListener {

	private static int i = 1;
	private ABSListView wikiList;

	private ChooserAdapter adapter;

	private ActionMode actionmode;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle icicle) {

		super.onCreate(icicle);
		setContentView(R.layout.chooser);

		adapter = new ChooserAdapter();

		wikiList = (ABSListView)findViewById(R.id.wiki_list);
		wikiList.setOnItemClickListener(this);
		wikiList.setAdapter(adapter);
		wikiList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		wikiList.setMultiChoiceModeListener(new ChooserMultiChoiceModeListener());

	}

	@Override
	protected void onStart() {
		super.onStart();
		clearChoices();
	}

	/**
	 * Called when the activity should create an options menu.
	 * 
	 * @param menu The menu to be inflated.
	 * @return Whether the menu has been inflated.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.chooser, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		try {
			Dokuwiki.add("DWM Wiki", "http://wiki.dokuwikimobile.org/lib/exe/xmlrpc.php" + i++);
		} catch (MalformedURLException ex) {
			Logger.getLogger(ChooserActivity.class.getName()).log(Level.SEVERE, null, ex);
		}
		adapter.reload();
		return true;
	}

	/**
	 * Will be called when a dokuwiki has been selected in the list. It will start
	 * the browser activity for that dokuwiki.
	 * 
	 * @param parent The parent view.
	 * @param view The view that has been clicked.
	 * @param position The position of the clicked item.
	 * @param id The id of the clicked item.
	 */
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

		Intent intent = new Intent(this, BrowserActivity.class);
		intent.putExtra(DokuwikiActivity.WIKI_HASH, 
				((Dokuwiki)adapter.getItem(position)).getMd5hash());
		startActivity(intent);
		
	}

	private void clearChoices() {
		wikiList.clearChoices();
		wikiList.requestLayout();

		if(actionmode != null) {
			actionmode.finish();
		}
	}

	private class ChooserMultiChoiceModeListener implements ABSListView.MultiChoiceModeListener {

		public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) { 
			int wikisSelected = wikiList.getCheckedItemCount();
			String title = getResources().getQuantityString(R.plurals.wikis_selected, wikisSelected, wikisSelected);
			mode.setTitle(title);
		}

		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			actionmode = mode;
			MenuInflater inflater = getSupportMenuInflater();
			inflater.inflate(R.menu.chooser_item_context, menu);
			return true;
		}

		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			
			switch(item.getItemId()) {
				case R.id.delete:
					int wikisToDelete = wikiList.getCheckedItemCount();
					// Delete checked wikis from list
					for(int i = 0; i < wikiList.getCheckedItemPositions().size(); i++) {
						if(wikiList.getCheckedItemPositions().valueAt(i)) {
							int position = wikiList.getCheckedItemPositions().keyAt(i);
							Dokuwiki.deleteWiki((Dokuwiki)adapter.getItem(position));
						}
					}
					// Close ActionMode after deleting wikis
					mode.finish();
					adapter.reload();
					Toast.makeText(ChooserActivity.this, 
							getResources().getQuantityString(R.plurals.wikis_deleted, 
							wikisToDelete, wikisToDelete), Toast.LENGTH_SHORT).show();
					return true;
				default:
					return false;
			}

		}

		public void onDestroyActionMode(ActionMode mode) { 
			actionmode = null;
		}
		
	}

	private class ChooserAdapter extends BaseAdapter {

		private List<Dokuwiki> dokuwikis = Dokuwiki.getAll();

		public void reload() {
			dokuwikis = Dokuwiki.getAll();
			notifyDataSetChanged();
		}
		
		public int getCount() {
			return dokuwikis.size();
		}

		public Object getItem(int position) {
			return dokuwikis.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			
			if(convertView == null)
				convertView = ChooserActivity.this.getLayoutInflater().inflate(R.layout.chooser_item, parent, false);

			Dokuwiki dw = dokuwikis.get(position);
			
			((TextView)convertView.findViewById(R.id.wiki_title)).setText(dw.getTitle());
			((TextView)convertView.findViewById(R.id.wiki_url)).setText(dw.getUrl().toString());

			return convertView;

		}
		
	}

}
