package org.dokuwikimobile.listener;

/**
 *
 * @author Daniel Baelz <daniel.baelz@lysandor.de>
 */
public interface VersionListener extends CancelableListener {

	public void onVersion(int version, long id);

}
