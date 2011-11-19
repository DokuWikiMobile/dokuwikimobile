package de.timroes.dokuapp.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;
import de.timroes.dokuapp.R;

/**
 *
 * @author Tim Roes
 */
public class MessageView extends TextView {

	public static enum Type { SUCCESS, WARNING, ERROR };

	private Paint gradientPaint;
	private Paint linePaint;

	private DokuwikiWebView web;
	
	public MessageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		build();
	}

	public MessageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		build();
	}

	public MessageView(Context context) {
		super(context);
		build();
	}

	public void setWebView(DokuwikiWebView webview) {
		this.web = webview;
	}

	private void build() {
		// Set Paint for bottom line
		linePaint = new Paint();
		linePaint.setColor(0xFFAAAAAA);
		linePaint.setStrokeWidth(2);
		// Create background paint
		gradientPaint = new Paint();
		// Set padding of text
		setPadding(4, 4, 4, 4);
	}

	public void setMessage(Type type, String message) {
		// Set message text
		setText(message);
		int color1 = 0, color2 = 0;
		switch(type) {
			case SUCCESS:
				color1 = getResources().getColor(R.color.success_color1);
				color2 = getResources().getColor(R.color.success_color2);
				setTextColor(getResources().getColor(R.color.success_text_color));
				break;
			case WARNING:
				color1 = getResources().getColor(R.color.warning_color1);
				color2 = getResources().getColor(R.color.warning_color2);
				setTextColor(getResources().getColor(R.color.warning_text_color));
				break;
			case ERROR:
				color1 = getResources().getColor(R.color.error_color1);
				color2 = getResources().getColor(R.color.error_color2);
				setTextColor(getResources().getColor(R.color.error_text_color));
				break;
		}

		gradientPaint.setShader(new LinearGradient(0, 0, 0, getHeight(), 
				color1, color2, Shader.TileMode.CLAMP));
		
	}

	public void clearMessage() {
		setText("");
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		if(getText().length() <= 0) 
			return;
		
		canvas.drawPaint(gradientPaint);
		canvas.drawLine(0, canvas.getClipBounds().bottom - 1, 
				canvas.getClipBounds().right, canvas.getClipBounds().bottom - 1, 
				linePaint);

		// Draw normal TextView
		super.onDraw(canvas);
		
	}

	public void fold() {
		this.setMaxLines(1);
	}

	public void unfold() {
		this.setMaxLines(Integer.MAX_VALUE);
	}

}