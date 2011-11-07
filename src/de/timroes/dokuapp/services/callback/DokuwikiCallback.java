package de.timroes.dokuapp.services.callback;

import de.timroes.axmlrpc.XMLRPCException;
import de.timroes.axmlrpc.XMLRPCServerException;

/**
 *
 * @author Tim Roes
 */
public interface DokuwikiCallback {

	public void onPageLoaded(String pageHtml, long id);

	public void onLogin(boolean succeeded, long id);

	public void onError(XMLRPCException error, long id);

	public void onServerError(XMLRPCServerException error, long id);

}