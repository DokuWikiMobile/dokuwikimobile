package org.dokuwikimobile.android.activities.dialogs;

import android.app.Dialog;
import android.content.Context;
import org.dokuwikimobile.android.services.DokuwikiService;
import org.dokuwikimobile.android.services.DokuwikiServiceConnector;
import org.dokuwikimobile.android.services.ServiceConnectorListener;

/**
 *
 * @author Tim Roes
 */
public abstract class DokuwikiDialog extends Dialog implements ServiceConnectorListener {

	protected final DokuwikiServiceConnector connector;

	protected final Context context;
		
	public DokuwikiDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		this.context = context;
		connector = new DokuwikiServiceConnector(context, this);
	}

	public DokuwikiDialog(Context context, int theme) {
		super(context, theme);
		this.context = context;
		connector = new DokuwikiServiceConnector(context, this);
	}

	public DokuwikiDialog(Context context) {
		super(context);
		this.context = context;
		connector = new DokuwikiServiceConnector(context, this);
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

}