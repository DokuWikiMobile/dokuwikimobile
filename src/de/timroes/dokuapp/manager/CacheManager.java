package de.timroes.dokuapp.manager;

import android.content.Context;
import de.timroes.dokuapp.content.Page;
import de.timroes.dokuapp.services.PageLoadedListener;
import de.timroes.dokuapp.util.FileUtil;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Tim Roes
 */
public class CacheManager {
	
	private Map<String,Page> cache = new HashMap<String, Page>();
	private Context context;

	private final static String PAGE_DIR = "pages";
	private final static String MEDIA_DIR = "media";
	
	private static File pageDir;
	private static File mediaDir;
	
	public CacheManager(Context context) {
		pageDir = new File(context.getCacheDir().getAbsolutePath() + File.separator + PAGE_DIR);
		mediaDir = new File(context.getCacheDir().getAbsolutePath() + File.separator + MEDIA_DIR);
		this.context = context;
//		cache = loadCacheFromDisk();

		if(!pageDir.exists())
			pageDir.mkdir();
		if(!mediaDir.exists())
			mediaDir.mkdir();
	}

	public long getPage(PageLoadedListener listener, String pagename) {
		
		return 0;
		
	}

	private Map<String,Page> loadCacheFromDisk() {

		File[] files = pageDir.listFiles();

		for(File f : files) {
			System.out.println(f.getName());
		}

		return null;

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

	public void addPage(Page page) {
		savePage(page);
	}

	/**
	 * Clear the cache. Deletes all pages and media from the cache directory.
	 */
	public void clearCache() {
		FileUtil.clearDirectory(pageDir);
		FileUtil.clearDirectory(mediaDir);
		cache.clear();
	}

	private void savePage(Page page) {

		ObjectOutputStream obj = null;
		try {
			FileOutputStream stream = new FileOutputStream(new File(pageDir, page.getPageName()));
			obj = new ObjectOutputStream(stream);
			obj.writeObject(page);
		} catch (IOException ex) {
			Logger.getLogger(CacheManager.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			try {
				obj.close();
			} catch (IOException ex) { 
				// Ignore if closing of objectstream fails.
			}
		}
		
	}
	
}
