package de.timroes.dokuapp.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import de.timroes.dokuapp.exceptions.DokuwikiError;
import de.timroes.dokuapp.exceptions.DokuwikiServerError;
import de.timroes.dokuapp.services.DokuwikiService;
import de.timroes.dokuapp.services.DokuwikiService.DokuwikiBinder;
import de.timroes.dokuapp.services.callback.DokuwikiCallback;

/**
 *
 * @author Tim Roes
 */
public abstract class DokuwikiActivity extends Activity implements DokuwikiCallback {

	protected DokuwikiService service;

	private ServiceConnection serviceConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className, IBinder service) {
			DokuwikiActivity.this.service = ((DokuwikiBinder)service).getService();
			onServiceBound();
		}

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

	protected boolean isServiceBound() {
		return service != null;
	}

	public void onError(DokuwikiError error, long id) { }

	public void onPageLoaded(String pageHtml, long id) { }

	public void onServerError(DokuwikiServerError error, long id) { }

}