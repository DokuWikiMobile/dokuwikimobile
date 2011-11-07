package de.timroes.dokuapp.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import de.timroes.axmlrpc.XMLRPCException;
import de.timroes.axmlrpc.XMLRPCServerException;
import de.timroes.dokuapp.services.DokuwikiService;
import de.timroes.dokuapp.services.DokuwikiService.DokuwikiBinder;
import de.timroes.dokuapp.xmlrpc.callback.ErrorCallback;
import de.timroes.dokuapp.xmlrpc.callback.LoginCallback;
import de.timroes.dokuapp.xmlrpc.callback.PageLoadedCallback;

/**
 *
 * @author Tim Roes
 */
public abstract class DokuwikiActivity extends Activity 
		implements ErrorCallback, PageLoadedCallback, LoginCallback {

	protected DokuwikiService service;

	private ServiceConnection serviceConnection = new ServiceConnection() {

		/**
		 * This methos is called when the service has been connected. This will set the
		 * service variable and call the onServiceBound method, that should
		 * be overwritten in any subclass activiy, that needs the service to be
		 * connected.
		 */
		public void onServiceConnected(ComponentName className, IBinder service) {
			DokuwikiActivity.this.service = ((DokuwikiBinder)service).getService();
			onServiceBound();
		}

		/**
		 * This method is called when the connection to the service has terminated.
		 * It clears the service variable and call the onServiceUnbound method,
		 * that can be overwritten in any subclass activity, that needs to get
		 * informed.
		 */
		public void onServiceDisconnected(ComponentName className) {
			DokuwikiActivity.this.service = null;
			onServiceUnbound();
		}

	};

	protected void onServiceBound() { }

	protected void onServiceUnbound() { }

	@Override
	protected void onStart() {
		super.onStart();
		bindToService();
	}

	@Override
	protected void onStop() {
		super.onStop();
		unbindFromService();
	}

	private void bindToService() {
		Intent i = new Intent(this, DokuwikiService.class);
		bindService(i, serviceConnection, Context.BIND_AUTO_CREATE);
	}

	private void unbindFromService() {
		unbindService(serviceConnection);
	}

	protected final boolean isServiceBound() {
		return service != null;
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

	protected void onPageLoadedCallback(String pageHTML, long id) { }

	public void onPageLoaded(final String pageHtml, final long id) {
		runOnUiThread(new Runnable() {
			public void run() {
				onPageLoadedCallback(pageHtml, id);
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