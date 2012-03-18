package org.dokuwikimobile.listener;

import de.timroes.axmlrpc.XMLRPCException;
import de.timroes.axmlrpc.XMLRPCServerException;

/**
 *
 * @author Tim Roes
 */
public interface ErrorListener {

	public void onError(XMLRPCException error, long id);

	public void onServerError(XMLRPCServerException error, long id);

}