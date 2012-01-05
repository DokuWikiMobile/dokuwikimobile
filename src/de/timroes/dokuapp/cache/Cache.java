package de.timroes.dokuapp.cache;

import de.timroes.dokuapp.content.Attachment;
import de.timroes.dokuapp.content.Page;
import de.timroes.dokuapp.util.FileUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Tim Roes
 */
public class Cache implements Storage {

	private Map<String,Page> pages = new HashMap<String,Page>();

	private static final String PAGE_DIR = "pages";
	private static final String MEDIA_DIR = "media";

	private final File pageDir;
	private final File mediaDir;

	public Cache(String cacheDir) {
		pageDir = new File(cacheDir + File.separator + PAGE_DIR);
		mediaDir = new File(cacheDir + File.separator + MEDIA_DIR);

		// Create directories
		if(!pageDir.exists())
			pageDir.mkdir();
		if(!mediaDir.exists())
			mediaDir.mkdir();

		pages = loadPagesFromDisk();
	}
	
	/**
	 * Add a page to the cache if it is allready in there it will be overwritten.
	 * 
	 * @param page The page to add to the cache.
	 */
	public void addPage(Page page) {
		pages.put(page.getPageName(), page);
		savePage(page);
	}
	
	public Page getPage(String pagename) {
		return pages.get(pagename);
	}

	public void addAttachment(Attachment att) {
		saveAttachment(att);
	}

	public Attachment getAttachment(String id) {
		return loadAttachment(new File(mediaDir, id));
	}
	
	/**
	 * Clear the cache. Will delete all saved cache files.
	 */
	public void clear() {
		FileUtil.clearDirectory(pageDir);
		FileUtil.clearDirectory(mediaDir);
		pages.clear();
	}

	/**
	 * Get the current needed size of the cache.
	 * 
	 * @return The size of all cache files in bytes.
	 */
	public int getCacheSize() {
		return FileUtil.getDirectorySize(mediaDir)
				+ FileUtil.getDirectorySize(pageDir);
	}

	private void savePage(Page page) {

		ObjectOutputStream obj = null;
		try {
			FileOutputStream stream = new FileOutputStream(new File(pageDir, page.getPageName()));
			obj = new ObjectOutputStream(stream);
			obj.writeObject(page);
		} catch (IOException ex) {
			Logger.getLogger(Cache.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			try {
				obj.close();
			} catch (IOException ex) { 
				// Ignore if closing of objectstream fails.
			}
		}
		
	}

	private void saveAttachment(Attachment att) {

		ObjectOutputStream obj = null;
		try {
			FileOutputStream stream = new FileOutputStream(new File(mediaDir, att.getId()));
			obj = new ObjectOutputStream(stream);
			obj.writeObject(att);
		} catch (IOException ex) {
			Logger.getLogger(Cache.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			try {
				obj.close();
			} catch (IOException ex) { 
				// Ignore if closing of objectstream fails.
			}
		}

	}

	private Page loadPage(File file) {

		Page p = null;
		ObjectInputStream obj = null;
		try {
			FileInputStream stream = new FileInputStream(file);
			obj = new ObjectInputStream(stream);
			p = (Page)obj.readObject();
			p.setAttachmentStorage(this);
		} catch (OptionalDataException ex) {
			// If data is broken just delete cache file
			file.delete();
		} catch (ClassNotFoundException ex) {
			// If data is broken just delete cache file
			file.delete();
		} catch (IOException ex) {
			Logger.getLogger(Cache.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			try {
				obj.close();
			} catch (IOException ex) {
				// Ignore if closing of objectstream fails.
			}
		}

		return p;
		
	}

	private Attachment loadAttachment(File file) {

		Attachment a = null;
		ObjectInputStream obj = null;
		try {
			FileInputStream stream = new FileInputStream(file);
			obj = new ObjectInputStream(stream);
			a = (Attachment)obj.readObject();
		} catch(StreamCorruptedException ex) {
			// If data is broken just delete cache file
			file.delete();	
		} catch (OptionalDataException ex) {
			// If data is broken just delete cache file
			file.delete();
		} catch (ClassNotFoundException ex) {
			// If data is broken just delete cache file
			file.delete();
		} catch (IOException ex) {
			Logger.getLogger(Cache.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			try {
				obj.close();
			} catch (Exception ex) {
				// Ignore if closing of objectstream fails.
			}
		}

		return a;	
		
	}
		
	private Map<String,Page> loadPagesFromDisk() {
		
		Map<String,Page> page = new HashMap<String, Page>();

		Page p;
		for(File f : pageDir.listFiles()) {
			p = loadPage(f);
			page.put(p.getPageName(), p);
		}

		return page;
		
	}

}