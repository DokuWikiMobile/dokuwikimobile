package org.dokuwikimobile.manager;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import org.dokuwikimobile.model.LoginData;

/**
 *
 * @author Tim Roes <mail@timroes.de>
 */
public class PasswordManager {

	private final static String USERNAME = "USERNAME";
	private final static String PASSWORD = "PASSWORD";
	
	private SharedPreferences sharedPrefs;

	public PasswordManager(SharedPreferences sharedPrefs) {
		this.sharedPrefs = sharedPrefs;
	}

	public void saveLoginData(LoginData login) {
		Editor edit = sharedPrefs.edit();
		edit.putString(USERNAME, login.Username);
		edit.putString(PASSWORD, login.Password);
		edit.commit();
	}

	public boolean hasLoginData() {
		return sharedPrefs.contains(USERNAME) && sharedPrefs.contains(PASSWORD);
	}

	public LoginData getLoginData() {
		return new LoginData(sharedPrefs.getString(USERNAME, ""),
				sharedPrefs.getString(PASSWORD, ""));
	}

	public void clearLoginData() {
		Editor edit = sharedPrefs.edit();
		edit.remove(USERNAME).remove(PASSWORD);
		edit.commit();
	}
	
}