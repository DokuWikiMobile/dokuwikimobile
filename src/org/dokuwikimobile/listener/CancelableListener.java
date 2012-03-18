package org.dokuwikimobile.listener;

import org.dokuwikimobile.xmlrpc.DokuwikiXMLRPCClient.Canceler;

/**
 *
 * @author Tim Roes
 */
public interface CancelableListener {
	
	public void startLoading(Canceler cancel);

	public void endLoading();
	
}