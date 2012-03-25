package org.dokuwikimobile.listener;

import de.timroes.axmlrpc.XMLRPCException;
import org.dokuwikimobile.xmlrpc.DokuwikiXMLRPCClient.Canceler;

/**
 *
 * @author Tim Roes <mail@timroes.de>
 */
public interface CancelableListener {
	
	public void onStartLoading(Canceler cancel, long id);

	public void onEndLoading(long id);
        
	public void onError(XMLRPCException error, long id);
	
}