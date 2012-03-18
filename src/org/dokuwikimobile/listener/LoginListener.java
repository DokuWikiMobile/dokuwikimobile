package org.dokuwikimobile.listener;

/**
 *
 * @author Tim Roes
 */
public interface LoginListener extends ErrorListener {

	public void onLogin(boolean succeeded, long id);

}
