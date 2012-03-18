package org.dokuwikimobile.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dokuwikimobile.Settings;
import org.dokuwikimobile.cache.CacheManager;
import org.dokuwikimobile.manager.PasswordManager;
import org.dokuwikimobile.xmlrpc.DokuwikiXMLRPCClient;
import org.dokuwikimobile.xmlrpc.callback.LoginCallback;
import org.dokuwikimobile.xmlrpc.callback.SearchCallback;

/**
 *
 * @author Tim Roes
 */
public class DokuwikiService extends Service {

	private DokuwikiBinder binder = new DokuwikiBinder();
	private DokuwikiXMLRPCClient client;
	private CacheManager cache;

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
			cache = new CacheManager(this, client);
		} catch (MalformedURLException ex) {
			Logger.getLogger(DokuwikiService.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	public long getPage(PageLoadedListener callback, LoadingListener loading, String pagename) {
		return cache.getPage(callback, loading, pagename);
	}

	public void login(LoginCallback callback, String username, String password) {
		client.login(callback, username, password);
	}

	public void search(SearchCallback callback, LoadingListener loading, String query) {
		cache.search(callback, loading, query);
	}

	public int getCacheSize() {
		return cache.getCacheSize();
	}

	public void clearCache() {
		cache.clearCache();
	}

	public void logout() {
		client.logout();
	}

}