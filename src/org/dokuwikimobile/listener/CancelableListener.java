package org.dokuwikimobile.listener;

import org.dokuwikimobile.xmlrpc.DokuwikiXMLRPCClient.Canceler;
import org.dokuwikimobile.xmlrpc.ErrorCode;

/**
 *
 * @author Tim Roes <mail@timroes.de>
 */
public interface CancelableListener {
	
	/**
	 * Will be called, whenever a call with the xmlrpc interface is made.
	 * This indicates that some network traffic has just begun. A Canceler
	 * object is passed to this method, that can be used to cancel that call.
	 * This method can be called multiple times, for subsequent calls.
	 * Since only the first call to this method will be made in the same thread,
	 * as the request, be sure not to touch the UI in this thread, unlike
	 * you are absolutely sure, that the call you made, will only lead to one 
	 * onStartLoading call (e.g. the login call).
	 * 
	 * @param cancel The canceler to cancel the call.
	 * @param id The id of the call.
	 */
	public void onStartLoading(Canceler cancel, long id);

	/**
	 * Will be called, whenever a call to the xmlrpc interface ended loading.
	 * This method should be the last callback made for a specific requested call.
	 * Any listener of an 'original call' can be assume, that after this method 
	 * has been called on it, no further information will be passed related,
	 * to the original call.
	 * This method won't return in the the UI thread, so be sure that the implementation
	 * doesn't touch the UI in the returning thread.
	 * 
	 * @param id The id of the call.
	 */
	public void onEndLoading(long id);
        
	public void onError(ErrorCode error, long id);
	
}