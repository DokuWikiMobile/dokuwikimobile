package de.timroes.dokuapp.activities;

import android.app.Activity;
import android.app.Dialog;
import de.timroes.axmlrpc.XMLRPCException;
import de.timroes.axmlrpc.XMLRPCServerException;
import de.timroes.dokuapp.activities.dialogs.LoginDialog;
import de.timroes.dokuapp.content.Page;
import de.timroes.dokuapp.services.DokuwikiService;
import de.timroes.dokuapp.services.DokuwikiServiceConnector;
import de.timroes.dokuapp.services.ServiceConnectorListener;
import de.timroes.dokuapp.xmlrpc.callback.ErrorCallback;
import de.timroes.dokuapp.xmlrpc.callback.LoginCallback;
import de.timroes.dokuapp.services.PageLoadedListener;

/**
 *
 * @author Tim Roes
 */
public abstract class DokuwikiActivity extends Activity 
		implements ServiceConnectorListener, ErrorCallback, PageLoadedListener, LoginCallback {

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
				LoginDialog.Builder builder = new LoginDialog.Builder(this);
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

	protected void onServerErrorCallback(XMLRPCServerException error, long id) { }

	public final void onServerError(final XMLRPCServerException error, final long id) {
		runOnUiThread(new Runnable() {
			public void run() {
				onServerErrorCallback(error, id);
			}
		});
	}

	protected void onPageLoadedCallback(Page page) { }

	public void onPageLoaded(final Page page) {
		runOnUiThread(new Runnable() {
			public void run() {
				onPageLoadedCallback(page);
			}
		});
	}

	protected void onLoginCallback(boolean succeeded, long id) { }

	public void onLogin(final boolean succeeded, final long id) {
		runOnUiThread(new Runnable() {
			public void run() {
				onLoginCallback(succeeded, id);
			}
		});
	}

}