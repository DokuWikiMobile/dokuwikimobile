package org.dokuwikimobile.listener;

import org.dokuwikimobile.Dokuwiki;

/**
 *
 * @author Tim Roes <mail@timroes.de>
 */
public interface DokuwikiCreationListener extends CancelableListener {

	public void onDokuwikiCreated(Dokuwiki dokuwiki);
	
}
