package de.timroes.dokuapp.cache;

import de.timroes.dokuapp.content.Attachment;

/**
 *
 * @author Tim Roes
 */
public interface AttachmentStorage {

	public Attachment getAttachment(String id);

}
