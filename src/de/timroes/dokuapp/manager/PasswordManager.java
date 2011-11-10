package de.timroes.dokuapp.manager;

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

	public static PasswordManager get(Context ctx) {
		if(instance == null) {
			instance = new PasswordManager(ctx);
		} else {
			instance.context = ctx;
		}
		return instance;
	}

	private Context context;

	private PasswordManager(Context ctx) {
		this.context = ctx;	
	}

	public void saveLoginData(String username, String password) {
		Editor edit = context.getSharedPreferences(PASSWORD_MANAGER_FILE, Context.MODE_PRIVATE).edit();
		edit.putString(USERNAME, username);
		edit.putString(PASSWORD, password);
		edit.commit();
	}

	public boolean hasLoginData() {
		SharedPreferences sharedPreferences = context.getSharedPreferences(PASSWORD_MANAGER_FILE, Context.MODE_PRIVATE);
		return sharedPreferences.contains(USERNAME) && sharedPreferences.contains(PASSWORD);
	}

	public String getUsername() {
		return context.getSharedPreferences(PASSWORD_MANAGER_FILE, Context.MODE_PRIVATE)
				.getString(USERNAME, "");
	}

	public String getPassword() {
		return context.getSharedPreferences(PASSWORD_MANAGER_FILE, Context.MODE_PRIVATE)
				.getString(PASSWORD, "");
	}

	public void clearLoginData() {
		Editor edit = context.getSharedPreferences(PASSWORD_MANAGER_FILE, Context.MODE_PRIVATE).edit();
		edit.remove(USERNAME).remove(PASSWORD);
		edit.commit();
	}
	
}