package org.dokuwikimobile.listener;

import org.dokuwikimobile.model.Attachment;

/**
 *
 * @author Tim Roes
 */
public interface AttachmentListener extends CancelableListener {
	
	public void onAttachmentLoaded(Attachment att, long id);
	
}