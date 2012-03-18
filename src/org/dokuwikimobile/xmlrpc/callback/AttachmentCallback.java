package org.dokuwikimobile.xmlrpc.callback;

import org.dokuwikimobile.model.Attachment;

/**
 *
 * @author Tim Roes
 */
public interface AttachmentCallback extends ErrorCallback {
	
	public void onAttachmentLoaded(Attachment att, long id);
	
}