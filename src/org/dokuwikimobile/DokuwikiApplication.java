package org.dokuwikimobile;

import android.app.Application;
import android.content.Context;
import org.dokuwikimobile.exception.ApplicationContextMissingError;

/**
 * The DokuwikiApplication is the main application of DokuWikiMobile.
 * It offers access to the application context from anywhere in the application.
 * 
 * @author Tim Roes <mail@timroes.de>
 */
public class DokuwikiApplication extends Application {

	public static final String LOGGER_NAME = "DokuWikiMobile";

	private static Context context;

	/**
	 * This method will be called during creation of the application. It stores
	 * the application context, to make it available through a static method.
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		DokuwikiApplication.context = getApplicationContext();
	}

	/**
	 * Return the application context of the application.
	 * This will be set on startup of application and should be available during
	 * the whole lifetime of the application.
	 * 
	 * @return The application context.
	 */
	public static Context getAppContext() {
		
		if(context == null) {
			throw new ApplicationContextMissingError("The application requested an ApplicationContext, "
					+ "which is currently not available.");
		}
		
		return context;

	}

}