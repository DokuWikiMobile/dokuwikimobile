package org.dokuwikimobile.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import org.dokuwikimobile.Dokuwiki;
import org.dokuwikimobile.DokuwikiApplication;
import org.dokuwikimobile.manager.DokuwikiManager;
import org.dokuwikimobile.ui.dialog.LoginDialog;

/**
 *
 * @author Tim Roes <mail@timroes.de>
 */
public abstract class DokuwikiActivity extends Activity 
		implements LoginDialog.LoginDialogFinished {

	public final static String WIKI_HASH = "wiki_hash";

	protected final static int DIALOG_LOGIN = 0;
	
	protected DokuwikiManager manager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(!readWikiFromIntent(getIntent())) {
			finish();
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		readWikiFromIntent(intent);
	}

	private boolean readWikiFromIntent(Intent intent) {

		String hash = intent.getStringExtra(WIKI_HASH);
		Dokuwiki dokuwiki = Dokuwiki.get(hash);
		
		if(dokuwiki == null) {
			// If dokuwiki doesn't exist, just close activity and log the error.
			Log.e(DokuwikiApplication.LOGGER_NAME, "The hash in the intent (" 
					+ hash + ") isn't stored in the dokuwiki list");
			return false;
		}

		// Initialize manager for the dokuwiki from the intent
		manager = DokuwikiManager.get(dokuwiki);

		return true;

	}

	@Override
	public void startActivity(Intent intent) {
		intent.putExtra(WIKI_HASH, manager.getDokuwiki().getMd5hash());
		super.startActivity(intent);
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		intent.putExtra(WIKI_HASH, manager.getDokuwiki().getMd5hash());
		super.startActivityForResult(intent, requestCode);
	}

	@Override
	public void startActivityFromChild(Activity child, Intent intent, int requestCode) {
		intent.putExtra(WIKI_HASH, manager.getDokuwiki().getMd5hash());
		super.startActivityFromChild(child, intent, requestCode);
	}

	@Override
	public boolean startActivityIfNeeded(Intent intent, int requestCode) {
		intent.putExtra(WIKI_HASH, manager.getDokuwiki().getMd5hash());
		return super.startActivityIfNeeded(intent, requestCode);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id) {
			case DIALOG_LOGIN:
				LoginDialog.Builder builder = new LoginDialog.Builder(this,this);
				return builder.create();
			default:
				return super.onCreateDialog(id);
		}
	}

	public void loginDialogFinished(FinishState state) { }

}