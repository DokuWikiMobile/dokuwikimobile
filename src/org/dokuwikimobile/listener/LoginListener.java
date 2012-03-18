package org.dokuwikimobile.listener;

/**
 *
 * @author Tim Roes
 */
public interface LoginListener extends CancelableListener {

	public void onLogin(boolean succeeded, long id);

}
