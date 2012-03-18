package org.dokuwikimobile.service;

import org.dokuwikimobile.xmlrpc.DokuwikiXMLRPCClient.Canceler;

/**
 *
 * @author Tim Roes
 */
public interface LoadingListener {
	
	public void startLoading(Canceler cancel);

	public void endLoading();
	
}