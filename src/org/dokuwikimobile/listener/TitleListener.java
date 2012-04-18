package org.dokuwikimobile.listener;

/**
 *
 * @author Tim Roes <mail@timroes.de>
 */
public interface TitleListener extends CancelableListener {
	
	public void onTitle(String title, long id);

}
