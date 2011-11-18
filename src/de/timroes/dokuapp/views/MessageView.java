package de.timroes.dokuapp.views;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;
import de.timroes.dokuapp.R;

/**
 *
 * @author Tim Roes
 */
public class MessageView extends TextView {

	public static enum Type { SUCCESS, WARNING, ERROR };
	
	public MessageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MessageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MessageView(Context context) {
		super(context);
	}

	public void setMessage(Type type, String message) {
		setText(message);
		switch(type) {
			case SUCCESS:
				setBackgroundResource(R.drawable.message_success);
				setTextColor(getResources().getColor(R.color.success_text_color));
				break;
			case WARNING:
				setBackgroundResource(R.drawable.message_warning);
				setTextColor(getResources().getColor(R.color.warning_text_color));
				break;
			case ERROR:
				setBackgroundResource(R.drawable.message_error);
				setTextColor(getResources().getColor(R.color.error_text_color));
				break;
		}
	}

	public void clearMessage() {
		setText("");
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		if(getText().length() <= 0) 
			return;

		super.onDraw(canvas);
	}

	

}
