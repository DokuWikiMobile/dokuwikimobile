package de.timroes.dokuapp.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import de.timroes.axmlrpc.XMLRPCCallback;
import de.timroes.axmlrpc.XMLRPCException;
import de.timroes.axmlrpc.XMLRPCServerException;
import de.timroes.dokuapp.Settings;
import de.timroes.dokuapp.services.callback.DokuwikiCallback;
import de.timroes.dokuapp.services.callback.OnErrorListener;
import de.timroes.dokuapp.services.callback.OnPageLoadedListener;
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
			client = new DokuwikiClient(new URL(Settings.XMLRPC_URL), userAgent, pageCallback);
		} catch (MalformedURLException ex) {
			Logger.getLogger(DokuwikiService.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	public long getPage(String pagename, DokuwikiCallback callback) {;
		long id = client.getPageHTML(pagename);
		return id;
	}

	private XMLRPCCallback pageCallback = new XMLRPCCallback() {

		public void onResponse(long id, Object result) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		public void onError(long id, XMLRPCException error) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		public void onServerError(long id, XMLRPCServerException error) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

	};

}