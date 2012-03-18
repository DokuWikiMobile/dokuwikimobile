package org.dokuwikimobile.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 *
 * @author Tim Roes
 */
public class PasswordManager {

	private final static String PASSWORD_MANAGER_FILE = PasswordManager.class.getName();
	private final static String USERNAME = "USERNAME";
	private final static String PASSWORD = "PASSWORD";
	
	private static PasswordManager instance;

	// TODO: Remove singleton functionality, is now handled by DokuwikiManager
	public static PasswordManager get(Context ctx) {
		if(instance == null) {
			instance = new PasswordManager(ctx.getSharedPreferences(PASSWORD_MANAGER_FILE, Context.MODE_PRIVATE));
		} else {
			instance.sharedPrefs = ctx.getSharedPreferences(PASSWORD_MANAGER_FILE, Context.MODE_PRIVATE);
		}
		return instance;
	}

	private SharedPreferences sharedPrefs;

	public PasswordManager(SharedPreferences sharedPrefs) {
		this.sharedPrefs = sharedPrefs;
	}

	public void saveLoginData(String username, String password) {
		Editor edit = sharedPrefs.edit();
		edit.putString(USERNAME, username);
		edit.putString(PASSWORD, password);
		edit.commit();
	}

	public boolean hasLoginData() {
		return sharedPrefs.contains(USERNAME) && sharedPrefs.contains(PASSWORD);
	}

	public String getUsername() {
		return sharedPrefs.getString(USERNAME, "");
	}

	public String getPassword() {
		return sharedPrefs.getString(PASSWORD, "");
	}

	public void clearLoginData() {
		Editor edit = sharedPrefs.edit();
		edit.remove(USERNAME).remove(PASSWORD);
		edit.commit();
	}
	
}