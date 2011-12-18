package de.timroes.dokuapp.services;

import de.timroes.dokuapp.xmlrpc.DokuwikiXMLRPCClient.Canceler;

/**
 *
 * @author Tim Roes
 */
public interface LoadingListener {
	
	public void startLoading(Canceler cancel);

	public void endLoading();
	
}