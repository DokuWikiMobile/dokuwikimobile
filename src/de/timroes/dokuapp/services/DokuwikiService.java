package de.timroes.dokuapp.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import de.timroes.dokuapp.Settings;
import de.timroes.dokuapp.manager.PasswordManager;
import de.timroes.dokuapp.xmlrpc.DokuwikiXMLRPCClient;
import de.timroes.dokuapp.xmlrpc.callback.LoginCallback;
import de.timroes.dokuapp.xmlrpc.callback.PageLoadedCallback;
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
	private DokuwikiXMLRPCClient client;

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
			client = new DokuwikiXMLRPCClient(new URL(Settings.XMLRPC_URL), 
					PasswordManager.get(this), userAgent);
		} catch (MalformedURLException ex) {
			Logger.getLogger(DokuwikiService.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	public long getPage(PageLoadedCallback callback, String pagename) {
		return client.getPageHTML(callback, pagename);
	}

	public long login(LoginCallback callback, String username, String password) {
		return client.login(callback, username, password);
	}

	public void logout() {
		client.logout();
	}

}