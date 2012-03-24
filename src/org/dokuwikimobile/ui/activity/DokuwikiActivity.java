package org.dokuwikimobile.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import de.timroes.axmlrpc.XMLRPCException;
import de.timroes.axmlrpc.XMLRPCServerException;
import java.util.List;
import org.dokuwikimobile.listener.CancelableListener;
import org.dokuwikimobile.listener.LoginListener;
import org.dokuwikimobile.listener.SearchListener;
import org.dokuwikimobile.model.Page;
import org.dokuwikimobile.model.SearchResult;
import org.dokuwikimobile.listener.PageListener;
import org.dokuwikimobile.manager.DokuwikiManager;
import org.dokuwikimobile.ui.dialog.LoginDialog;
import org.dokuwikimobile.xmlrpc.DokuwikiXMLRPCClient;

/**
 *
 * @author Tim Roes
 */
public abstract class DokuwikiActivity extends Activity 
		implements CancelableListener, PageListener, 
		LoginListener, SearchListener, LoginDialog.LoginDialogFinished {

	protected final static int DIALOG_LOGIN = 0;
	
	protected DokuwikiManager manager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
	
	protected void onErrorCallback(XMLRPCException error, long id) { }

	public final void onError(final XMLRPCException error, final long id) {
		runOnUiThread(new Runnable() {
			public void run() {
				onErrorCallback(error, id);
			}
		});
	}

	protected void onServerErrorCallback(XMLRPCServerException error, long id) {
		if(error.getErrorNr() == DokuwikiXMLRPCClient.ERROR_NO_ACCESS) {
                        // TODO: only if not logged in
			showDialog(DIALOG_LOGIN);
		}
	}

	public final void onServerError(final XMLRPCServerException error, final long id) {
		runOnUiThread(new Runnable() {
			public void run() {
				onServerErrorCallback(error, id);
			}
		});
	}

	public void loginDialogFinished(FinishState state) { }

	protected void onPageLoadedCallback(Page page) { }

	public final void onPageLoaded(final Page page) {
		runOnUiThread(new Runnable() {
			public void run() {
				onPageLoadedCallback(page);
			}
		});
	}

	protected void onLoginCallback(boolean succeeded, long id) { }

	public final void onLogin(final boolean succeeded, final long id) {
		runOnUiThread(new Runnable() {
			public void run() {
				onLoginCallback(succeeded, id);
			}
		});
	}

	protected void onSearchCallback(List<SearchResult> pages, long id) { }

	public final void onSearchResults(final List<SearchResult> pages, final long id) {
		runOnUiThread(new Runnable() {
			public void run() {
				onSearchCallback(pages, id);
			}
		});
	}

}