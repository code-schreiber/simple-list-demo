package com.sebasguillen.mobile.android.simplelistdemo.frontend.splash;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sebasguillen.mobile.android.simplelistdemo.R;
import com.sebasguillen.mobile.android.simplelistdemo.frontend.home.HomeActivity;

/**
 * The splash screen
 * A dummy layout for when the app starts
 * should be used to load stuff
 * @author Sebastian Guillen
 */
public class SplashActivity extends Activity {

	private static final String TAG = SplashActivity.class.getSimpleName();
	private static final int SPLASH_TIME_IN_SECONDS = 1;
	private Thread splashThread;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_splash);
		appendVersion();
		setLayoutListener((RelativeLayout) findViewById(R.id.SplashLayout));
		launchSplashThread();
	}

	@Override
	public void onBackPressed() {
		splashThread.interrupt();
		super.onBackPressed();
	}

	private void appendVersion() {
		String versionCode = getVersionCode(getBaseContext());
		String format = getString(R.string.Version_Format);
		String text = String.format(format, versionCode);
		text += "\n";
		TextView tv = (TextView) findViewById(R.id.SplashText);
		tv.setText(text + tv.getText());
	}

	private String getVersionCode(Context c) {
		String versionCode = "";
		try {
			PackageInfo pi = c.getPackageManager().getPackageInfo(c.getPackageName(), 0);
			versionCode = pi.versionName;
		} catch (NameNotFoundException e) {
			Log.e(TAG, e.getMessage());
		}
		return versionCode;
	}

	private void setLayoutListener(RelativeLayout splashLayout) {
		splashLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startHomeActivity();
			}
		});
	}

	/**
	 * Launch thread for displaying the SplashScreen
	 */
	private void launchSplashThread() {
		final Handler handler = getHandler();
		this.splashThread = new Thread() {
			@Override
			public void run() {
				try {
					int oneSecond = 1000;
					sleep(SPLASH_TIME_IN_SECONDS * oneSecond);
					Message message = handler.obtainMessage();
					handler.sendMessage(message);
				} catch (InterruptedException e) {
					Log.d(TAG, "Splash screen skipped "+e.getMessage());
				}
			}
		};
		this.splashThread.start();
	}

	private Handler getHandler (){
		return new Handler(){
			@Override
			public void handleMessage(Message message) {
				startHomeActivity();
			}
		};
	}

	private void startHomeActivity() {
		// Interrupt so startHomeActivity() isn't called again
		splashThread.interrupt();
		finish();
		Intent intent = new Intent();
		intent.setClass(SplashActivity.this, HomeActivity.class);
		SplashActivity.this.startActivity(intent);
	}

}
