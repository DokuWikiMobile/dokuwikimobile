package de.timroes.dokuapp.util;

import java.io.File;

/**
 * The FileUtil class offers several utility methods to handle files in java.
 * 
 * @author Tim Roes
 */
public class FileUtil {
	
	/**
	 * Calculates the size of a directory and all containign files and subdirectories.
	 * 
	 * @param f A directory to calculate the size of.
	 * @return The size of the directory and all containing files in bytes.
	 */
	public static int getDirectorySize(File f) {
		
		int size = 0;

		if(f.isDirectory())	{
			for(File file : f.listFiles()) {
				size += getDirectorySize(file);
			}
		} else {
			size += f.length();
		}

		return size;

	}

	/**
	 * Clear a directory. This will delete all files and subdirectories in the
	 * given directory.
	 * 
	 * @param f The directory to clear.
	 */
	public static void clearDirectory(File f) {

		for(File file : f.listFiles()) {
			file.delete();
		}
		
	}
	
}
