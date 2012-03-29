package org.dokuwikimobile.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import java.net.MalformedURLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dokuwikimobile.Dokuwiki;
import org.dokuwikimobile.DokuwikiApplication;
import org.dokuwikimobile.R;

/**
 *
 * @author Tim Roes <mail@timroes.de>
 */
public class ChooserActivity extends Activity {

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
		wikiList.setAdapter(adapter);

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
		wikiList.invalidate();
		return true;
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

			Log.e(DokuwikiApplication.LOGGER_NAME, String.valueOf(position));
			Dokuwiki dw = dokuwikis.get(position);
			
			((TextView)convertView.findViewById(R.id.wiki_title)).setText(dw.getMd5hash());
			((TextView)convertView.findViewById(R.id.wiki_url)).setText(dw.getUrl().toString());

			return convertView;

		}
		
	}

}
