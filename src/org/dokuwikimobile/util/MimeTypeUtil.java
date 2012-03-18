package org.dokuwikimobile.util;

/**
 *
 * @author timroes
 */
public class MimeTypeUtil {
	
	public static String getFileType(byte[] file) {

		if(file[0] == (byte)0x89 && file[1] == 0x50 && file[2] == 0x4e && file[3] == 0x47)
			return "image/png";
		else if(file[0] == (byte)0xff && file[1] == (byte)0xd8)
			return "image/jpeg";
		else if(file[0] == (byte)0x47 && file[1] == (byte)0x49)
			return "image/gif";

		return null;
		
	}
	
}
