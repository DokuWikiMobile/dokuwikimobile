package org.dokuwikimobile.ui.activity;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.widget.Toast;
import org.dokuwikimobile.android.R;
import org.dokuwikimobile.service.DokuwikiService;
import org.dokuwikimobile.service.DokuwikiServiceConnector;
import org.dokuwikimobile.service.ServiceConnectorListener;
import org.dokuwikimobile.util.StringUtil;

/**
 *
 * @author Tim Roes
 */
public class Preferences extends PreferenceActivity implements Preference.OnPreferenceClickListener,
		ServiceConnectorListener {

	private final static String CLEAR_LOGIN_DATA = "clearLoginData";
	private final static String CLEAR_CACHE = "clearCache";

	private DokuwikiServiceConnector connector;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		connector = new DokuwikiServiceConnector(this, this);
		connector.bindToService();

		addPreferencesFromResource(R.xml.preferences);

		((Preference)findPreference(CLEAR_LOGIN_DATA)).setOnPreferenceClickListener(this);
		((Preference)findPreference(CLEAR_CACHE)).setOnPreferenceClickListener(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		connector.unbindFromService();
	}

	public boolean onPreferenceClick(Preference pref) {

		if(CLEAR_LOGIN_DATA.equals(pref.getKey())) {
			clearLoginData();
		} else if(CLEAR_CACHE.equals(pref.getKey())) {
			clearCache();
		} else {
			return false;
		}
		
		return true;

	}

	private void clearLoginData() {
		connector.getService().logout();
		Toast.makeText(Preferences.this, R.string.logout_done, Toast.LENGTH_SHORT).show();
	}

	private void clearCache() {
		connector.getService().clearCache();
		updateCacheInfo();
	}

	public void onServiceBound(DokuwikiService service) {
		updateCacheInfo();
	}

	public void onServiceUnbound() { }

	private void updateCacheInfo() {
		// Get size of cache dir. Disable clear cache option if no cache files exists
		int cacheUsage = connector.getService().getCacheSize();
		((Preference)findPreference(CLEAR_CACHE)).setEnabled(cacheUsage <= 0);
		((Preference)findPreference(CLEAR_CACHE)).setSummary(
				getResources().getString(R.string.pref_sum_clear_cache, 
				StringUtil.humanReadable(cacheUsage, 1)));
	}
	
}
