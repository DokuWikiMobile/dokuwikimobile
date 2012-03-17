package org.dokuwikimobile.android.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import org.dokuwikimobile.android.R;

/**
 *
 * @author Tim Roes
 */
public class MessageView extends LinearLayout {

	public static enum Type { SUCCESS, WARNING, ERROR };

	private Paint gradientPaint;
	private Paint linePaint;

	private ProgressBar progress;
	private TextView text;

	private int color1;
	private int color2;

	public MessageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		build();
	}

	public MessageView(Context context) {
		super(context);
		build();
	}

	private void build() {
		setWillNotDraw(false);
		// Initialize child elements
		text = new TextView(getContext());
		progress = new ProgressBar(getContext());
		progress.setIndeterminate(true);
		// Set orientation to horicontal
		setOrientation(LinearLayout.HORIZONTAL);
		addViewInLayout(text, 0, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1.0f));
		addViewInLayout(progress, 1, new LayoutParams(0, 0, 0.0f));
		// Set Paint for bottom line
		linePaint = new Paint();
		linePaint.setColor(0xFFAAAAAA);
		linePaint.setStrokeWidth(2);
		// Create background paint
		gradientPaint = new Paint();
		// Set padding of text
		setPadding(4, 4, 4, 5);
		text.setPadding(0, 0, 4, 0);
	}

	public void setMessage(Type type, int resId) {
		setMessage(type, getResources().getString(resId));
	}
	
	public void setMessage(Type type, String message) {
		// Set message text
		text.setText(message);
		switch(type) {
			case SUCCESS:
				color1 = getResources().getColor(R.color.success_color1);
				color2 = getResources().getColor(R.color.success_color2);
				text.setTextColor(getResources().getColor(R.color.success_text_color));
				break;
			case WARNING:
				color1 = getResources().getColor(R.color.warning_color1);
				color2 = getResources().getColor(R.color.warning_color2);
				text.setTextColor(getResources().getColor(R.color.warning_text_color));
				break;
			case ERROR:
				color1 = getResources().getColor(R.color.error_color1);
				color2 = getResources().getColor(R.color.error_color2);
				text.setTextColor(getResources().getColor(R.color.error_text_color));
				break;
		}

	}

	public void clearMessage() {
		text.setText("");
	}

	public void showLoading() {
		progress.setVisibility(VISIBLE);
	}

	public void hideLoading() {
		progress.setVisibility(INVISIBLE);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if(text.getText().length() <= 0) 
			return;

		// Draw background gradient
		canvas.drawPaint(gradientPaint);
		// Draw bottom line
		canvas.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1, linePaint);

		super.onDraw(canvas);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		progress.getLayoutParams().height = text.getHeight();
		progress.getLayoutParams().width = text.getHeight();
		gradientPaint.setShader(new LinearGradient(0, 0, 0, getHeight(), 
				color1, color2, Shader.TileMode.REPEAT));
	}

	public void fold() {
		text.setMaxLines(1);
	}

	public void unfold() {
		text.setMaxLines(Integer.MAX_VALUE);
	}

}