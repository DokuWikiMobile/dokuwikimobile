package org.dokuwikimobile;

import android.app.Application;
import android.content.Context;
import org.dokuwikimobile.exception.ApplicationContextMissingError;

/**
 *
 * @author Tim Roes <mail@timroes.de>
 */
public class DokuwikiApplication extends Application {

	private static Context context;

	@Override
	public void onCreate() {
		super.onCreate();
		DokuwikiApplication.context = getApplicationContext();
	}

	public static Context getAppContext() {
		
		if(context == null) {
			throw new ApplicationContextMissingError("The application requested an ApplicationContext, "
					+ "which is currently not available.");
		}
		
		return context;

	}

}