package org.dokuwikimobile.model;

import de.timroes.base64.Base64;
import java.io.Serializable;
import org.dokuwikimobile.util.MimeTypeUtil;

/**
 *
 * @author Daniel Baelz <daniel.baelz@lysandor.de>
 * @author Tim Roes
 */
public class Attachment implements Serializable {

	private static final long serialVersionUID = 1;
	
	private String id;
	private byte[] data;
	private String type;
	
	public Attachment(String id, byte[] data) {
		this.id = id;
		this.data = data;
		this.type = MimeTypeUtil.getFileType(data);
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
	
	public String getType() {
		return type;
	}
}
