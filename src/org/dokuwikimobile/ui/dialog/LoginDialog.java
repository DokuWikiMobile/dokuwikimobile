package org.dokuwikimobile.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.*;
import de.timroes.axmlrpc.XMLRPCException;
import de.timroes.axmlrpc.XMLRPCServerException;
import org.dokuwikimobile.R;
import org.dokuwikimobile.listener.LoginListener;
import org.dokuwikimobile.manager.DokuwikiManager;
import org.dokuwikimobile.manager.PasswordManager;
import org.dokuwikimobile.model.LoginData;
import org.dokuwikimobile.xmlrpc.DokuwikiXMLRPCClient.Canceler;

/**
 *
 * @author Tim Roes
 */
public class LoginDialog extends Dialog implements View.OnClickListener,
		CheckBox.OnCheckedChangeListener, LoginListener {

	private final int HIDE_PROGRESS = 0x01;
	private final int SHOW_TOAST = 0x02;
	private final int NOTIFY_LISTENER = 0x03;

	private DokuwikiManager manager;
	
	private ProgressDialog progress;
	private LoginDialogFinished listener;

	private Context context;

	final Handler handler = new Handler() {
	
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
				case HIDE_PROGRESS:
					if(progress != null)
						progress.hide();
					break;
				case SHOW_TOAST:
					Toast.makeText(context, context.getResources()
							.getString(msg.arg1), msg.arg2).show();
					break;
				case NOTIFY_LISTENER:
					listener.loginDialogFinished((LoginDialogFinished.FinishState)msg.obj);
					break;
			}
		}
		
	};

	public LoginDialog(Context context, LoginDialogFinished listener) {
		super(context);
		this.context = context;
		this.listener = listener;
	}

	@Override
	protected void onStart() {
		super.onStart();
		((EditText)findViewById(R.id.password)).getText().clear();
	}

	@Override
	public void onBackPressed() {
		handler.sendMessage(handler.obtainMessage(NOTIFY_LISTENER, 
				LoginDialogFinished.FinishState.CANCEL));
		super.onBackPressed();
	}

	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.logincancel:
				handler.sendMessage(handler.obtainMessage(NOTIFY_LISTENER, 
						LoginDialogFinished.FinishState.CANCEL));
				dismiss();
				break;
			case R.id.loginok:
				doLogin();
				break;
		}

	}

	public void onCheckedChanged(CompoundButton button, boolean state) {
		
		// Show warning whenever save login data gets unchecked
		if(button.getId() == R.id.savelogin && !state) {
			showWarningDialog();
		}
		
	}

	private void showWarningDialog() {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.notrecommended)
				.setMessage(R.string.nocachewithoutlogin);
			
		// Positive button will do nothing
		builder.setPositiveButton(android.R.string.yes, null);
		
		// Insert cancel button
		builder.setNegativeButton(android.R.string.no, new OnClickListener() {
			public void onClick(DialogInterface inter, int button) {
				((CheckBox)findViewById(R.id.savelogin)).setChecked(true);
			}
		});
		
		// Dont disable checkbox when user cancels dialog
		builder.setOnCancelListener(new OnCancelListener() {
			public void onCancel(DialogInterface inter) {
				((CheckBox)findViewById(R.id.savelogin)).setChecked(true);
			}
		});

		builder.create().show();

	}

	private void doLogin() {
		
		// Show waiting dialog
		progress = ProgressDialog.show(context, null, 
				context.getResources().getString(R.string.loggingin), true, false);
		LoginData loginData = new LoginData(
				((EditText) findViewById(R.id.username)).getEditableText().toString(), 
				((EditText) findViewById(R.id.password)).getEditableText().toString());
		manager.login(this, loginData, ((CheckBox)findViewById(R.id.savelogin)).isChecked());
	
	}

	public void onLogin(boolean succeeded, long id) {

		handler.sendMessage(handler.obtainMessage(HIDE_PROGRESS));

		if(succeeded) {
			handler.sendMessage(handler.obtainMessage(NOTIFY_LISTENER, 
					LoginDialogFinished.FinishState.SUCCESS));
			dismiss();
		} else {
			handler.sendMessage(handler.obtainMessage(SHOW_TOAST, R.string.loginwrong, Toast.LENGTH_SHORT));
		}
		
	}

	public void onError(XMLRPCException error, long id) {
		handler.sendMessage(handler.obtainMessage(HIDE_PROGRESS));
		handler.sendMessage(handler.obtainMessage(SHOW_TOAST, R.string.loginfailed, Toast.LENGTH_SHORT));
		System.err.println(error.toString());
	}

	public void onServerError(XMLRPCServerException error, long id) {
		handler.sendMessage(handler.obtainMessage(HIDE_PROGRESS));
		handler.sendMessage(handler.obtainMessage(SHOW_TOAST, R.string.loginfailed, Toast.LENGTH_SHORT));
		System.err.println(error.toString());
	}

	public void startLoading(Canceler cancel) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void endLoading() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public static interface LoginDialogFinished {

		public static enum FinishState { SUCCESS, CANCEL };
		public void loginDialogFinished(FinishState state);

	}

	public static class Builder {

		private final Context context;
		private final LoginDialogFinished listener;
		
		public Builder(Context ctx, LoginDialogFinished listener) {
			this.context = ctx;
			this.listener = listener;
		}

		public LoginDialog create() {

			// Create new dialog login
			final LoginDialog dialog = new LoginDialog(context, listener);
			// Add the xml view to the dialog
			dialog.setContentView(R.layout.login);
			// Set the title of the dialog
			dialog.setTitle(R.string.pleaselogin);

			// Add onClickListeners
			((Button)dialog.findViewById(R.id.logincancel)).setOnClickListener(dialog);
			((Button)dialog.findViewById(R.id.loginok)).setOnClickListener(dialog);
			((CheckBox)dialog.findViewById(R.id.savelogin)).setOnCheckedChangeListener(dialog);
			
			// Make dialog to fit screen width
			LayoutParams params = dialog.getWindow().getAttributes();
			params.width = LayoutParams.FILL_PARENT;
			dialog.getWindow().setAttributes(params);
			
			return dialog;

		}

	}
	
}
