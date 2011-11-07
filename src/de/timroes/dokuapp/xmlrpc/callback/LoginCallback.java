package de.timroes.dokuapp.xmlrpc.callback;

/**
 *
 * @author Tim Roes
 */
public interface LoginCallback extends ErrorCallback {

	public void onLogin(boolean succeeded, long id);

}
