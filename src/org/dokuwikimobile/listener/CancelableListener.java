package org.dokuwikimobile.listener;

import org.dokuwikimobile.xmlrpc.DokuwikiXMLRPCClient.Canceler;
import org.dokuwikimobile.xmlrpc.ErrorCode;

/**
 *
 * @author Tim Roes <mail@timroes.de>
 */
public interface CancelableListener {
	
	public void onStartLoading(Canceler cancel, long id);

	public void onEndLoading(long id);
        
	public void onError(ErrorCode error, long id);
	
}