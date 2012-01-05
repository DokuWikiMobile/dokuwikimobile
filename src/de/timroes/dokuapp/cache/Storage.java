package de.timroes.dokuapp.cache;

import de.timroes.dokuapp.content.Attachment;
import de.timroes.dokuapp.content.Page;

/**
 *
 * @author Tim Roes
 */
public interface Storage {

	public Page getPage(String id);
	
	public Attachment getAttachment(String id);

}
