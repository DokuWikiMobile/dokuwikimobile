package org.dokuwikimobile.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.*;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import java.net.MalformedURLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dokuwikimobile.Dokuwiki;
import org.dokuwikimobile.R;

/**
 *
 * @author Tim Roes <mail@timroes.de>
 */
public class ChooserActivity extends Activity implements AdapterView.OnItemClickListener {

	private ListView wikiList;

	private ChooserAdapter adapter;
	
	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle icicle) {

		super.onCreate(icicle);
		setContentView(R.layout.chooser);

		adapter = new ChooserAdapter();

		wikiList = (ListView)findViewById(R.id.wiki_list);
		wikiList.setOnItemClickListener(this);
		wikiList.setAdapter(adapter);

		registerForContextMenu(wikiList);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(R.string.add);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		try {
				Dokuwiki.add("http://wiki.dokuwikimobile.org/lib/exe/xmlrpc.php");
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

	/**
	 * This method will be called when a context menu should be created.
	 * 
	 * @param menu The context menu, that should be inflated.
	 * @param v The view for which a context menu should be created.
	 * @param menuInfo The context menu information.
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.choser_item_context, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
		
		switch(item.getItemId()) {
			
			case R.id.delete:
				Dokuwiki.deleteWiki((Dokuwiki)adapter.getItem(info.position));
				adapter.reload();
				return true;
				
			default:
				return super.onContextItemSelected(item);
				
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
			
			((TextView)convertView.findViewById(R.id.wiki_title)).setText(dw.getMd5hash());
			((TextView)convertView.findViewById(R.id.wiki_url)).setText(dw.getUrl().toString());

			return convertView;

		}
		
	}

}
