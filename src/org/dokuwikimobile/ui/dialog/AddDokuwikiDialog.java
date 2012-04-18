package org.dokuwikimobile.ui.dialog;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockDialogFragment;
import com.google.zxing.integration.android.IntentIntegrator;
import java.net.MalformedURLException;
import org.dokuwikimobile.Dokuwiki;
import org.dokuwikimobile.DokuwikiApplication;
import org.dokuwikimobile.R;
import org.dokuwikimobile.listener.DokuwikiCreationListener;
import org.dokuwikimobile.xmlrpc.DokuwikiXMLRPCClient.Canceler;
import org.dokuwikimobile.xmlrpc.ErrorCode;

/**
 *
 * @author Tim Roes <mail@timroes.de>
 */
public class AddDokuwikiDialog extends SherlockDialogFragment implements DokuwikiCreationListener {

	public static AddDokuwikiDialog newInstance(AddListener listener) {
		AddDokuwikiDialog dialog = new AddDokuwikiDialog();
		dialog.listener = listener;
		return dialog;
	}

	private AddListener listener;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(STYLE_NORMAL, 0);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		PackageManager packageManager = getDialog().getContext().getPackageManager();

		// If device has a camera, we can show the opportunity to scan the url via QR code
		if(packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
			return createChooseView(inflater);
		} else {
			return createInputView(inflater, null);
		}
		
	}

	private View createChooseView(LayoutInflater inflater) {

		// Set title
		getDialog().setTitle(R.string.add_wiki_choose_method_title);
		// Inflate design
		View v = inflater.inflate(R.layout.dialog_adddokuwiki1, null, false);
		
		((Button)v.findViewById(R.id.add_by_url)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showInputScreen(null);
			}
		});

		((Button)v.findViewById(R.id.add_by_photo)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startBarcodeScanner();
			}
		});

		return v;
		
	}

	public void showChooseScreen() {
		getDialog().setContentView(createChooseView(getDialog().getLayoutInflater()));
	}

	private View createInputView(LayoutInflater inflater, String url) {
		
		getDialog().setTitle(R.string.add_wiki_dialog_title);
		View v = inflater.inflate(R.layout.dialog_adddokuwiki2, null, false);

		if(url != null) {
			((TextView)v.findViewById(R.id.wiki_url)).setText(url);
		}

		final EditText urlField = (EditText)v.findViewById(R.id.wiki_url);
		urlField.setText("http://wiki-url.com/lib/exe/xmlrpc.php");
		urlField.setSelection(7, 19);

		((Button)v.findViewById(R.id.add)).setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				addDokuwiki(urlField.getText().toString());
				
			}

		});
		
		return v;

	}

	public void showInputScreen(String url) {
		getDialog().setContentView(createInputView(getDialog().getLayoutInflater(), url));
	}

	private void startBarcodeScanner() {

		IntentIntegrator integrator = new IntentIntegrator(getActivity());
		
		// Set string resources for question dialog if barcode scanner isn't installed
		integrator.setButtonNoByID(android.R.string.no);
		integrator.setButtonYesByID(android.R.string.yes);
		integrator.setTitleByID(R.string.install_barcode_title);
		integrator.setMessageByID(R.string.install_barcode_message);
		
		// Start scanner for QR codes
		integrator.initiateScan(IntentIntegrator.QR_CODE_TYPES);

	}

	private void addDokuwiki(String wikiUrl) {

		try {
			Dokuwiki.Creator creator = new Dokuwiki.Creator();
			creator.create(wikiUrl, this);
		} catch (MalformedURLException ex) {
			showError(R.string.invalid_url_title, R.string.invalid_url_msg);
			Log.e(DokuwikiApplication.LOGGER_NAME, "Cannot add dokuwiki " + wikiUrl + ". This isn't a valid url.");
		}

	}

	private void showError(int title, int msg) {
		SherlockDialogFragment dialog = ErrorDialog.newInstance(title, msg);
		dialog.show(getFragmentManager(), "dialog");
	}

	public void onDokuwikiCreated(final Dokuwiki dokuwiki) {
		listener.onDokuwikiAdded(dokuwiki);
		dismiss();
	}

	public void onStartLoading(Canceler cancel, long id) {
		Log.d(DokuwikiApplication.LOGGER_NAME, "Start Loading");
	}

	public void onEndLoading(long id) {
		Log.d(DokuwikiApplication.LOGGER_NAME, "End loading");
	}

	public void onError(ErrorCode error, long id) {
		
		int title, msg;

		switch(error) {
			default:
			case UNCATEGORIZED:
				title = R.string.not_wiki_url_title;
				msg = R.string.not_wiki_url_msg;
				break;
			case VERSION_ERROR:
				title = R.string.version_error_title;
				msg = R.string.version_error_msg;
				break;
		}

		showError(title, msg);
		
	}

	public static interface AddListener {
		
		public void onDokuwikiAdded(Dokuwiki dokuwiki);
				
	}

}
