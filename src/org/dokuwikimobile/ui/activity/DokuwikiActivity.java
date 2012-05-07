package org.dokuwikimobile.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import org.dokuwikimobile.Dokuwiki;
import org.dokuwikimobile.DokuwikiApplication;
import org.dokuwikimobile.R;
import org.dokuwikimobile.listener.CancelableListener;
import org.dokuwikimobile.manager.DokuwikiManager;
import org.dokuwikimobile.ui.dialog.LoginDialog;
import org.dokuwikimobile.xmlrpc.DokuwikiXMLRPCClient.Canceler;
import org.dokuwikimobile.xmlrpc.ErrorCode;

/**
 * The DokuwikiActivity is the parent class for all all activities, that should
 * be available for every wiki. It takes care of reading the currently selected
 * wiki from the intent and pass it to the next activity.
 * 
 * @author Tim Roes <mail@timroes.de>
 */
public abstract class DokuwikiActivity extends SherlockFragmentActivity 
		implements LoginDialog.LoginDialogFinished, CancelableListener {

	public final static String WIKI_HASH = "wiki_hash";

	protected final static int DIALOG_LOGIN = 0;
	
	protected DokuwikiManager manager;

	protected Handler handler;

	/**
	 * This method gets called when the activity is about to be created.
	 * The dokuwiki for this activity is read from the intent. If it couldn't
	 * be read, finish the activity. We cannot do anything without the knowledge
	 * of the current dokuwiki.
	 * 
	 * @param savedInstanceState The bundle with saved informations.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Set the handler for the UI thread
		handler = new Handler();

		// Request the feature to show an indeterminate process in the ActionBar
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		if(!readWikiFromIntent(getIntent())) {
			finish();
		}

		// Enable the up button for the actionbar
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

	}

	/**
	 * This method will be called, when a new intent is send to the activity.
	 * The dokuwiki for this activity will be read from the intent.
	 * 
	 * @param intent 
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		readWikiFromIntent(intent);
	}

	/**
	 * Read the wiki that should be used for this activity from the given intent.
	 * This will read the hash of the wiki from the intents extra and initialize
	 * the DokuwikiManager for this dokuwiki.
	 * 
	 * @param intent The intent to read the dokuwiki from.
	 * @return Whether the dokuwiki could be read from the intent.
	 */
	private boolean readWikiFromIntent(Intent intent) {

		String hash = intent.getStringExtra(WIKI_HASH);
		Dokuwiki dokuwiki = Dokuwiki.get(hash);
		
		if(dokuwiki == null) {
			// If dokuwiki doesn't exist, just close activity and log the error.
			Log.e(DokuwikiApplication.LOGGER_NAME, "The hash in the intent (" 
					+ hash + ") isn't stored in the dokuwiki list");
			return false;
		}

		// Initialize manager for the dokuwiki from the intent
		manager = DokuwikiManager.get(dokuwiki);

		return true;

	}

	/**
	 * Replace all content of the current activity with a big loading spinner.
	 * This should be used while data is loaded and nothing is yet available 
	 * to present to the user. As soon as some data is available, the spinner
	 * in the ActionBar should be used to indicate loading.
	 */
	protected void loadingScreen() {
		
		setContentView(R.layout.loading_screen);
		ProgressBar spinner = (ProgressBar)findViewById(R.id.spinner);
		AnimationDrawable anim = (AnimationDrawable)getResources().getDrawable(R.drawable.spinner);
		spinner.setIndeterminateDrawable(anim);
		anim.start();
		
	}

	/**
	 * Replace all content of the current activity with a big message.
	 * This can be used to show warnings or errors, while no other data is available
	 * to present to the user.
	 * 
	 * @param message The resource id of the message to show.
	 */
	protected void messageScreen(int message) {
		messageScreen(getResources().getString(message));
	}

	/**
	 * Replace all content of the current activity with a big message.
	 * This can be used to show warnings or errors, while no other data is available
	 * to present to the user.
	 * 
	 * @param message The message to show.
	 */
	protected void messageScreen(String message) {
		messageScreen(message, null, null, null);
	}

	/**
	 * Replace all content of the current activity with a big message.
	 * This can be used to show warnings or errors, while no other data is available
	 * to present to the user.
	 * 
	 * @param message The resource id of the message to show.
	 * @param icon The resource id of the icon to show.
	 */
	protected void messageScreen(int message, int icon) {
		messageScreen(getResources().getString(message), 
				getResources().getDrawable(icon), null, null);
	}

	/**
	 * Replace all content of the current activity with a big message and a button below.
	 * This can be used to show warnings or errors, while no other data is
	 * available to present to the user.
	 * 
	 * @param message The resource id of the message to show.
	 * @param buttonLabel The resource id of the button label.
	 * @param clickListener A listener to be notified about the button click.
	 */
	protected void messageScreen(int message, int buttonLabel, View.OnClickListener clickListener) {
		messageScreen(getResources().getString(message),
				null, getResources().getString(buttonLabel), clickListener);
	}

	/**
	 * Replace all content of the current activity with a big message and a button below.
	 * This can be used to show warnings or errors, while no other data is
	 * available to present to the user.
	 * 
	 * @param message The resource id of the message to show.
	 * @param icon The resource id of a drawable to use as icon.
	 * @param buttonLabel The resource id of the button label.
	 * @param clickListener A listener to be notified about the button click.
	 */
	protected void messageScreen(int message, int icon, int buttonLabel, View.OnClickListener clickListener) {
		messageScreen(getResources().getString(message), 
				getResources().getDrawable(icon),
				getResources().getString(buttonLabel), clickListener);
	}

	/**
	 * Replace all content of the current activity with a big message and a button below.
	 * This can be used to show warnings or errors, while no other data is
	 * available to present to the user.
	 * 
	 * @param message The message to show.
	 * @param buttonLabel The button label.
	 * @param clickListener A listener to be notified about the button click.
	 */
	protected void messageScreen(String message, Drawable icon, String buttonLabel, View.OnClickListener clickListener) {

		int layoutId = (icon == null) ? R.layout.message_screen : R.layout.message_screen_icon;
		setContentView(layoutId);

		TextView messageView = (TextView)findViewById(R.id.message);
		messageView.setText(message);

		Button button = (Button)findViewById(R.id.button);
		if(buttonLabel == null || clickListener == null) {
			button.setVisibility(View.GONE);
		} else {
			button.setText(buttonLabel);
			button.setOnClickListener(clickListener);
		}

		if(icon != null) {
			ImageView imageView = (ImageView)findViewById(R.id.icon);
			imageView.setImageDrawable(icon);
		}
		
	}

	/**
	 * Will be called when an options item is selected. In this class the method
	 * will only check for a click on the UP icon in the ActionBar and go up to 
	 * the ChooserActivity.
	 * 
	 * @param item The item, the user clicked.
	 * @return Whether the method handled the click.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch(item.getItemId()) {
			case android.R.id.home:
				Intent intent = new Intent(this, ChooserActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}

	}

	/**
	 * Will be called when the application starts networking.
	 * This method will show the indeterminate loading spinner in the
	 * ActionBar. If this isn't wanted, don't call the super method in 
	 * the activity.
	 * 
	 * @param cancel The canceler to cancel the call.
	 * @param id The id of the call.
	 */
	public void onStartLoading(Canceler cancel, long id) {

		handler.post(new Runnable() {
			public void run() {
				setSupportProgressBarIndeterminateVisibility(true);
			}
		});

	}

	/**
	 * Will be called when the networking has been stopped.
	 * This will hide the indeterminate loading spinner in the ActionBar.
	 * 
	 * @param id The id of the call.
	 */
	public void onEndLoading(long id) {

		handler.post(new Runnable() {
			public void run() {
				setSupportProgressBarIndeterminateVisibility(false);
			}
		});
		
	}

	/**
	 * This will be called when an error occurs.
	 * The method doesn't do anything. Overwrite it in an activity, for
	 * error handling.
	 * 
	 * @param error The error code.
	 * @param id The id of the call.
	 */
	public void onError(ErrorCode error, long id) { }

	@Override
	public void startActivity(Intent intent) {
		intent.putExtra(WIKI_HASH, manager.getDokuwiki().getMd5hash());
		super.startActivity(intent);
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		intent.putExtra(WIKI_HASH, manager.getDokuwiki().getMd5hash());
		super.startActivityForResult(intent, requestCode);
	}

	@Override
	public void startActivityFromChild(Activity child, Intent intent, int requestCode) {
		intent.putExtra(WIKI_HASH, manager.getDokuwiki().getMd5hash());
		super.startActivityFromChild(child, intent, requestCode);
	}

	@Override
	public boolean startActivityIfNeeded(Intent intent, int requestCode) {
		intent.putExtra(WIKI_HASH, manager.getDokuwiki().getMd5hash());
		return super.startActivityIfNeeded(intent, requestCode);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id) {
			case DIALOG_LOGIN:
				LoginDialog.Builder builder = new LoginDialog.Builder(this,this);
				return builder.create();
			default:
				return super.onCreateDialog(id);
		}
	}

	public void loginDialogFinished(FinishState state) { }

}