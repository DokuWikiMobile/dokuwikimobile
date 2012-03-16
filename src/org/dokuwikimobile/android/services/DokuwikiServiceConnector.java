package org.dokuwikimobile.android.services;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import org.dokuwikimobile.android.services.DokuwikiService.DokuwikiBinder;

/**
 *
 * @author Tim Roes
 */
public class DokuwikiServiceConnector {
	
	private Context context;
	private ServiceConnectorListener listener;
	private DokuwikiService service;

	private ServiceConnection serviceConnection = new ServiceConnection() {

		/**
		 * This method is called when the service has been connected. This will set the
		 * service variable and call the onServiceBound method, that should
		 * be overwritten in any subclass activity, that needs the service to be
		 * connected.
		 */
		public void onServiceConnected(ComponentName className, IBinder service) {
			DokuwikiServiceConnector.this.service = ((DokuwikiBinder)service).getService();
			listener.onServiceBound(DokuwikiServiceConnector.this.service);
		}

		/**
		 * This method is called when the connection to the service has terminated.
		 * It clears the service variable and call the onServiceUnbound method,
		 * that can be overwritten in any subclass activity, that needs to get
		 * informed.
		 */
		public void onServiceDisconnected(ComponentName className) {
			DokuwikiServiceConnector.this.service = null;
			listener.onServiceUnbound();
		}

	};	

	public DokuwikiServiceConnector(Context context, ServiceConnectorListener listener) {
		this.context = context;
		this.listener = listener;
	}

	public void bindToService() {
		Intent i = new Intent(context, DokuwikiService.class);
		context.bindService(i, serviceConnection, Context.BIND_AUTO_CREATE);
	}

	public void unbindFromService() {
		context.unbindService(serviceConnection);
	}

	public DokuwikiService getService() {
		return service;
	}

	public boolean isConnected() {
		return service != null;
	}
	
}
