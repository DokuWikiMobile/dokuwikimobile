package de.timroes.dokuapp.xmlrpc.callback;

import de.timroes.dokuapp.content.Attachment;

/**
 *
 * @author Tim Roes
 */
public interface AttachmentCallback extends ErrorCallback {
	
	public void onAttachmentLoaded(Attachment att, long id);
	
}