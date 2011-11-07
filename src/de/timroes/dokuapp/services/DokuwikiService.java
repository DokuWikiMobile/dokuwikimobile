package de.timroes.dokuapp.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import de.timroes.dokuapp.Settings;
import de.timroes.dokuapp.services.DokuwikiClientCallbacks.CallType;
import de.timroes.dokuapp.services.callback.DokuwikiCallback;
import de.timroes.dokuapp.xmlrpc.DokuwikiClient;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Tim Roes
 */
public class DokuwikiService extends Service {

	private DokuwikiBinder binder = new DokuwikiBinder();
	private DokuwikiClientCallbacks callbacks = new DokuwikiClientCallbacks();
	private DokuwikiClient client;

	/**
	 * A Binder interface that allows access to the public methods of this service,
	 * when bound to it.
	 */
	public class DokuwikiBinder extends Binder {
		/**
		 * Get the bounded service.
		 * @return The DokuwikiService to which you bound.
		 */
		public DokuwikiService getService() {
			return DokuwikiService.this;
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();

		try {
			String userAgent = getPackageName();
			client = new DokuwikiClient(new URL(Settings.XMLRPC_URL), userAgent, callbacks);
		} catch (MalformedURLException ex) {
			Logger.getLogger(DokuwikiService.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	public long getPage(String pagename, DokuwikiCallback callback) {
		long id = client.getPageHTML(pagename);
		return callbacks.addRequest(id, CallType.PAGE, callback);
	}

	public long login(String username, String password, DokuwikiCallback callback) {
		long id = client.login(username, password);
		return callbacks.addRequest(id, CallType.LOGIN, callback);
	}

}