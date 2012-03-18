package org.dokuwikimobile.service;

/**
 *
 * @author Tim Roes
 */
public interface ServiceConnectorListener {
	
	public void onServiceBound(DokuwikiService service);

	public void onServiceUnbound();
	
}