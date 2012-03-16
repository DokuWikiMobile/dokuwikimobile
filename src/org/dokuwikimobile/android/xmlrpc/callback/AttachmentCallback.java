package org.dokuwikimobile.android.xmlrpc.callback;

import org.dokuwikimobile.android.content.Attachment;

/**
 *
 * @author Tim Roes
 */
public interface AttachmentCallback extends ErrorCallback {
	
	public void onAttachmentLoaded(Attachment att, long id);
	
}