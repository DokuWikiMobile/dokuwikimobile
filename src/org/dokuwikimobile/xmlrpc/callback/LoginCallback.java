package org.dokuwikimobile.xmlrpc.callback;

/**
 *
 * @author Tim Roes
 */
public interface LoginCallback extends ErrorCallback {

	public void onLogin(boolean succeeded, long id);

}
