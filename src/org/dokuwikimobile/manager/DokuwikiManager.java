package org.dokuwikimobile.manager;

import android.content.Context;
import java.util.HashMap;
import java.util.Map;
import org.dokuwikimobile.DokuwikiApplication;
import org.dokuwikimobile.model.Dokuwiki;
import org.dokuwikimobile.xmlrpc.DokuwikiXMLRPCClient;


/**
 * The DokuwikiManager handles one complete Dokuwiki. It manages the PasswordManager,
 * Caches and the DokuwikiXMLRPCClient to this Dokuwiki. This class can only exist once
 * for each Dokuwiki. It has a static factory method to recieve or create the instance
 * for a specific Dokuwiki.
 * 
 * @author Tim Roes <mail@timroes.de>
 */
public class DokuwikiManager {

	private static Map<Dokuwiki, DokuwikiManager> instances 
			= new HashMap<Dokuwiki, DokuwikiManager>();
	
	public static DokuwikiManager get(Dokuwiki dokuwiki) {

		// If dokuwiki has already a DokuwikiManager, return it.
		if(instances.containsKey(dokuwiki)) {
			return instances.get(dokuwiki);
		}

		Context context = DokuwikiApplication.getAppContext();
		
		DokuwikiManager manager = new DokuwikiManager();

		manager.dokuwiki = dokuwiki;
		manager.passwordManager = new PasswordManager(
				context.getSharedPreferences(dokuwiki.getMd5hash(), Context.MODE_PRIVATE));
		// TODO: Remove password manager from dokuwikiXMLRPCclient
		manager.xmlrpcClient = new DokuwikiXMLRPCClient(dokuwiki.getUrl(), null);

		instances.put(dokuwiki, manager);
		
		return manager;
		
	}
	
	private Dokuwiki dokuwiki;
	private PasswordManager passwordManager;
	private DokuwikiXMLRPCClient xmlrpcClient;

	private DokuwikiManager() {

	}

	public void login(String username, String password) {
		passwordManager.saveLoginData(username, password);
	}

	public void logout() {
		passwordManager.clearLoginData();
		xmlrpcClient.clearLoginData();
	}

}
