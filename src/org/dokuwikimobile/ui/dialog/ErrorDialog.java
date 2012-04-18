package org.dokuwikimobile.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import com.actionbarsherlock.app.SherlockDialogFragment;
import org.dokuwikimobile.R;

/**
 *
 * @author Tim Roes <mail@timroes.de>
 */
public class ErrorDialog extends SherlockDialogFragment {

	private static final String TITLE = "title";
	private static final String MESSAGE = "message";
	
	public static ErrorDialog newInstance(int title, int msg) {
		
		ErrorDialog dialog = new ErrorDialog();
		
		Bundle args = new Bundle();
		args.putInt(TITLE, title);
		args.putInt(MESSAGE, msg);

		dialog.setArguments(args);
		
		return dialog;
		
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		int title = getArguments().getInt(TITLE);
		int message = getArguments().getInt(MESSAGE);

		return new AlertDialog.Builder(getActivity())
				.setIcon(R.drawable.ic_dialog_error)
				.setTitle(title)
				.setMessage(message)
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						dismiss();
					}

				})
				.create();
		
	}

}
