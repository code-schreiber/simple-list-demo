package com.sebasguillen.mobile.android.simplelistdemo.frontend;

import android.R.style;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TableLayout;

import com.sebasguillen.mobile.android.simplelistdemo.R;


/**
 * This class does most of the work for our {@link android.widget.PopupWindow} so it's easier to use.
 * @author Sebastian Guillen
 */
public class MyPopup extends PopupWindow {

	private static final int TEXT_COLOR = Color.WHITE;
	private static final int TEXT_TYPE = Typeface.BOLD;
	private static final int BUTTON_BACKGROUD = R.drawable.popupbutton;
	private static final int POPUP_BACKGROUND = android.R.drawable.dialog_holo_light_frame;

	private TableLayout layout;
	private Activity actvty;
	private View anchor;

	public MyPopup(Context c) {
		super(c);
	}

	public MyPopup(View anchor, Activity activity) {
		super(anchor.getContext());
		this.anchor = anchor;
		this.actvty = activity;

		this.setWidth(android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		this.setHeight(android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		/*
		 * Remove background imposed by super (constructor) popup window does
		 * not respond to onTouch or onKey events unless it has a background
		 * that != null
		 */
		this.setBackgroundDrawable(new BitmapDrawable());
		this.setAnimationStyle(style.Animation_Toast);
		this.setFocusable(true);
		this.setOutsideTouchable(true);
		setTouchInterceptor();
		initPopupWindow();
	}

	private void setTouchInterceptor() {
		// When User changed his mind and touches outside
		this.setTouchInterceptor(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					// when a touch even happens outside of the window make the window go away
					MyPopup.this.dismiss();
					return true;
				}
				return false;
			}
		});
	}

	private void initPopupWindow() {
		layout = new TableLayout(this.actvty);
		layout.setBackgroundResource(POPUP_BACKGROUND);
		this.setContentView(layout);
	}

	public void showPopup() {
		int[] whereToBeShown = getXandY(this.anchor, this.actvty);
		// Launch!
		this.showAtLocation(this.anchor, Gravity.NO_GRAVITY,
				whereToBeShown[0],whereToBeShown[1]);
	}

	/**
	 * Create a new button on the popup window
	 */
	public void addButton(Button button) {
		final Button b = button;
		b.setBackgroundResource(BUTTON_BACKGROUD);
		b.setTextColor(TEXT_COLOR);
		b.setTextSize(TypedValue.COMPLEX_UNIT_FRACTION_PARENT, 17);
		b.setTypeface(Typeface.defaultFromStyle(TEXT_TYPE), TEXT_TYPE);
		layout.addView(b, layout.getChildCount());
	}

	/**
	 * Get position where popup must be shown (Align centers)
	 */
	private int[] getXandY(View parent, Activity a) {
		final int two = 2;
		int[] locationOfParent = new int[two];
		parent.getLocationInWindow(locationOfParent);
		// Measure before calling getMeasured___()
		layout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
		int x = (int) ((locationOfParent[0] + (float) parent.getMeasuredWidth() / two) - ((float) layout
				.getMeasuredWidth() / two));
		int y = (int) ((locationOfParent[1] + (float) parent
				.getMeasuredHeight() / two) - ((float) layout.getMeasuredHeight() / two));
		Rect rectgle = new Rect();
		Window window = a.getWindow();
		window.getDecorView().getWindowVisibleDisplayFrame(rectgle);
		int statusBarHeight = rectgle.top;
		if (y < statusBarHeight) {
			y = statusBarHeight;
		}

		int[] centerOfParent = new int[two];
		centerOfParent[0] = x;
		centerOfParent[1] = y;
		return centerOfParent;
	}

}
