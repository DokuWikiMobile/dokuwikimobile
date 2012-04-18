package org.dokuwikimobile.ui.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import org.dokuwikimobile.R;

/**
 * The ProgressDialgo shows a simple indeterminated progress dialog.
 * This class uses the android.app.ProgressDialog. By default this ProgressDialog
 * is not cancelable. Use the setOnCancelListener method to make it cancelable.
 * 
 * @author Tim Roes <mail@timroes.de>
 */
public class ProgressDialog {

	private android.app.ProgressDialog dialog;
	private Handler handler;

	/**
	 * Create a new ProgressDialog in the given context.
	 * 
	 * @param context An android context.
	 */
	public ProgressDialog(Context context) {
		handler = new Handler();
		this.dialog = new android.app.ProgressDialog(context);
		dialog.setMessage(context.getString(R.string.loading));
		dialog.setIndeterminate(true);
		dialog.setCancelable(false);
	}

	/**
	 * Show the progress dialog.
	 */
	public void show() {
		handler.post(new Runnable() {
			public void run() {
				dialog.show();
			}
		});
	}

	/**
	 * Set the message shown in the progress dialog.
	 * 
	 * @param resource A string resource id of the message to show.
	 */
	public void setMessage(int resource) {
		setMessage(dialog.getContext().getResources().getString(resource));
	}

	/**
	 * Set the message shown in the progress dialog.
	 * 
	 * @param message The message to show.
	 */
	public void setMessage(final CharSequence message) {
		handler.post(new Runnable() {
			public void run() {
				dialog.setMessage(message);
			}
		});
	}

	/**
	 * Dismisses the progress dialog.
	 */
	public void dismiss() {
		handler.post(new Runnable() {
			public void run() {
				dialog.dismiss();
			}
		});
	}

	/**
	 * Set the OnCacnelListener for this dialog. This canceler will be informed,
	 * if the dialog will be canceled (e.g. by pressing the back button).
	 * You must call this to make the dialog cancelable.
	 * 
	 * @param listener The listener, that should be informed about the canceling.
	 */
	public void setOnCancelListener(DialogInterface.OnCancelListener listener) {
		dialog.setOnCancelListener(listener);
		dialog.setCancelable(true);
	}
	
}