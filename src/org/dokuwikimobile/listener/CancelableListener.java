package org.dokuwikimobile.listener;

import de.timroes.axmlrpc.XMLRPCException;
import org.dokuwikimobile.xmlrpc.DokuwikiXMLRPCClient.Canceler;

/**
 *
 * @author Tim Roes <mail@timroes.de>
 */
public interface CancelableListener {
	
	public void startLoading(Canceler cancel);

	public void endLoading();
        
	public void onError(XMLRPCException error, long id);
	
}