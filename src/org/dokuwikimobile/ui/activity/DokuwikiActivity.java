package org.dokuwikimobile.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import de.timroes.axmlrpc.XMLRPCException;
import de.timroes.axmlrpc.XMLRPCServerException;
import org.dokuwikimobile.ui.dialog.LoginDialog;
import org.dokuwikimobile.model.Page;
import org.dokuwikimobile.model.SearchResult;
import org.dokuwikimobile.service.DokuwikiService;
import org.dokuwikimobile.service.DokuwikiServiceConnector;
import org.dokuwikimobile.service.PageLoadedListener;
import org.dokuwikimobile.service.ServiceConnectorListener;
import org.dokuwikimobile.xmlrpc.DokuwikiXMLRPCClient;
import org.dokuwikimobile.listener.ErrorListener;
import org.dokuwikimobile.listener.LoginListener;
import org.dokuwikimobile.listener.SearchListener;
import java.util.List;

/**
 *
 * @author Tim Roes
 */
public abstract class DokuwikiActivity extends Activity 
		implements ServiceConnectorListener, ErrorListener, PageLoadedListener, 
		LoginListener, SearchListener, LoginDialog.LoginDialogFinished {

	protected final static int DIALOG_LOGIN = 0;
	
	protected final DokuwikiServiceConnector connector;

	public DokuwikiActivity() {
		connector = new DokuwikiServiceConnector(this, this);
	}
	
	public void onServiceBound(DokuwikiService service) { }

	public void onServiceUnbound() { }

	@Override
	protected void onStart() {
		super.onStart();
		connector.bindToService();
	}

	@Override
	protected void onStop() {
		super.onStop();
		connector.unbindFromService();
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