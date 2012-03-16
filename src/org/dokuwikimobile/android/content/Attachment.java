package org.dokuwikimobile.android.content;

import de.timroes.base64.Base64;
import java.io.Serializable;

/**
 *
 * @author Tim Roes
 */
public class Attachment implements Serializable {

	private static final long serialVersionUID = 1;
	
	private String id;
	private byte[] data;

	public Attachment(String id, byte[] data) {
		this.id = id;
		this.data = data;
	}
	
	public String getId() {
		return id;
	}

	public byte[] getData() {
		return data;
	}

	public String getBase64Data() {
		return Base64.encode(data);
	}
}
