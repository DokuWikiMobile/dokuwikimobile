package org.dokuwikimobile.manager;

import android.content.Context;
import de.timroes.axmlrpc.XMLRPCException;
import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.dokuwikimobile.DokuwikiApplication;
import org.dokuwikimobile.cache.Cache;
import org.dokuwikimobile.listener.CancelableListener;
import org.dokuwikimobile.listener.LoginListener;
import org.dokuwikimobile.listener.SearchListener;
import org.dokuwikimobile.model.Dokuwiki;
import org.dokuwikimobile.model.LoginData;
import org.dokuwikimobile.listener.PageListener;
import org.dokuwikimobile.xmlrpc.DokuwikiXMLRPCClient;
import org.dokuwikimobile.xmlrpc.DokuwikiXMLRPCClient.Canceler;


/**
 * The DokuwikiManager handles one complete Dokuwiki as a facade. 
 * It manages the PasswordManager, Caches and the DokuwikiXMLRPCClient to this 
 * Dokuwiki. This class can only exist once for each Dokuwiki.
 * 
 * It has a static factory method to receive or create the instance
 * for a specific Dokuwiki. The single isntances are singleton classes, saved
 * in a singleton catalog.
 * 
 * @author Tim Roes <mail@timroes.de>
 */
public class DokuwikiManager {

	/// ==================
	/// STATIC METHODS
	/// ==================

	private static Map<Dokuwiki, DokuwikiManager> instances 
			= new HashMap<Dokuwiki, DokuwikiManager>();
	
	/**
	 * This will return the instance of DokuwikiManager for a specific Dokuwiki.
	 * If an instance of the specific DokuwikiManager already exist, it will be 
	 * returned. If not, it will be generated, stored and returned.
	 * 
	 * @param dokuwiki The Dokuwiki a manager should be returned for.
	 * @return The DokuwikiManager for the requested Dokuwiki.
	 */
	public static DokuwikiManager get(Dokuwiki dokuwiki) {

		// If this dokuwiki has already a DokuwikiManager, return it.
		if(instances.containsKey(dokuwiki)) {
			return instances.get(dokuwiki);
		}

		// Retrieve application context
		Context context = DokuwikiApplication.getAppContext();
		
		// Generate new DokuwikiManager
		DokuwikiManager manager = new DokuwikiManager();

		// Store dokuwiki information
		manager.dokuwiki = dokuwiki;
		// Initialize password manager for this dokuwiki
		manager.passwordManager = new PasswordManager(
				context.getSharedPreferences(dokuwiki.getMd5hash(), Context.MODE_PRIVATE));
		// TODO: Remove password manager from dokuwikiXMLRPCclient
		manager.xmlrpcClient = new DokuwikiXMLRPCClient(dokuwiki.getUrl());

		// Pass login data to XMLRPC client
		if(manager.passwordManager.hasLoginData()) {
			manager.xmlrpcClient.setLoginData(manager.passwordManager.getLoginData());
		}

		// Create cache within cachedir/wikimd5hash/
		manager.cache = new Cache(new File(context.getCacheDir(), dokuwiki.getMd5hash()));

		// Save instance in singleton map
		instances.put(dokuwiki, manager);
		
		return manager;
		
	}

	/**
	 * Returns a collection of all currently instantiated dokuwikis.
	 * This is NOT necessarily a collection of all dokuwikis, saved in this app.
	 * 
	 * @return A collection of all instantiated dokuwikis.
	 */
	public static Collection<DokuwikiManager> getAll() {
		return instances.values();
	}


	/// ====================
	/// INSTANCE
	/// ====================
	
	private Dokuwiki dokuwiki;
	private PasswordManager passwordManager;
	private DokuwikiXMLRPCClient xmlrpcClient;
	private Cache cache;

	private Listener listener = new Listener();

	private DokuwikiManager() { }

	/// ====================
	/// CACHE FUNCTIONS
	/// ====================

	/**
	 * Returns the size of the cache in byte.
	 * 
	 * @return Size of cache used in bytes.
	 */
	public int getCacheSize() {
		return cache.getCacheSize();
	}

	/**
	 * Clear the whole cache of this dokuwiki. 
	 * This will delete all cached pages and media. Saved preferences will not 
	 * be touched.
	 */
	public void clearCache() {
		cache.clear();
	}

	/// ====================
	/// PAGE FUNCTIONS
	/// ====================

	public void getPage(PageListener listener, String pageName) {
		// TODO: Implement page get
	}

	/// ====================
	/// SEARCHING
	/// ====================

	public void search(SearchListener listener, String query) {
		// TODO: Implement search
	}

	/// ====================
	/// AUTHENTICATION
	/// ====================
	
	public void login(LoginListener listerner, LoginData login, boolean saveLogin) {

		if(saveLogin) {
			// Save login data in password manager
			passwordManager.saveLoginData(login);
		}

		// Set login data in xmlrpc interface
		xmlrpcClient.setLoginData(login);

		// TODO: move startloading to xmlrpc
		Canceler canceler = xmlrpcClient.login(this.listener, login);
		listener.startLoading(canceler);
		listener.put(canceler.getId(), listener);

	}

	/**
	 * Logs out the user from the dokuwiki.
	 */
	public void logout() {
		passwordManager.clearLoginData();
		xmlrpcClient.clearLoginData();
	}


	/// ====================
	/// LISTENER
	/// ====================
	
	/**
	 * The Listener class handles all callbacks from the XMLRPC interface.
	 * It stores a map of all call ids and their listeners.
	 */
	private class Listener implements LoginListener {
		
		private Map<Long, CancelableListener> listeners = new HashMap<Long, CancelableListener>();

		/**
		 * Stores a listener with its corresponding id. This must be called after
		 * a call has been made, so this subclass can match the response to the listener.
		 * 
		 * @param id The id of the call.
		 * @param listener The listener, that wants to be noticed about the answer.
		 */
		public void put(long id, CancelableListener listener) {
			listeners.put(id, listener);
		}

		/**
		 * Gets the listener for a specific callback. This will take the callback id,
		 * the type of the listener expected to be returned and whether the listener 
		 * should be removed from listener map.
		 * 
		 * @param id The id of the callback.
		 * @param listenerType The type of listener expected for that id.
		 * @return The listener with the given type or null if none has been found,
		 * 		or had the wrong type.
		 */
		private <T> T getListener(long id, Class<T> listenerType) {
			return getListener(id, listenerType, false);
		}

		/**
		 * Gets or removes the listener for a specific callback. This will take the callback id,
		 * the type of the listener expected to be returned and whether the listener 
		 * should be removed from listener map.
		 * 
		 * @param id The id of the callback.
		 * @param listenerType The type of listener expected for that id.
		 * @param removeListener Whether the listener should be removed from listener map.
		 * @return The listener with the given type or null if none has been found,
		 * 		or had the wrong type.
		 */
		private <T> T getListener(long id, Class<T> listenerType, boolean removeListener) {
			
			CancelableListener listener = listeners.get(id);

			// If no listener existed, return null
			if(listener == null) {
				return null;
			}

			// If listener has wrong type, return null
			if(listenerType != listener.getClass()) {
				return null;
			}

			if(removeListener) {
				return (T)listeners.remove(id);
			}
			
			return (T)listener;
			
		}

		public void onLogin(boolean succeeded, long id) {

			// If login data was wrong, delete it from password manager and xmlrpc
			// client
			if(!succeeded) {
				passwordManager.clearLoginData();
				xmlrpcClient.clearLoginData();
			}

			// Notify original listener
			LoginListener l;
			if((l = getListener(id, LoginListener.class, true)) != null) {
				l.onLogin(succeeded, id);
			}

		}

		// TODO: needs id of call
		public void startLoading(Canceler cancel) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		public void endLoading() {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		/**
		 * This method will be called if an error occurred during a call.
		 * The DokuwikiManager will just remove the listener for this call and
		 * forward the error message to it.
		 * 
		 * @param error The error returned from XMLRPC client.
		 * @param id The id of the call.
		 */
		public void onError(XMLRPCException error, long id) {
			getListener(id, CancelableListener.class, true).onError(error, id);
		}
		
	}

}
