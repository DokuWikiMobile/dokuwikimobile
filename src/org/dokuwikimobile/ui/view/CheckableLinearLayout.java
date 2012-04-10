package org.dokuwikimobile.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.LinearLayout;

/**
 *
 * @author Tim Roes <mail@timroes.de>
 */
public class CheckableLinearLayout extends LinearLayout implements Checkable {

	private boolean checked;

	public CheckableLinearLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public CheckableLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CheckableLinearLayout(Context context) {
		super(context);
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
		if(checked) {
			this.setBackgroundColor(0xFFFF0000);
		} else {
			this.setBackgroundDrawable(null);
		}
		for(int i = 0; i < this.getChildCount(); i++) {
			View v = this.getChildAt(i);
			if(v instanceof Checkable) {
				((Checkable)v).setChecked(checked);
			}
		}
	}

	public boolean isChecked() {
		return checked;
	}

	public void toggle() {
		setChecked(!checked);
	}

}
